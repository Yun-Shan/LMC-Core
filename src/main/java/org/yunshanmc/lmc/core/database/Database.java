package org.yunshanmc.lmc.core.database;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.yunshanmc.lmc.core.database.type.DatabaseType;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.sql.SQLException;

public abstract class Database {

    protected final ConfigurationSection dbConfig;
    protected MessageSender messageSender;

    private volatile boolean inited = false;
    private volatile boolean closed = false;
    protected DatabaseType dbType;

    public Database(FileConfiguration pluginConfig, MessageSender messageSender){
        this.dbConfig = pluginConfig.getConfigurationSection("database");
        this.messageSender = messageSender;
    }

    public boolean init() {
        if (this.inited) return true;
        if (this.dbConfig == null) return fail("missingAllConfig");
        // 获取配置的数据库类型
        String type = this.dbConfig.getString("type");
        if (type == null) return fail("missingTypeConfig");
        // 匹配支持的数据库类型
        this.dbType = DatabaseType.matchType(this.dbConfig.getString("type"), this.messageSender);
        if (this.dbType == null) return fail("unsupportedDatabaseType");
        // 获取实际数据库配置
        ConfigurationSection dbCfg = this.dbConfig.getConfigurationSection(this.dbType.getName().toLowerCase());
        if (dbCfg == null) return fail("missingSubConfig", this.dbType.getName(), this.dbType.getName().toLowerCase());
        // 构建JDBC URL
        String dbUrl = this.dbType.constructJdbcUrl(dbCfg);
        if (dbUrl == null) return false;

        messageSender.debugConsole(2, "database.tryConnect", dbType, dbUrl);
        try {
            if (this.connect(dbUrl)) {
                this.inited = true;
                messageSender.infoConsole("database.connectSuccess");
                return true;
            } else {
                return fail("connectFail");
            }
        } catch (SQLException e) {
            ExceptionHandler.handle(e);
            return fail("connectFail");
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
