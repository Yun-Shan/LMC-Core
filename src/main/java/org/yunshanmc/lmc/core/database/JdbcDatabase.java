package org.yunshanmc.lmc.core.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcDatabase extends Database {

    private Connection connection;

    public JdbcDatabase(LMCPlugin plugin, FileConfiguration pluginConfig, MessageSender messageSender) {
        super(plugin, pluginConfig, messageSender);
    }

    @Override
    protected boolean connect(String jdbcUrl) throws SQLException {
        this.connection = DriverManager.getConnection(jdbcUrl);

        this.connection.createStatement().execute(this.dbType.getTestSQL());
        return true;
    }

    @Override
    public void close() {
        super.close();
        try {
            if (this.connection != null) this.connection.close();
        } catch (SQLException e) {
            ExceptionHandler.handle(e);
        } finally {
            this.connection = null;
        }
    }

    public Connection getConnection() {
        this.checkNotClosed();
        return this.connection;
    }
}
