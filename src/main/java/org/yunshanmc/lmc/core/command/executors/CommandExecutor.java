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
        LMCCommand cmd;
        if (args.length > 0) cmd = this.commands.get(args[0]);
            // 无参数时尝试空命令名
        else cmd = this.commands.get("");
        return cmd;
    }

    public void registerCommand(LMCCommand command) {
        if (command.getCommandManager() != null) return;
        command.setCommandManager(this.commandManager);
        this.commands.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            this.commands.putIfAbsent(alias, command);
        }
        command.getPermissions().replaceAll(
                perm -> perm.charAt(0) == '.' ? this.commandManager.getHandleCommand() + perm : perm);
        if (command.isUseDefaultPermission()) command.getPermissions().add(this.getDefaultCommandPermission(command));
    }

    public void unregisterCommand(String cmdName) {
        LMCCommand cmd = this.commands.remove(cmdName);
        if (cmd != null) {
            cmd.getPermissions().remove(this.getDefaultCommandPermission(cmd));
            String prefix = this.commandManager.getHandleCommand() + '.';
            int idx = prefix.length() - 1;
            cmd.getPermissions().replaceAll(perm -> perm.startsWith(prefix) ? perm.substring(idx) : perm);
            for (String alias : cmd.getAliases()) {
                if (cmd.equals(this.commands.get(alias))) this.commands.remove(alias);
            }
            cmd.setCommandManager(null);
        }
    }

    private String getDefaultCommandPermission(LMCCommand cmd) {
        return this.commandManager.getHandleCommand() + '.' + cmd.getName();
    }


    public List<LMCCommand> getCommands() {
        return ImmutableList.copyOf(this.commands.values());
    }
}
