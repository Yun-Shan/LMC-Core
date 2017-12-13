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

    /**
     * 获取类型名(数据库类型)
     *
     * @return 数据库类型
     */
    public final String getName() {
        return this.name;
    }

    /**
     * 获取数据库驱动类
     *
     * @return 数据库驱动类
     */
    public final String getDriverClass() {
        return this.driverClass;
    }

    /**
     * 构建用于连接数据库的jdbc url
     *
     * @param config 数据库配置
     * @return 用于连接数据库的jdbc url
     */
    public abstract String constructJdbcUrl(ConfigurationSection config);

    /**
     * 获取测试URL，该测试URL用于测试数据库连接是否正常
     *
     * @return 测试URL
     */
    public abstract String getTestSQL();

    public static DatabaseType matchType(String type, MessageSender messageSender) {
        switch (type.toLowerCase()) {
            case "mysql":
                return new MySQLDatabaseType(messageSender);
            case "sqlite":
                return new SQLiteDatabaseType(messageSender);
            default:
                return null;
        }
    }
}
