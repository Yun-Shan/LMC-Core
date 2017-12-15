package org.yunshanmc.lmc.core.command.executors;

import com.google.common.collect.ImmutableList;
import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.LMCCommand;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.HashMap;
import java.util.List;
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

    protected LMCCommand resolveCommand(String[] args) {
        LMCCommand cmd = null;
        if (args.length > 0) cmd = this.commands.get(args[0]);
        // 无参数或第一个参数无法匹配到命令时尝试空命令名
        if (cmd == null) this.commands.get("");
        return cmd;
    }

    public void registerCommand(LMCCommand command) {
        this.commands.put(command.getName(), command);
    }

    public void unregisterCommand(String cmdName) {
        this.commands.remove(cmdName);
    }

    public List<LMCCommand> getCommands() {
        return ImmutableList.copyOf(this.commands.values());
    }
}
