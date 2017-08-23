package org.yunshanmc.lmc.core.database.type;

import com.google.common.base.Strings;
import org.bukkit.configuration.ConfigurationSection;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class MySQLDatabaseType extends DatabaseType {

    protected MySQLDatabaseType(MessageSender messageSender) {
        super("MySQL", "com.mysql.jdbc.Driver", messageSender);
    }

    @Override
    public String constructJdbcUrl(ConfigurationSection config) {
        String host = config.getString("host");
        String port = config.getString("port");
        String dbName = config.getString("database-name");
        String user = config.getString("user");
        String password = config.getString("password");
        List<String> require = Arrays.asList(host, port, dbName, user);
        require.removeIf(Strings::isNullOrEmpty);
        if (require.isEmpty() || password == null) {
            messageSender.errorConsole("database.MySQL.buildUrl.missingConfig",
                                       host != null ? host : "未配置",
                                       port != null ? port : "未配置",
                                       dbName != null ? dbName : "未配置",
                                       user != null ? user : "未配置",
                                       password != null ? password : "未配置");
            return null;
        }
        String url = MessageFormat.format(
                "jdbc:mysql://{0}:{1}/{2}" +
                "?user={3}" +
                "&password={4}" +
                "&createDatabaseIfNotExist=true" +
                "&autoReconnect=true" +
                "&useUnicode=true" +
                "&characterEncoding=UTF-8",
                host, port, dbName, user, password);
        messageSender.debugConsole(2, "database.MySQL.buildUrl.jdbcUrl", host, port, dbName, user, password, url);
        return url;
    }
}
