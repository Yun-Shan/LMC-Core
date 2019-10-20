package org.yunshanmc.lmc.core.command;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.command.executors.BaseCommandExecutor;
import org.yunshanmc.lmc.core.util.PlatformUtils;
import org.yunshanmc.lmc.core.util.ReflectUtils;

public class MockCommandManager extends BaseCommandManager {

    static {
        try {
            ReflectUtils.setFieldValue(PlatformUtils.class, "SENDER_CLASS", null, MockCommandSender.class);
            ReflectUtils.setFieldValue(PlatformUtils.class, "PLAYER_CLASS", null, MockPlayer.class);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public MockCommandManager(LMCPlugin plugin, String handleCommand) {
        super(plugin, handleCommand, null, (String[]) null);
    }

    @Override
    protected void initCommandExecutor(LMCPlugin plugin, String handleCommand, String permission, String... aliases) {
        this.commandExecutor = new BaseCommandExecutor(this, plugin.getMessageManager().getMessageSender()) {
            @Override
            protected boolean checkPermission(Object sender, String perm) {
                return ((MockCommandSender) sender).hasPermission(perm);
            }
        };
    }

    public BaseCommandExecutor getCommandExecutor() {
        return this.commandExecutor;
    }
}
