package org.yunshanmc.lmc.core.command.executors;

import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.LMCCommand;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class CommandExecutor {

    protected MessageSender messageSender;
    protected final CommandManager commandManager;
    protected final String handleCommand;

    protected Map<String, LMCCommand> commands = new HashMap<>();

    protected CommandExecutor(CommandManager commandManager, MessageSender messageSender) {
        this.commandManager = commandManager;
        this.messageSender = messageSender;

        this.handleCommand = commandManager.getHandleCommand();
    }

    public void registerCommand(LMCCommand command) {
        this.commands.put(command.getName(), command);
    }

    public void unregisterCommand(String cmdName) {
        this.commands.remove(cmdName);
    }
}
