package org.yunshanmc.lmc.core.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.bukkit.LMCBukkitPlugin;
import org.yunshanmc.lmc.core.bungee.LMCBungeeCordPlugin;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.utils.BukkitUtils;
import org.yunshanmc.lmc.core.utils.BungeeUtils;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class OrmLiteDatabase extends Database {

    static {
        CreateLoggerInterceptor.setup();
    }

    private ConnectionSource connectionSource;

    public OrmLiteDatabase(LMCPlugin plugin, FileConfiguration pluginConfig, MessageSender messageSender) {
        super(pluginConfig, messageSender);

        String levelStr = this.dbConfig.getString("logLevel", "");
        Log.Level level;
        try {
            level = Log.Level.valueOf(levelStr);
        } catch (IllegalArgumentException ex) {
            messageSender.errorConsole("database.UnknownOrmLiteLogLevel", levelStr);
            level = Log.Level.INFO;
        }
        CreateLoggerInterceptor.setLevel(plugin.getName(), level);

    }

    @Override
    protected boolean connect(String jdbcUrl) throws SQLException {
        this.connectionSource = new JdbcConnectionSource(jdbcUrl);

        // 连接测试
        DatabaseConnection conn = this.connectionSource.getReadWriteConnection("");
        // TODO: 测试连接语句 各类型数据库适配
        // MySQL
        // conn.executeStatement("SELECT 1;", DatabaseConnection.DEFAULT_RESULT_FLAGS);
        // this.connectionSource.releaseConnection(conn);
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
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (method.getName().equals("createLog")) {
                Object logger = method.invoke(this.logType, args);
                InvocationHandler handler = (proxyProxy, proxyMethod, proxyArgs) -> {
                    if (proxyMethod.getName().equals("log")) {
                        String pluginName = null;
                        MessageSender sender = null;
                        if (PlatformUtils.isBukkit()) {
                            Plugin plugin = BukkitUtils.traceFirstPlugin(new Throwable().getStackTrace());
                            if (plugin instanceof LMCBukkitPlugin) {
                                pluginName = plugin.getName();
                                sender = ((LMCBukkitPlugin) plugin).getMessageManager().getMessageSender();
                            }
                        } else if (PlatformUtils.isBungeeCord()) {
                            net.md_5.bungee.api.plugin.Plugin plugin = BungeeUtils.traceFirstPlugin(
                                    new Throwable().getStackTrace());
                            if (plugin instanceof LMCBungeeCordPlugin) {
                                pluginName = ((LMCBungeeCordPlugin) plugin).getName();
                                sender = ((LMCBungeeCordPlugin) plugin).getMessageManager().getMessageSender();
                            }
                        }

                        if (sender != null) {
                            Log.Level current = (Log.Level) proxyArgs[0];
                            if (LEVELS.getOrDefault(pluginName, Log.Level.INFO).isEnabled(current)) {
                                sender.infoConsole("database.OrmLiteLog." + current.name().toLowerCase(), proxyArgs[1]);
                            }
                            if (proxyArgs.length == 3) {
                                ExceptionHandler.handle((Throwable) proxyArgs[2]);
                            }
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
