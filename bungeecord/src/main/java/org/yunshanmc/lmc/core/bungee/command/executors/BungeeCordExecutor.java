package org.yunshanmc.lmc.core.bungee.command.executors;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.yunshanmc.lmc.core.bungee.BaseLMCBungeeCordPlugin;
import org.yunshanmc.lmc.core.bungee.command.BungeeLMCCommandSender;
import org.yunshanmc.lmc.core.command.AbstractLMCCommand;
import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.executors.BaseCommandExecutor;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author Yun-Shan
 */
public class BungeeCordExecutor extends BaseCommandExecutor {

    public BungeeCordExecutor(CommandManager commandManager, MessageSender messageSender, BaseLMCBungeeCordPlugin plugin, String
        permission, String... aliases) {
        super(commandManager, messageSender);
        Function<String, BaseBCCommand> cmdCreator = name -> new BaseBCCommand(name, permission) {
            @Override
            public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
                return BungeeCordExecutor.this.onTabComplete(sender, this, name, args);
            }

            @Override
            public void execute(CommandSender sender, String[] args) {
                BungeeCordExecutor.this.execute(sender, this, name, args);
            }
        };
        PluginManager pm = plugin.getProxy().getPluginManager();
        pm.registerCommand(plugin, cmdCreator.apply(this.handleCommand));
        for (String alias : aliases) {
            pm.registerCommand(plugin, cmdCreator.apply(alias));
        }
    }

    public void execute(CommandSender sender, Command command, String label, String[] args) {
        AbstractLMCCommand cmd = super.resolveCommand(args);

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
                cmd.execute(new BungeeLMCCommandSender(sender, this.messageSender), label, args);

            } else {
                this.messageSender.info(sender, "command.InvalidCommand",
                    '/' + Joiner.on(' ').join(label, args.length > 0 ? args[0] : cmd.getName()));

            }
        } else {
            this.messageSender.info(sender, "command.CommandNotFound",
                '/' + Joiner.on(' ').skipNulls().join(label, args.length > 0 ? args[0] : null));
        }
    }

    public Iterable<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> cmds = new ArrayList<>(this.commands.keySet());
        if (args.length == 0) {
            return cmds;
        }

        cmds.removeIf(cmd -> !cmd.startsWith(args[0]));
        return cmds;
    }

    private static abstract class BaseBCCommand extends Command implements TabExecutor {
        public BaseBCCommand(String name, String permission, String... aliases) {
            super(name, permission, aliases);
        }
    }
}
