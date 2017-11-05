package org.yunshanmc.lmc.core.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.sql.SQLException;

public class OrmLiteDatabase extends Database {

    private ConnectionSource connectionSource;

    public OrmLiteDatabase(FileConfiguration pluginConfig, MessageSender messageSender) {
        super(pluginConfig, messageSender);
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
}
