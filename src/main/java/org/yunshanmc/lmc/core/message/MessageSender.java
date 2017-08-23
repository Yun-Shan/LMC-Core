/*
 * Author: Yun-Shan
 * Date: 2017/06/23
 */
package org.yunshanmc.lmc.core.message;

import org.bukkit.entity.Player;

/**
 * 信息发送者，用于向后台/玩家发送信息
 */
public interface MessageSender {

    /**
     * 获取信息字符串
     *
     * @param msgKey 信息key
     * @param args 信息文本中的参数列表
     * @return 格式化后的信息字符串
     */
    String getMessage(String msgKey, Object... args);

    /**
     * 向玩家发送信息
     *
     * @param receiver 接收信息的玩家
     * @param type     信息类型
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    MessageSender message(Player receiver, String type, String msgKey, Object... args);

    /**
     * 向后台发送信息
     *
     * @param type   信息类型
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    MessageSender messageConsole(String type, String msgKey, Object... args);

    /**
     * 向玩家发送普通信息
     *
     * @param receiver 接收信息的玩家
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    MessageSender info(Player receiver, String msgKey, Object... args);

    /**
     * 向后台发送普通信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    MessageSender infoConsole(String msgKey, Object... args);

    /**
     * 向玩家发送警告信息
     *
     * @param receiver 接收信息的玩家
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    MessageSender warning(Player receiver, String msgKey, Object... args);

    /**
     * 向后台发送警告信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    MessageSender warningConsole(String msgKey, Object... args);

    /**
     * 向玩家发送错误信息
     *
     * @param receiver 接收信息的玩家
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    MessageSender error(Player receiver, String msgKey, Object... args);

    /**
     * 向后台发送错误信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    MessageSender errorConsole(String msgKey, Object... args);

    /**
     * 向玩家发送调试信息
     *
     * @param debugLevel 调试等级，仅当传入的调试等级小于等于设置的调试等级时才会发生调试信息
     * @param receiver   接收信息的玩家
     * @param msgKey     信息key
     * @param args       信息文本中的参数列表
     * @return 自身实例
     */
    MessageSender debug(int debugLevel, Player receiver, String msgKey, Object... args);

    /**
     * 向后台发送调试信息
     *
     * @param debugLevel 调试等级，仅当传入的调试等级小于等于设置的调试等级时才会发生调试信息
     * @param msgKey     信息key
     * @param args       信息文本中的参数列表
     * @return 自身实例
     */
    MessageSender debugConsole(int debugLevel, String msgKey, Object... args);

    /**
     * 设置调试等级
     *
     * @param debugLevel 调试等级
     * @return 自身实例
     */
    MessageSender setDebugLevel(int debugLevel);

    /**
     * 获取调试等级
     *
     * @return 调试等级
     */
    int getDebugLevel();
}
