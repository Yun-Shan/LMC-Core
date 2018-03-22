/*
 * Author: Yun-Shan
 * Date: 2017/06/23
 */
package org.yunshanmc.lmc.core.message;

import java.util.UUID;

/**
 * 信息发送者，用于向后台/玩家发送信息
 */
public interface MessageSender {

    /**
     * 获取信息字符串(根据平台自适应)
     *
     * @param msgKey 信息key
     * @param playerId 玩家ID(根据平台自适应)，用于玩家相关变量的格式化
     * @param args   信息文本中的参数列表
     * @return 格式化后的信息字符串
     */
    String getMessage(String msgKey, UUID playerId, Object... args);

    /**
     * 获取信息字符串
     *
     * @param msgKey 信息key
     * @param player 玩家(必须是相应服务端的玩家对象)，用于玩家相关变量的格式化
     * @param args   信息文本中的参数列表
     * @return 格式化后的信息字符串
     */
    String getMessage(String msgKey, Object player, Object... args);

    /**
     * 获取信息字符串(无玩家信息的格式化)
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     * @return 格式化后的信息字符串
     */
    String getMessage(String msgKey, Object... args);

    /**
     * 发送信息
     *
     * @param receiver 信息接收者(必须是相应服务端的CommandSender对象)
     * @param type     信息类型
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void message(Object receiver, String type, String msgKey, Object... args);

    /**
     * 发送信息(根据平台自适应)
     *
     * @param playerId 信息接收玩家的ID(根据平台自适应)
     * @param type     信息类型
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void message(UUID playerId, String type, String msgKey, Object... args);

    /**
     * 向后台发送信息
     *
     * @param type   信息类型
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    void messageConsole(String type, String msgKey, Object... args);

    /**
     * 发送普通信息
     *
     * @param receiver 信息接收者(必须是相应服务端的CommandSender对象)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void info(Object receiver, String msgKey, Object... args);

    /**
     * 向玩家发送普通信息(根据平台自适应)
     *
     * @param playerId 信息接收玩家的ID(根据平台自适应)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void info(UUID playerId, String msgKey, Object... args);

    /**
     * 向后台发送普通信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    void infoConsole(String msgKey, Object... args);

    /**
     * 发送警告信息
     *
     * @param receiver 信息接收者(必须是相应服务端的CommandSender对象)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void warning(Object receiver, String msgKey, Object... args);

    /**
     * 向玩家发送警告信息(根据平台自适应)
     *
     * @param playerId 信息接收玩家的ID(根据平台自适应)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void warning(UUID playerId, String msgKey, Object... args);

    /**
     * 向后台发送警告信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    void warningConsole(String msgKey, Object... args);

    /**
     * 发送错误信息
     *
     * @param receiver 信息接收者(必须是相应服务端的CommandSender对象)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void error(Object receiver, String msgKey, Object... args);

    /**
     * 向玩家发送错误信息(根据平台自适应)
     *
     * @param playerId 接收信息玩家的ID(根据平台自适应)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void error(UUID playerId, String msgKey, Object... args);

    /**
     * 向后台发送错误信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    void errorConsole(String msgKey, Object... args);

    /**
     * 发送调试信息
     *
     * @param debugLevel 调试等级，当且仅当传入的调试等级小于等于设置的调试等级时才会发生调试信息
     * @param receiver   信息接收者(必须是相应服务端的CommandSender对象)
     * @param msgKey     信息key
     * @param args       信息文本中的参数列表
     */
    void debug(int debugLevel, Object receiver, String msgKey, Object... args);

    /**
     * 向玩家发送调试信息(根据平台自适应)
     *
     * @param debugLevel 调试等级，当且仅当传入的调试等级小于等于设置的调试等级时才会发生调试信息
     * @param playerId   接收信息玩家的ID(根据平台自适应)
     * @param msgKey     信息key
     * @param args       信息文本中的参数列表
     */
    void debug(int debugLevel, UUID playerId, String msgKey, Object... args);

    /**
     * 向后台发送调试信息
     *
     * @param debugLevel 调试等级，当且仅当传入的调试等级小于等于设置的调试等级时才会发生调试信息
     * @param msgKey     信息key
     * @param args       信息文本中的参数列表
     */
    void debugConsole(int debugLevel, String msgKey, Object... args);

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
