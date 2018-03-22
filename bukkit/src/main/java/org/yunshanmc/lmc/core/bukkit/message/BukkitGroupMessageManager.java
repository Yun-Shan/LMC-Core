package org.yunshanmc.lmc.core.bukkit.message;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.config.ConfigManager;
import org.yunshanmc.lmc.core.message.BaseGroupMessageManager;
import org.yunshanmc.lmc.core.message.Message;
import org.yunshanmc.lmc.core.message.MessageContext;
import org.yunshanmc.lmc.core.message.MessageSender;

public class BukkitGroupMessageManager extends BaseGroupMessageManager {
    public BukkitGroupMessageManager(LMCPlugin plugin, ConfigManager configManager) {
        super(plugin, configManager);
    }

    public BukkitGroupMessageManager(LMCPlugin plugin, ConfigManager configManager, String defMsgPath) {
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
