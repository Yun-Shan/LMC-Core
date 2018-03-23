package org.yunshanmc.lmc.core.bukkit.message;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.bukkit.message.BukkitMessageSender;
import org.yunshanmc.lmc.core.config.ConfigManager;
import org.yunshanmc.lmc.core.message.BaseMessageManager;
import org.yunshanmc.lmc.core.message.Message;
import org.yunshanmc.lmc.core.message.MessageContext;
import org.yunshanmc.lmc.core.message.MessageSender;

/**
 * @author Yun-Shan
 */
public class BukkitMessageManager extends BaseMessageManager {
    public BukkitMessageManager(LMCPlugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
    }

    public BukkitMessageManager(LMCPlugin plugin, ConfigManager configManager, String defMsgPath) {
        super(plugin, configManager, defMsgPath);
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
