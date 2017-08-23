package org.yunshanmc.lmc.core.database.type;

import org.bukkit.configuration.ConfigurationSection;
import org.yunshanmc.lmc.core.message.MessageSender;

public abstract class DatabaseType {

    protected final MessageSender messageSender;
    private final String name;
    private final String driverClass;

    protected DatabaseType(String name, String driverClass, MessageSender messageSender) {
        this.name = name;
        this.driverClass = driverClass;
        this.messageSender = messageSender;
    }

    public final String getName() {
        return this.name;
    }

    public final String getDriverClass() {
        return this.driverClass;
    }

    public abstract String constructJdbcUrl(ConfigurationSection config);

    public static DatabaseType matchType(String type, MessageSender messageSender) {
        switch (type.toLowerCase()) {
            case "mysql": return new MySQLDatabaseType(messageSender);
            case "sqlite": return new SQLiteDatabaseType(messageSender);
            default: return null;
        }
    }
}
