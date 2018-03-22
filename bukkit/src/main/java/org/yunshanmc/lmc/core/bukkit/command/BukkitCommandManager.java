package org.yunshanmc.lmc.core.bukkit.command;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.bukkit.LMCBukkitPlugin;
import org.yunshanmc.lmc.core.bukkit.command.executors.BukkitExecutor;
import org.yunshanmc.lmc.core.command.BaseCommandManager;

public class BukkitCommandManager extends BaseCommandManager {

    public BukkitCommandManager(LMCPlugin plugin, String handleCommand) {
        super(plugin, handleCommand, null, (String[]) null);
    }

    @Override
    protected void initCommandExecutor(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.commandExecutor = new BukkitExecutor(this, plugin.getMessageManager().getMessageSender(),
            (LMCBukkitPlugin) plugin);
    }
}
