package org.yunshanmc.lmc.core.bungee.command.executors;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.yunshanmc.lmc.core.bungee.LMCBungeeCordPlugin;
import org.yunshanmc.lmc.core.bungee.command.LMCCommandSenderImpl;
import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.LMCCommand;
import org.yunshanmc.lmc.core.command.executors.CommandExecutor;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BungeeCordExecutor extends CommandExecutor {

    public BungeeCordExecutor(CommandManager commandManager, MessageSender messageSender, LMCBungeeCordPlugin plugin, String
        permission, String... aliases) {
        super(commandManager, messageSender);
        plugin.getProxy().getPluginManager().registerCommand(plugin, new BCCommand(this.handleCommand, permission, aliases) {
            @Override
            public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
                return BungeeCordExecutor.this.onTabComplete(sender, args);
            }

            @Override
            public void execute(CommandSender sender, String[] args) {
                BungeeCordExecutor.this.execute(sender, args);
            }
        });
    }

    public void execute(CommandSender sender, String[] args) {
        LMCCommand cmd = super.resolveCommand(args);

        if (cmd != null) {
            for (String perm : cmd.getPermissions()) {
                if (!sender.hasPermission(perm)) {
                    this.messageSender.info(sender, "command.NeedPermission");
                    return;
                }
            }
            if (cmd.isValid()) {
                if (args.length >= 2) {
                    args = Arrays.copyOfRange(args, 1, args.length);
                } else {
                    args = new String[0];
                }
                cmd.execute(new LMCCommandSenderImpl(sender, this.messageSender), this.handleCommand, args);
            } else {
                this.messageSender.info(sender, "command.InvalidCommand",
                                        '/' + Joiner.on(' ').join(this.handleCommand, args.length > 0 ? args[0] : cmd.getName()));

            }
        } else {
            this.messageSender.info(sender, "command.CommandNotFound",
                                    '/' + Joiner.on(' ').skipNulls().join(this.handleCommand, args.length > 0 ? args[0] : null));
        }
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> cmds = new ArrayList<>(this.commands.keySet());
        if (args.length == 0) return cmds;

        cmds.removeIf(cmd -> !cmd.startsWith(args[0]));
        return cmds;
    }

    private static abstract class BCCommand extends Command implements TabExecutor {
        public BCCommand(String name, String permission, String... aliases) {
            super(name, permission, aliases);
        }
    }
}
