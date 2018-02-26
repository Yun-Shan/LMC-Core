/*
 * Author: Yun-Shan
 * Date: 2017/06/23
 */
package org.yunshanmc.lmc.core.message;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
     * 获取信息字符串(BungeeCord)
     *
     * @param msgKey 信息key
     * @param player 玩家(BungeeCord)，用于玩家相关变量的格式化
     * @param args   信息文本中的参数列表
     * @return 格式化后的信息字符串
     */
    String getMessage(String msgKey, ProxiedPlayer player, Object... args);

    /**
     * 获取信息字符串(Bukkit)
     *
     * @param msgKey 信息key
     * @param player 玩家(Bukkit)，用于玩家相关变量的格式化
     * @param args   信息文本中的参数列表
     * @return 格式化后的信息字符串
     */
    String getMessage(String msgKey, Player player, Object... args);

    /**
     * 获取信息字符串(无玩家信息的格式化)
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     * @return 格式化后的信息字符串
     */
    String getMessage(String msgKey, Object... args);

    /**
     * 发送信息(Bukkit)
     *
     * @param receiver 信息接收者(Bukkit)
     * @param type     信息类型
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void message(CommandSender receiver, String type, String msgKey, Object... args);

    /**
     * 发送信息(BungeeCord)
     *
     * @param receiver 信息接收者(BungeeCord)
     * @param type     信息类型
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void message(net.md_5.bungee.api.CommandSender receiver, String type, String msgKey, Object... args);

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
     * 发送普通信息(Bukkit)
     *
     * @param receiver 信息接收者(Bukkit)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void info(CommandSender receiver, String msgKey, Object... args);

    /**
     * 发送普通信息(BungeeCord)
     *
     * @param receiver 信息接收者(BungeeCord)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void info(net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args);

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
     * 发送警告信息(Bukkit)
     *
     * @param receiver 信息接收者(Bukkit)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void warning(CommandSender receiver, String msgKey, Object... args);

    /**
     * 发送警告信息(BungeeCord)
     *
     * @param receiver 信息接收者(BungeeCord)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void warning(net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args);

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
     * 发送错误信息(Bukkit)
     *
     * @param receiver 信息接收者(Bukkit)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void error(CommandSender receiver, String msgKey, Object... args);

    /**
     * 发送错误信息(BungeeCord)
     *
     * @param receiver 信息接收者(BungeeCord)
     * @param msgKey   信息key
     * @param args     信息文本中的参数列表
     */
    void error(net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args);

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
     * 发送调试信息(Bukkit)
     *
     * @param debugLevel 调试等级，当且仅当传入的调试等级小于等于设置的调试等级时才会发生调试信息
     * @param receiver   信息接收者(Bukkit)
     * @param msgKey     信息key
     * @param args       信息文本中的参数列表
     */
    void debug(int debugLevel, CommandSender receiver, String msgKey, Object... args);

    /**
     * 发送调试信息(BungeeCord)
     *
     * @param debugLevel 调试等级，当且仅当传入的调试等级小于等于设置的调试等级时才会发生调试信息
     * @param receiver   信息接收者(BungeeCord)
     * @param msgKey     信息key
     * @param args       信息文本中的参数列表
     */
    void debug(int debugLevel, net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args);

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
