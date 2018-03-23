package org.yunshanmc.lmc.core.database.type;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.config.bukkitcfg.ConfigurationSection;
import org.yunshanmc.lmc.core.message.MessageSender;

/**
 * @author Yun-Shan
 */
public abstract class AbstractDatabaseType {

    protected final LMCPlugin plugin;
    protected final MessageSender messageSender;
    private final String name;
    private final String driverClass;

    protected AbstractDatabaseType(LMCPlugin plugin, String name, String driverClass, MessageSender messageSender) {
        this.plugin = plugin;
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

    public static AbstractDatabaseType matchType(String type, LMCPlugin plugin, MessageSender messageSender) {
        switch (type.toLowerCase()) {
            case "mysql":
                return new MySQLDatabaseType(plugin, messageSender);
            case "sqlite":
                return new SQLiteDatabaseType(plugin, messageSender);
            default:
                return null;
        }
    }
}
