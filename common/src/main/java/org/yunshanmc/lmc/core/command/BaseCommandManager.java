package org.yunshanmc.lmc.core.command;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.command.executors.BaseCommandExecutor;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.message.MessageSender;

import java.util.List;

/**
 * @author Yun-Shan
 */
public abstract class BaseCommandManager implements CommandManager {

    protected final String handleCommand;

    protected BaseCommandExecutor commandExecutor;

    protected MessageManager messageManager;
    protected MessageSender messageSender;

    public BaseCommandManager(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.handleCommand = handleCommand;
        this.messageManager = plugin.getMessageManager();
        this.messageSender = this.messageManager.getMessageSender();
        this.initCommandExecutor(plugin, handleCommand, permission, aliases);
    }

    /**
     * 初始化相应平台的命令执行器，将相应平台的命令处理器绑定到自身
     *
     * @param plugin 命令所属插件
     * @param handleCommand 命令执行器管理的主命令
     * @param permission 主命令的权限
     * @param aliases 主命令的别名
     */
    protected abstract void initCommandExecutor(LMCPlugin plugin, String handleCommand, String permission, String... aliases);

    @Override
    public String getHandleCommand() {
        return this.handleCommand;
    }

    @Override
    public void registerCommand(AbstractLMCCommand command) {
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
    public List<AbstractLMCCommand> getCommands() {
        return this.commandExecutor.getCommands();
    }
}
