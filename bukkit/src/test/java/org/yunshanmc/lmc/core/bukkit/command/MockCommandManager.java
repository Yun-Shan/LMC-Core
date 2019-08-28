package org.yunshanmc.lmc.core.bukkit.command;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.command.AbstractLMCCommand;
import org.yunshanmc.lmc.core.command.BaseCommandManager;
import org.yunshanmc.lmc.core.command.executors.BaseCommandExecutor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MockCommandManager extends BaseCommandManager {

    private Map<String, AbstractLMCCommand> mockCommands;

    public MockCommandManager(LMCPlugin plugin, String handleCommand, AtomicReference<Map<String, AbstractLMCCommand>> mockCommands) {
        super(plugin, handleCommand, null, (String[]) null);
        mockCommands.set(this.mockCommands);
    }

    @Override
    protected void initCommandExecutor(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.commandExecutor = new BaseCommandExecutor(this, null) {
            {
                MockCommandManager.this.mockCommands = this.commands;
            }
        };
    }
}
