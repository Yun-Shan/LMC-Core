package org.yunshanmc.lmc.core.bukkit.message;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.message.BaseGroupMessageManager;
import org.yunshanmc.lmc.core.message.Message;
import org.yunshanmc.lmc.core.message.MessageContext;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.resource.ResourceManager;

/**
 * @author Yun-Shan
 */
public class BukkitGroupMessageManager extends BaseGroupMessageManager {
    public BukkitGroupMessageManager(LMCPlugin plugin, ResourceManager resourceManager) {
        super(plugin, resourceManager);
    }

    public BukkitGroupMessageManager(LMCPlugin plugin, ResourceManager resourceManager, String defMsgPath) {
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
