/*
 * Author: Yun-Shan
 * Date: 2017/06/23
 */
package org.yunshanmc.lmc.core.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 信息发送者，用于向后台/玩家发送信息
 */
public interface MessageSender {// TODO 暂时想不好注释怎么写，先实现吧，实现完写注释
    
    void message(Player receiver, String type, String msgKey, Object... args);
    
    void messageConsole(String type, String msgKey, Object... args);
    
    
}
