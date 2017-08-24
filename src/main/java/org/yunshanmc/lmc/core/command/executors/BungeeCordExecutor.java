package org.yunshanmc.lmc.core.command.executors;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.LMCCommand;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BungeeCordExecutor extends CommandExecutor {

    public BungeeCordExecutor(CommandManager commandManager, MessageSender messageSender, Plugin plugin, String permission, String... aliases) {
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
            if (!cmd.isValid()) {
                this.messageSender.info(sender, "command.invalid", this.handleCommand, cmd.getName());
                return;
            }
            if (args.length >= 2) {
                args = Arrays.copyOfRange(args, 1, args.length);
            } else {
                args = new String[0];
            }
            cmd.execute(sender, args);
        } else {
            this.messageSender.info(sender, "command.notFound", this.handleCommand, args.length > 0 ? args[0] : "");
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
