/*
 * Author: Yun-Shan
 * Date: 2017/07/05
 */
package org.yunshanmc.lmc.core.message;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.util.UUID;

/**
 * //TODO 注释
 */
public class Message {

    private final String msg;
    private final MessageContext context;
    private MessageFormat format;

    public Message(String msg, MessageContext context) {
        this(msg, context, new DefaultMessageFormat(context));
    }

    public Message(String msg, MessageContext context, MessageFormat format) {
        this.msg = ChatColor.translateAlternateColorCodes('&', msg);
        this.context = context;
        this.format = format;
    }

    public MessageContext getContext() {
        return this.context;
    }

    public String getRawMessage() {
        return this.msg;
    }

    public String getMessage(Player player, Object... args) {
        return this.format.format(player, this.msg, args);
    }

    public String[] getMessages(Player player, Object... args) {
        String[] msgs = this.msg.split("\n");
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = this.format.format(player, msgs[i], args);
        }
        return msgs;
    }

    public String getMessage(ProxiedPlayer player, Object... args) {
        return this.format.format(player, this.msg, args);
    }

    public String[] getMessages(ProxiedPlayer player, Object... args) {
        String[] msgs = this.msg.split("\n");
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = this.format.format(player, msgs[i], args);
        }
        return msgs;
    }

    public String getMessage(UUID playerId, Object... args) {
        if (PlatformUtils.isBukkit()) {
            Player p = Bukkit.getPlayer(playerId);
            if (p != null) {
                return this.getMessage(p, args);
            } else {
                return null;
            }
        } else if (PlatformUtils.isBungeeCord()) {
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerId);
            if (p != null) {
                return this.getMessage(p, args);
            } else {
                return null;
            }
        } else {
            throw new UnsupportedOperationException("Unsupported Platform");
        }
    }

    public String[] getMessages(UUID playerId, Object... args) {
        if (PlatformUtils.isBukkit()) {
            Player p = Bukkit.getPlayer(playerId);
            if (p != null) {
                return this.getMessages(p, args);
            } else {
                return null;
            }
        } else if (PlatformUtils.isBungeeCord()) {
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerId);
            if (p != null) {
                return this.getMessages(p, args);
            } else {
                return null;
            }
        } else {
            throw new UnsupportedOperationException("Unsupported Platform");
        }
    }

    public String getMessage(Object... args) {
        return this.format.format(this.msg, args);
    }

    public String[] getMessages(Object... args) {
        String[] msgs = this.msg.split("\n");
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = this.format.format(msgs[i], args);
        }
        return msgs;
    }

    public static class MissingMessage extends Message {

        public MissingMessage(String msg) {
            super(msg, null);
        }

        @Override
        public String getMessage(Player player, Object... args) {
            return this.missingMessage();
        }

        @Override
        public String[] getMessages(Player player, Object... args) {
            return new String[]{ this.missingMessage() };
        }

        @Override
        public String getMessage(ProxiedPlayer player, Object... args) {
            return this.missingMessage();
        }

        @Override
        public String[] getMessages(ProxiedPlayer player, Object... args) {
            return new String[]{ this.missingMessage() };
        }

        @Override
        public String getMessage(UUID playerId, Object... args) {
            return this.missingMessage();
        }

        @Override
        public String[] getMessages(UUID playerId, Object... args) {
            return new String[]{ this.missingMessage() };
        }

        @Override
        public String getMessage(Object... args) {
            return this.missingMessage();
        }

        @Override
        public String[] getMessages(Object... args) {
            return new String[]{ this.missingMessage() };
        }


        private String missingMessage() {
            return "§cMissingLanguage: " + getRawMessage();
        }
    }

}