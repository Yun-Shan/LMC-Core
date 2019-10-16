package org.yunshanmc.lmc.core.bukkit.message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.yunshanmc.lmc.core.message.BaseMessage;
import org.yunshanmc.lmc.core.message.MessageContext;
import org.yunshanmc.lmc.core.message.MessageFormat;
import org.yunshanmc.lmc.core.util.PlatformUtils;

import java.util.UUID;

/**
 * @author Yun-Shan
 */
public class BukkitMessage extends BaseMessage {

    public BukkitMessage(String msg, MessageContext context) {
        super(msg, context);
    }

    public BukkitMessage(String msg, MessageContext context, MessageFormat format) {
        super(msg, context, format);
    }

    @Override
    protected String translateAlternateColorCodes(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public String getMessageForPlayer(Object player, Object... args) {
        if (player != null) {
            PlatformUtils.checkPlayer(player);
        }
        return this.format.format(player, this.msg, args);
    }

    @Override
    public String[] getMessagesForPlayer(Object player, Object... args) {
        if (player != null) {
            PlatformUtils.checkPlayer(player);
        }
        String[] msgs = this.msg.split("\n");
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = this.format.format(player, msgs[i], args);
        }
        return msgs;
    }

    @Override
    public String getMessageForPlayer(UUID playerId, Object... args) {
        if (playerId == null) {
            return getMessageForPlayer((Object) null, args);
        }
        Player p = Bukkit.getPlayer(playerId);
        if (p != null) {
            return this.getMessageForPlayer(p, args);
        } else {
            return null;
        }
    }

    @Override
    public String[] getMessagesForPlayer(UUID playerId, Object... args) {
        if (playerId == null) {
            return getMessagesForPlayer((Object) null, args);
        }
        Player p = Bukkit.getPlayer(playerId);
        if (p != null) {
            return this.getMessagesForPlayer(p, args);
        } else {
            return null;
        }
    }
}
