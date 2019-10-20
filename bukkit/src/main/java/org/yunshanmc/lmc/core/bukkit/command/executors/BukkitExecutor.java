package org.yunshanmc.lmc.core.bukkit.command.executors;

import org.bukkit.command.*;
import org.bukkit.permissions.Permissible;
import org.yunshanmc.lmc.core.bukkit.BaseLMCBukkitPlugin;
import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.executors.BaseCommandExecutor;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.List;

/**
 * @author Yun-Shan
 */
public class BukkitExecutor extends BaseCommandExecutor implements CommandExecutor, TabCompleter {

    public BukkitExecutor(CommandManager commandManager, MessageSender messageSender, BaseLMCBukkitPlugin plugin) {
        super(commandManager, messageSender);
        PluginCommand command = plugin.getCommand(this.handleCommand);
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    protected boolean checkPermission(Object sender, String perm) {
        return ((Permissible) sender).hasPermission(perm);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.onTabComplete(sender, alias, args);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.executeCommand(sender, label, args);
        return true;
    }
}
