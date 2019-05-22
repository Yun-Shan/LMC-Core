package org.yunshanmc.lmc.core.bukkit.command;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.bukkit.BaseLMCBukkitPlugin;
import org.yunshanmc.lmc.core.bukkit.command.executors.BukkitExecutor;
import org.yunshanmc.lmc.core.command.BaseCommandManager;

/**
 * @author Yun-Shan
 */
public class BukkitCommandManager extends BaseCommandManager {

    public BukkitCommandManager(LMCPlugin plugin, String handleCommand) {
        super(plugin, handleCommand, null);
    }

    @Override
    protected void initCommandExecutor(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.commandExecutor = new BukkitExecutor(this, plugin.getMessageManager().getMessageSender(),
            (BaseLMCBukkitPlugin) plugin);
    }
}
