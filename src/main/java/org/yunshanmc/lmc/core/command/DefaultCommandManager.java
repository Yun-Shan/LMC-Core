package org.yunshanmc.lmc.core.command;

import com.google.common.base.Strings;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.bukkit.LMCBukkitPlugin;
import org.yunshanmc.lmc.core.bukkit.command.executors.BukkitExecutor;
import org.yunshanmc.lmc.core.bungee.LMCBungeeCordPlugin;
import org.yunshanmc.lmc.core.bungee.command.executors.BungeeCordExecutor;
import org.yunshanmc.lmc.core.command.executors.CommandExecutor;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.message.Message;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class DefaultCommandManager implements CommandManager {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final String handleCommand;

    private CommandExecutor commandExecutor;

    private MessageManager messageManager;
    private MessageSender messageSender;

    public DefaultCommandManager(LMCPlugin plugin, String handleCommand) {
        this(plugin, handleCommand, null, (String[]) null);
    }

    /**
     * 注意：此方法是给BungeeCord插件使用的，Bukkit插件传入的<code>permission, aliases</code>参数将被忽略。
     * Bukkit插件建议使用{@link #DefaultCommandManager(LMCPlugin, String)}
     * <p>
     */
    public DefaultCommandManager(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.handleCommand = handleCommand;
        this.messageManager = plugin.getMessageManager();
        this.messageSender = this.messageManager.getMessageSender();

        if (PlatformUtils.isBukkit() && plugin instanceof LMCBukkitPlugin) {
            this.commandExecutor = new BukkitExecutor(this, plugin.getMessageManager().getMessageSender(),
                                                      (LMCBukkitPlugin) plugin);
        } else if (PlatformUtils.isBungeeCord() && plugin instanceof LMCBungeeCordPlugin) {
            this.commandExecutor = new BungeeCordExecutor(this, plugin.getMessageManager().getMessageSender(),
                                                          // XXX: Java抽风，不能转LMCBungeeCordPlugin，只能转Plugin，鬼知道为什么
                                                          (Plugin) plugin, permission, aliases);
        }
    }

    @Override
    public String getHandleCommand() {
        return this.handleCommand;
    }

    @Override
    public void registerCommand(LMCCommand command) {
        this.commandExecutor.registerCommand(command);
    }

    @Override
    public void registerCommands(SimpleLMCCommand command) {
        SimpleCommandFactory
                .build(command, this.messageSender, this.handleCommand, this.messageManager)
                .forEach(this::registerCommand);
    }

    @Override
    public void unregisterCommand(String cmdName) {
        this.commandExecutor.unregisterCommand(cmdName);
    }

    @Override
    public List<LMCCommand> getCommands() {
        return this.commandExecutor.getCommands();
    }
}
