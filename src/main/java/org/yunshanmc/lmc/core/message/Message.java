/*
 * Author: Yun-Shan
 * Date: 2017/07/05
 */
package org.yunshanmc.lmc.core.message;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * //TODO 注释
 */
public class Message {

    private final String msg;
    private MessageFormat format;

    public Message(String msg) {
        this(msg, new DefaultMessageFormat());
    }

    public Message(String msg, MessageFormat format) {
        this.msg = ChatColor.translateAlternateColorCodes('&', msg);
        this.format = format;
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

    public static class MissingMessage extends Message {

        public MissingMessage(String msg) {
            super(msg, null);
        }

        @Override
        public String getMessage(Player player, Object... args) {
            return "§cMissingLanguage: " + getRawMessage();
        }

        @Override
        public String[] getMessages(Player player, Object... args) {
            return new String[]{ this.getMessage(player, args) };
        }
    }

}