package org.yunshanmc.lmc.core.bukkit.command.executors;

import com.google.common.base.Joiner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.yunshanmc.lmc.core.bukkit.BaseLMCBukkitPlugin;
import org.yunshanmc.lmc.core.bukkit.command.BukkitLMCCommandSender;
import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.AbstractLMCCommand;
import org.yunshanmc.lmc.core.command.executors.BaseCommandExecutor;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yun-Shan
 */
public class BukkitExecutor extends BaseCommandExecutor implements CommandExecutor, TabCompleter {

    public BukkitExecutor(CommandManager commandManager, MessageSender messageSender, BaseLMCBukkitPlugin plugin) {
        super(commandManager, messageSender);
        PluginCommand command = ((JavaPlugin)plugin).getCommand(this.handleCommand);
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        AbstractLMCCommand cmd = super.resolveCommand(args);

        if (cmd != null) {
            for (String perm : cmd.getPermissions()) {
                if (!sender.hasPermission(perm)) {
                    this.messageSender.info(sender, "command.NeedPermission");
                    return true;
                }
            }
            if (cmd.isValid()) {
                if (args.length >= 2) {
                    args = Arrays.copyOfRange(args, 1, args.length);
                } else {
                    args = new String[0];
                }
                cmd.execute(new BukkitLMCCommandSender(sender, this.messageSender), label, args);
            } else {
                this.messageSender.info(sender, "command.InvalidCommand",
                                        '/' + Joiner.on(' ').join(label, args.length > 0 ? args[0] : cmd.getName()));

            }
        } else {
            this.messageSender.info(sender, "command.CommandNotFound",
                                    '/' + Joiner.on(' ').skipNulls().join(label, args.length > 0 ? args[0] : null));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> cmds = new ArrayList<>(this.commands.keySet());
        if (args.length == 0) return cmds;

        cmds.removeIf(cmd -> !cmd.startsWith(args[0]));
        return cmds;
    }
}
