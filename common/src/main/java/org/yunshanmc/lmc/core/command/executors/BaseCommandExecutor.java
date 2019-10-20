package org.yunshanmc.lmc.core.command.executors;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.yunshanmc.lmc.core.command.AbstractLMCCommand;
import org.yunshanmc.lmc.core.command.CommandManager;
import org.yunshanmc.lmc.core.command.LMCCommandSender;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.*;

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

    private static final String[] EMPTY_ARGS = new String[0];

    public void executeCommand(Object sender, String label, String... args) {
        if (args == null) {
            args = EMPTY_ARGS;
        }
        AbstractLMCCommand cmd = this.resolveCommand(args);

        if (cmd != null) {
            for (String perm : cmd.getPermissions()) {
                if (!checkPermission(sender, perm)) {
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
                cmd.execute(new LMCCommandSender(sender, this.messageSender), label, args);
            } else {
                this.messageSender.info(sender, "command.InvalidCommand",
                    '/' + Joiner.on(' ').join(label, args.length > 0 ? args[0] : cmd.getName()));

            }
        } else {
            this.messageSender.info(sender, "command.CommandNotFound",
                '/' + Joiner.on(' ').skipNulls().join(label, args.length > 0 ? args[0] : null));
        }
    }

    public List<String> onTabComplete(Object sender, String alias, String[] args) {
        List<String> cmds = new ArrayList<>(this.commands.keySet());
        if (args.length == 0) {
            return cmds;
        }

        cmds.removeIf(cmd -> !cmd.startsWith(args[0]));
        return cmds;
    }

    protected abstract boolean checkPermission(Object sender, String perm);

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
