package org.yunshanmc.lmc.core.command.executors;

import com.google.common.collect.ImmutableList;
import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.AbstractLMCCommand;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yun-Shan
 */
public abstract class BaseCommandExecutor {

    protected MessageSender messageSender;
    protected final CommandManager commandManager;
    protected final String handleCommand;

    protected Map<String, AbstractLMCCommand> commands = new HashMap<>();

    protected BaseCommandExecutor(CommandManager commandManager, MessageSender messageSender) {
        this.commandManager = commandManager;
        this.messageSender = messageSender;

        this.handleCommand = commandManager.getHandleCommand();
    }

    protected AbstractLMCCommand resolveCommand(String[] args) {
        AbstractLMCCommand cmd;
        if (args.length > 0) {
            cmd = this.commands.get(args[0]);
        } else {// 无参数时尝试空命令名
            cmd = this.commands.get("");
        }
        return cmd;
    }

    public void registerCommand(AbstractLMCCommand command) {
        if (command.getCommandManager() != null) {
            return;
        }
        command.setCommandManager(this.commandManager);
        this.commands.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            this.commands.putIfAbsent(alias, command);
        }
        command.getPermissions().replaceAll(
                perm -> perm.charAt(0) == '.' ? this.commandManager.getHandleCommand() + perm : perm);
        if (command.isUseDefaultPermission()) {
            command.getPermissions().add(this.getDefaultCommandPermission(command));
        }
    }

    public void unregisterCommand(String cmdName) {
        AbstractLMCCommand cmd = this.commands.remove(cmdName);
        if (cmd != null) {
            cmd.getPermissions().remove(this.getDefaultCommandPermission(cmd));
            String prefix = this.commandManager.getHandleCommand() + '.';
            int idx = prefix.length() - 1;
            cmd.getPermissions().replaceAll(perm -> perm.startsWith(prefix) ? perm.substring(idx) : perm);
            for (String alias : cmd.getAliases()) {
                if (cmd.equals(this.commands.get(alias))) {
                    this.commands.remove(alias);
                }
            }
            cmd.setCommandManager(null);
        }
    }

    private String getDefaultCommandPermission(AbstractLMCCommand cmd) {
        return this.commandManager.getHandleCommand() + '.' + cmd.getName();
    }


    public List<AbstractLMCCommand> getCommands() {
        return ImmutableList.copyOf(this.commands.values());
    }
}
