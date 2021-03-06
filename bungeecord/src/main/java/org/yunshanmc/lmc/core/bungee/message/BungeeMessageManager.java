package org.yunshanmc.lmc.core.bungee.message;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.message.BaseMessageManager;
import org.yunshanmc.lmc.core.message.Message;
import org.yunshanmc.lmc.core.message.MessageContext;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.resource.ResourceManager;

/**
 * @author Yun-Shan
 */
public class BungeeMessageManager extends BaseMessageManager {
    public BungeeMessageManager(LMCPlugin plugin, ResourceManager resourceManager) {
        super(plugin, resourceManager);
    }

    public BungeeMessageManager(LMCPlugin plugin, ResourceManager resourceManager, String defMsgPath) {
        super(plugin, resourceManager, defMsgPath);
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
