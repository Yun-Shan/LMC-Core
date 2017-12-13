package org.yunshanmc.lmc.core.database.type;

import com.google.common.base.Strings;
import org.bukkit.configuration.ConfigurationSection;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.io.File;
import java.nio.file.Paths;

public class SQLiteDatabaseType extends DatabaseType {

    protected SQLiteDatabaseType(MessageSender messageSender) {
        super("SQLite", "org.sqlite.JDBC", messageSender);
    }

    @Override
    public String constructJdbcUrl(ConfigurationSection config) {
        String path = config.getString("path");
        if (Strings.isNullOrEmpty(path)) {
            messageSender.errorConsole("database.SQLite.buildUrl.missingConfig", path);
            return null;
        }
        File dbFile = Paths.get("plugin", "EpicGuild").resolve(path).toFile();
        if (!dbFile.getParentFile().mkdirs()) {
            messageSender.warningConsole("database.SQLite.buildUrl.mkdirsFail", path);
        }
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        messageSender.debugConsole(2, "database.SQLite.buildUrl.jdbcUrl", path, url);
        return url;
    }

    @Override
    public String getTestSQL() {
        return null;
    }
}
