package org.yunshanmc.lmc.core.database;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcDatabase extends Database {

    private Connection connection;

    public JdbcDatabase(FileConfiguration pluginConfig, MessageSender messageSender) {
        super(pluginConfig, messageSender);
    }

    @Override
    protected boolean connect(String jdbcUrl) throws SQLException {
        this.connection = DriverManager.getConnection(jdbcUrl);
        // TODO: 测试连接语句 各类型数据库适配
        // MySQL
        // this.connection.createStatement().execute("SELECT 1;");
        return true;
    }

    public Connection getConnection() {
        this.checkNotClosed();
        return this.connection;
    }
}
