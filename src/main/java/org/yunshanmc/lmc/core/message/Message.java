/*
 * Author: Yun-Shan
 * Date: 2017/07/05
 */
package org.yunshanmc.lmc.core.message;

import org.bukkit.entity.Player;

/**
 * //TODO 注释
 */
public class Message {
    
    private final String msg;
    private MessageFormat format;
    
    public Message(String msg) {
        this.msg = msg;
    }
    
    public Message(String msg, MessageFormat format) {
        this(msg);
        this.format = format;
    }
    
    public String getRawMessage() {
        return this.msg;
    }
    
    public String getMessage(Player player, Object... args) {
        return this.format.format(player, this.msg, args);
    }

}