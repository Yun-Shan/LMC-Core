package org.yunshanmc.lmc.core.database.type;

import com.google.common.base.Strings;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.config.bukkitcfg.ConfigurationSection;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.io.File;
import java.nio.file.Paths;

/**
 * @author Yun-Shan
 */
public class SQLiteDatabaseType extends AbstractDatabaseType {

    protected SQLiteDatabaseType(LMCPlugin plugin, MessageSender messageSender) {
        super(plugin, "SQLite", "org.sqlite.JDBC", messageSender);
    }

    @Override
    public String constructJdbcUrl(ConfigurationSection config) {
        String path = config.getString("path");
        if (Strings.isNullOrEmpty(path)) {
            this.messageSender.errorConsole("database.SQLite.buildURL.MissingConfig", path);
            return null;
        }
        File dbFile = Paths.get("plugins", this.plugin.getName()).resolve(path).toFile();
        if (!dbFile.getParentFile().exists() && !dbFile.getParentFile().mkdirs()) {
            this.messageSender.warningConsole("database.SQLite.buildURL.MKDirsFail", path);
            return null;
        }
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        this.messageSender.debugConsole(2, "database.SQLite.buildURL.JDBC_URL", path, url);
        return url;
    }

    @Override
    public String getTestSQL() {
        return "SELECT 1;";
    }
}
