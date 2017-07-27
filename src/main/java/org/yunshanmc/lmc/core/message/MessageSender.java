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

    MessageSender message(Player receiver, String type, String msgKey, Object... args);

    MessageSender messageConsole(String type, String msgKey, Object... args);

    MessageSender info(Player receiver, String msgKey, Object... args);

    MessageSender infoConsole(String msgKey, Object... args);

    MessageSender warning(Player receiver, String msgKey, Object... args);

    MessageSender warningConsole(String msgKey, Object... args);

    MessageSender error(Player receiver, String msgKey, Object... args);

    MessageSender errorConsole(String msgKey, Object... args);

    MessageSender debug(int debugLevel, Player receiver, String msgKey, Object... args);

    MessageSender debugConsole(int debugLevel, String msgKey, Object... args);

    MessageSender setDebugLevel(int debugLevel);

    int getDebugLevel();
}
