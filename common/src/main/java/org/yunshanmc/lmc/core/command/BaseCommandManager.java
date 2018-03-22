package org.yunshanmc.lmc.core.command;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.command.executors.CommandExecutor;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.lang.invoke.MethodHandles;
import java.util.List;

public abstract class BaseCommandManager implements CommandManager {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    protected final String handleCommand;

    protected CommandExecutor commandExecutor;

    protected MessageManager messageManager;
    protected MessageSender messageSender;

    public BaseCommandManager(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.handleCommand = handleCommand;
        this.messageManager = plugin.getMessageManager();
        this.messageSender = this.messageManager.getMessageSender();
        this.initCommandExecutor(plugin, handleCommand, permission, aliases);
    }

    protected abstract void initCommandExecutor(LMCPlugin plugin, String handleCommand, String permission, String... aliases);

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
