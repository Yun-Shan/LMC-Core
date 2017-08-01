package org.yunshanmc.lmc.core.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public interface CommandManager {

    /**
     * 获取管理的命令，该命令为插件在plugin.yml中定义的命令
     *
     * @return 该命令管理器管理的命令
     */
    String getHandleCommand();

    /**
     * 批量注册注解命令
     *
     * @param command
     *     要注册的命令
     */
    void registerCommands(SimpleLMCCommand command);

    /**
     * 注册命令
     *
     * @param command
     *     要注册的命令
     */
    void registerCommand(LMCCommand command);

    /**
     * 删除命令
     *
     * @param cmdName
     *     要删除的命令名
     */
    void unregisterCommand(String cmdName);
}
