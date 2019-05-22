package org.yunshanmc.lmc.core.database;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.config.bukkitcfg.ConfigurationSection;
import org.yunshanmc.lmc.core.config.bukkitcfg.file.FileConfiguration;
import org.yunshanmc.lmc.core.database.type.AbstractDatabaseType;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.sql.SQLException;

/**
 * @author Yun-Shan
 */
public abstract class BaseDatabase {

    protected final LMCPlugin plugin;
    protected final ConfigurationSection dbConfig;
    protected MessageSender messageSender;

    private volatile boolean inited = false;
    private volatile boolean closed = false;
    protected AbstractDatabaseType dbType;

    public BaseDatabase(LMCPlugin plugin, FileConfiguration pluginConfig, MessageSender messageSender) {
        this.plugin = plugin;
        this.dbConfig = pluginConfig.getConfigurationSection("database");
        this.messageSender = messageSender;
    }

    public AbstractDatabaseType getDbType() {
        return this.dbType;
    }

    public boolean init() {
        if (this.inited) {
            return true;
        }
        if (this.dbConfig == null) {
            return fail("MissingAllConfig");
        }
        // 获取配置的数据库类型
        String type = this.dbConfig.getString("type");
        if (type == null) {
            return fail("MissingTypeConfig");
        }
        // 匹配支持的数据库类型
        this.dbType = AbstractDatabaseType.matchType(type, this.plugin, this.messageSender);
        if (this.dbType == null) {
            return fail("UnsupportedDatabaseType", type);
        }
        // 获取实际数据库配置
        ConfigurationSection dbCfg = this.dbConfig.getConfigurationSection(this.dbType.getName().toLowerCase());
        if (dbCfg == null) {
            return fail("MissingSubConfig", this.dbType.getName(), this.dbType.getName().toLowerCase());
        }
        // 构建JDBC URL
        String dbUrl = this.dbType.constructJdbcUrl(dbCfg);
        if (dbUrl == null) {
            return false;
        }

        try {
            Class.forName(this.dbType.getDriverClass());
        } catch (ClassNotFoundException e) {
            ExceptionHandler.handle(e);
            return fail("LoadDriverClassFail", this.dbType.getName(), this.dbType.getDriverClass());
        }

        messageSender.debugConsole(2, "database.TryConnect", dbUrl);
        try {
            if (this.connect(dbUrl)) {
                this.inited = true;
                messageSender.infoConsole("database.ConnectSuccess");
                return true;
            } else {
                return fail("ConnectFail");
            }
        } catch (SQLException e) {
            ExceptionHandler.handle(e);
            return fail("ConnectFail");
        }
    }

    /**
     * 使用jdbc url连接数据库
     *
     * @param jdbcUrl 指定用于连接的jdbc url
     * @return 连接成功返回true，其它情况返回false
     * @throws SQLException 连接时发生数据库异常
     */
    protected abstract boolean connect(String jdbcUrl) throws SQLException;

    public void close() {
        this.closed = true;
    }

    public void checkNotClosed() {
        if (this.closed) {
            throw new IllegalStateException(messageSender.getMessage("database.close"));
        }
    }

    private boolean fail(String msgKey, Object... args) {
        messageSender.errorConsole("database." + msgKey, args);
        return false;
    }
}
