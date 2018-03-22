package org.yunshanmc.lmc.core.database;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.config.bukkitcfg.ConfigurationSection;
import org.yunshanmc.lmc.core.config.bukkitcfg.file.FileConfiguration;
import org.yunshanmc.lmc.core.database.type.DatabaseType;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.sql.SQLException;

public abstract class Database {

    protected final LMCPlugin plugin;
    protected final ConfigurationSection dbConfig;
    protected MessageSender messageSender;

    private volatile boolean inited = false;
    private volatile boolean closed = false;
    protected DatabaseType dbType;

    public Database(LMCPlugin plugin, FileConfiguration pluginConfig, MessageSender messageSender) {
        this.plugin = plugin;
        this.dbConfig = pluginConfig.getConfigurationSection("database");
        this.messageSender = messageSender;
    }

    public DatabaseType getDbType() {
        return this.dbType;
    }

    public boolean init() {
        if (this.inited) return true;
        if (this.dbConfig == null) return fail("MissingAllConfig");
        // 获取配置的数据库类型
        String type = this.dbConfig.getString("type");
        if (type == null) return fail("MissingTypeConfig");
        // 匹配支持的数据库类型
        this.dbType = DatabaseType.matchType(type, this.plugin, this.messageSender);
        if (this.dbType == null) return fail("UnsupportedDatabaseType", type);
        // 获取实际数据库配置
        ConfigurationSection dbCfg = this.dbConfig.getConfigurationSection(this.dbType.getName().toLowerCase());
        if (dbCfg == null) return fail("MissingSubConfig", this.dbType.getName(), this.dbType.getName().toLowerCase());
        // 构建JDBC URL
        String dbUrl = this.dbType.constructJdbcUrl(dbCfg);
        if (dbUrl == null) return false;

        try {
            Class.forName(this.dbType.getDriverClass());
        } catch (ClassNotFoundException e) {
            ExceptionHandler.handle(e);
            return fail("LoadDriverClassFail", this.dbType.getName(), this.dbType.getDriverClass());
        }

        messageSender.debugConsole(2, "database.TryConnect", dbType, dbUrl);
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

    protected abstract boolean connect(String jdbcUrl) throws SQLException;

    public void close() {
        this.closed = true;
    }

    public void checkNotClosed() {
        if (this.closed) throw new IllegalStateException(messageSender.getMessage("database.close"));
    }

    private boolean fail(String msgKey, Object... args) {
        messageSender.errorConsole("database." + msgKey, args);
        return false;
    }
}
