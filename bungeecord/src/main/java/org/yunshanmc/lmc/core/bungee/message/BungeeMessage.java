package org.yunshanmc.lmc.core.bungee.message;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.yunshanmc.lmc.core.message.BaseMessage;
import org.yunshanmc.lmc.core.message.MessageContext;
import org.yunshanmc.lmc.core.message.MessageFormat;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.util.UUID;

/**
 * @author Yun-Shan
 */
public class BungeeMessage extends BaseMessage {

    public BungeeMessage(String msg, MessageContext context) {
        super(msg, context);
    }

    public BungeeMessage(String msg, MessageContext context, MessageFormat format) {
        super(msg, context, format);
    }

    @Override
    protected String translateAlternateColorCodes(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public String getMessage(Object player, Object... args) {
        if (player != null) {
            PlatformUtils.checkPlayer(player);
        }
        return this.format.format(player, this.msg, args);
    }

    @Override
    public String[] getMessages(Object player, Object... args) {
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
    public String getMessage(UUID playerId, Object... args) {
        if (playerId == null) return getMessage((Object) null, args);
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerId);
        if (p != null) {
            return this.getMessage(p, args);
        } else {
            return null;
        }
    }

    @Override
    public String[] getMessages(UUID playerId, Object... args) {
        if (playerId == null) return getMessages((Object) null, args);
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerId);
        if (p != null) {
            return this.getMessages(p, args);
        } else {
            return null;
        }
    }
}
