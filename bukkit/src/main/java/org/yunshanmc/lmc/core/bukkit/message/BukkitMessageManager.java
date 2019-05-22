package org.yunshanmc.lmc.core.bukkit.message;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.message.BaseMessageManager;
import org.yunshanmc.lmc.core.message.Message;
import org.yunshanmc.lmc.core.message.MessageContext;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.resource.ResourceManager;

/**
 * @author Yun-Shan
 */
public class BukkitMessageManager extends BaseMessageManager {
    public BukkitMessageManager(LMCPlugin plugin, ResourceManager resourceManager) {
        super(plugin, resourceManager);
    }

    public BukkitMessageManager(LMCPlugin plugin, ResourceManager resourceManager, String defMsgPath) {
        super(plugin, resourceManager, defMsgPath);
    }

    @Override
    protected Message newMessage(String msg, MessageContext context) {
        return new BukkitMessage(msg, context);
    }

    @Override
    public MessageSender getMessageSender() {
        return new BukkitMessageSender(this).setDebugLevel(this.getDebugLevel());
    }
}
