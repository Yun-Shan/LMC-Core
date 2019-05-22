package org.yunshanmc.lmc.core.bungee.command;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.bungee.BaseLMCBungeeCordPlugin;
import org.yunshanmc.lmc.core.bungee.command.executors.BungeeCordExecutor;
import org.yunshanmc.lmc.core.command.BaseCommandManager;

/**
 * @author Yun-Shan
 */
public class BungeeCommandManager extends BaseCommandManager {

    public BungeeCommandManager(LMCPlugin plugin, String handleCommand) {
        this(plugin, handleCommand, null);
    }

    public BungeeCommandManager(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        super(plugin, handleCommand, null, aliases);
    }

    @Override
    protected void initCommandExecutor(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.commandExecutor = new BungeeCordExecutor(this, plugin.getMessageManager().getMessageSender(),
            (BaseLMCBungeeCordPlugin) plugin, permission, aliases);
    }
}
