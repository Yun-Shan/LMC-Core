package org.yunshanmc.lmc.core.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.config.bukkitcfg.file.FileConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.util.PlatformUtils;
import org.yunshanmc.lmc.core.util.ReflectUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yun-Shan
 */
public class OrmLiteDatabase extends BaseDatabase {

    static {
        CreateLoggerInterceptor.setup();
    }

    private ConnectionSource connectionSource;
    private final boolean pooled;

    public OrmLiteDatabase(LMCPlugin plugin, FileConfiguration pluginConfig, MessageSender messageSender) {
        this(plugin, pluginConfig, messageSender, true);
    }

    public OrmLiteDatabase(LMCPlugin plugin, FileConfiguration pluginConfig, MessageSender messageSender, boolean pooled) {
        super(plugin, pluginConfig, messageSender);
        this.pooled = pooled;

        if (this.dbConfig != null) {
            String levelStr = this.dbConfig.getString("logLevel", "");
            Log.Level level;
            try {
                level = Log.Level.valueOf(levelStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                level = Log.Level.ERROR;
            }
            CreateLoggerInterceptor.setLevel(plugin.getName(), level);
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            if (this.connectionSource != null) {
                this.connectionSource.close();
            }
        } catch (IOException e) {
            ExceptionHandler.handle(e);
        } finally {
            this.connectionSource = null;
        }
    }

    @Override
    protected boolean connect(String jdbcUrl) throws SQLException {
        if (this.pooled) {
            this.connectionSource = new JdbcPooledConnectionSource(jdbcUrl);
        } else {
            this.connectionSource = new JdbcConnectionSource(jdbcUrl);
        }

        // 连接测试
        DatabaseConnection conn = this.connectionSource.getReadOnlyConnection("");

        conn.executeStatement(this.dbType.getTestSQL(), DatabaseConnection.DEFAULT_RESULT_FLAGS);
        this.connectionSource.releaseConnection(conn);
        return true;
    }

    public ConnectionSource getConnectionSource() {
        this.checkNotClosed();
        return this.connectionSource;
    }

    static class CreateLoggerInterceptor implements MethodInterceptor {

        private static final Map<String, Log.Level> LEVELS = new HashMap<>();

        public static void setLevel(String plugin, Log.Level level) {
            LEVELS.put(plugin, level);
        }

        private LoggerFactory.LogType logType;

        static void setup() {
            CreateLoggerInterceptor interceptor = new CreateLoggerInterceptor();

            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(LoggerFactory.LogType.class);
            enhancer.setCallback(interceptor);
            LoggerFactory.getLogger(CreateLoggerInterceptor.class);
            try {
                Field f = LoggerFactory.class.getDeclaredField("logType");
                f.setAccessible(true);
                interceptor.logType = (LoggerFactory.LogType) f.get(null);
                f.set(null, enhancer.create(new Class[]{ String.class, int.class, String.class, String.class,
                                                         Class.forName(LoggerFactory.class.getName() + "$1") },
                                            new Object[]{ "LMC_Core_CreateLoggerInterceptor",
                                                          LoggerFactory.LogType.values().length,
                                                          "LMC-Core_CreateLoggerInterceptor",
                                                          "LMC-Core_CreateLoggerInterceptor",
                                                          null }));
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                ExceptionHandler.handle(e);
            }
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if ("createLog".equals(method.getName())) {
                Object logger = method.invoke(this.logType, args);
                InvocationHandler handler = (proxyProxy, proxyMethod, proxyArgs) -> {
                    if ("log".equals(proxyMethod.getName())) {
                        String pluginName = null;
                        MessageSender sender = null;
                        Object plugin = PlatformUtils.traceFirstPlugin(ReflectUtils.captureStackTrace(), true);
                        if (plugin instanceof LMCPlugin) {
                            pluginName = ((LMCPlugin) plugin).getName();
                            sender = ((LMCPlugin) plugin).getMessageManager().getMessageSender();
                        }

                        if (sender != null) {
                            Log.Level current = (Log.Level) proxyArgs[0];
                            if (LEVELS.getOrDefault(pluginName, Log.Level.INFO).isEnabled(current)) {
                                sender.infoConsole("database.OrmLiteLog." + current.name().toLowerCase(), proxyArgs[1]);
                            }
                            if (proxyArgs.length == 3) {
                                ExceptionHandler.handle((Throwable) proxyArgs[2]);
                            }
                            return null;
                        }
                    }
                    return proxyMethod.invoke(logger, proxyArgs);
                };
                return Proxy.newProxyInstance(CreateLoggerInterceptor.class.getClassLoader(), new Class[]{ Log.class },
                                              handler);
            }
            return method.invoke(this.logType, args);
        }
    }
}
