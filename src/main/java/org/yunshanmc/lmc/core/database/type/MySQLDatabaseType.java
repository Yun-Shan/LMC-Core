package org.yunshanmc.lmc.core.database.type;

import com.google.common.base.Strings;
import org.bukkit.configuration.ConfigurationSection;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MySQLDatabaseType extends DatabaseType {

    protected MySQLDatabaseType(LMCPlugin plugin, MessageSender messageSender) {
        super(plugin, "MySQL", "com.mysql.jdbc.Driver", messageSender);
    }

    @Override
    public String constructJdbcUrl(ConfigurationSection config) {
        String host = config.getString("host", "");
        String port = config.getString("port", "");
        String dbName = config.getString("database-name", "");
        String user = config.getString("user", "");
        String password = config.getString("password", "");
        List<String> require = Stream
                .of(host, port, dbName, user)
                .filter(Strings::isNullOrEmpty)
                .collect(Collectors.toList());
        if (!require.isEmpty()) {
            messageSender.errorConsole("database.MySQL.buildURL.MissingConfig",
                                       host, port, dbName, user, password);
            return null;
        }
        String url = MessageFormat.format(
                "jdbc:mysql://{0}:{1}/{2}" +
                        "?user={3}" +
                        "&password={4}" +
                        "&createDatabaseIfNotExist=true" +
                        "&autoReconnect=true" +
                        "&useUnicode=true" +
                        "&characterEncoding=UTF-8" +
                        "&useSSL=false",
                host, port, dbName, user, password);
        messageSender.debugConsole(2, "database.MySQL.buildURL.JDBC_URL", host, port, dbName, user, password, url);
        return url;
    }

    @Override
    public String getTestSQL() {
        return "SELECT 1;";
    }
}
