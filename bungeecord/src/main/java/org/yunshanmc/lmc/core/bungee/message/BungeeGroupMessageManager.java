package org.yunshanmc.lmc.core.bungee.message;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.config.ConfigManager;
import org.yunshanmc.lmc.core.message.BaseGroupMessageManager;
import org.yunshanmc.lmc.core.message.Message;
import org.yunshanmc.lmc.core.message.MessageContext;
import org.yunshanmc.lmc.core.message.MessageSender;

public class BungeeGroupMessageManager extends BaseGroupMessageManager {
    public BungeeGroupMessageManager(LMCPlugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
    }

    public BungeeGroupMessageManager(LMCPlugin plugin, ConfigManager configManager, String defMsgPath) {
        super(plugin, configManager, defMsgPath);
    }

    @Override
    protected Message newMessage(String msg, MessageContext context) {
        return new BungeeMessage(msg, context);
    }

    @Override
    public MessageSender getMessageSender() {
        return new BungeeMessageSender(this).setDebugLevel(this.getDebugLevel());
    }
}
