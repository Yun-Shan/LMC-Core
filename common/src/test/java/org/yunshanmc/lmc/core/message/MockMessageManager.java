package org.yunshanmc.lmc.core.message;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.config.ConfigManager;

public class MockMessageManager extends BaseMessageManager {
    public MockMessageManager(LMCPlugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
    }

    @Override
    protected Message newMessage(String msg, MessageContext context) {
        return new MockMessage(msg, context);
    }

    @Override
    public MessageSender getMessageSender() {
        return new MockMessageSender(this);
    }
}
