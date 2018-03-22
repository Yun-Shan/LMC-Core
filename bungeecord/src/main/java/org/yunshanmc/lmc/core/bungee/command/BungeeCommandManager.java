package org.yunshanmc.lmc.core.bungee.command;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.bungee.LMCBungeeCordPlugin;
import org.yunshanmc.lmc.core.bungee.command.executors.BungeeCordExecutor;
import org.yunshanmc.lmc.core.command.BaseCommandManager;

public class BungeeCommandManager extends BaseCommandManager {

    public BungeeCommandManager(LMCPlugin plugin, String handleCommand) {
        super(plugin, handleCommand, null, (String[]) null);
    }

    @Override
    protected void initCommandExecutor(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.commandExecutor = new BungeeCordExecutor(this, plugin.getMessageManager().getMessageSender(),
            (LMCBungeeCordPlugin) plugin, permission, aliases);
    }
}
