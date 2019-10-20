package org.yunshanmc.lmc.core.bungee.command.executors;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.yunshanmc.lmc.core.bungee.BaseLMCBungeeCordPlugin;
import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.executors.BaseCommandExecutor;
import org.yunshanmc.lmc.core.message.MessageSender;

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
                return BungeeCordExecutor.this.onTabComplete(sender, name, args);
            }

            @Override
            public void execute(CommandSender sender, String[] args) {
                BungeeCordExecutor.this.executeCommand(sender, name, args);
            }
        };
        PluginManager pm = plugin.getProxy().getPluginManager();
        pm.registerCommand(plugin, cmdCreator.apply(this.handleCommand));
        for (String alias : aliases) {
            pm.registerCommand(plugin, cmdCreator.apply(alias));
        }
    }

    @Override
    protected boolean checkPermission(Object sender, String perm) {
        return ((CommandSender) sender).hasPermission(perm);
    }

    private static abstract class BaseBCCommand extends Command implements TabExecutor {
        public BaseBCCommand(String name, String permission, String... aliases) {
            super(name, permission, aliases);
        }
    }
}
