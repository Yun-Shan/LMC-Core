/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

/**
 * 信息格式化器.
 * <p>
 *
 * @author Yun-Shan
 */
public interface MessageFormat {

    /**
     * 为玩家格式化信息.
     * <p>
     *
     * @param player 获取玩家信息时需要用到的玩家，不需要可以传null
     * @param msg 原始信息
     * @param args 格式化参数
     * @return 格式化后的信息
     */
    default String formatForPlayer(Object player, String msg, Object... args) {
        return this.format(msg, args);
    }

    /**
     * 格式化信息.
     * <p>
     * 相当于调用<code>{@link #formatForPlayer(Object, String, Object...) format}(null, msg, args)</code>
     *
     * @param msg 原始信息
     * @param args 格式化参数
     * @return 格式化后的信息
     */
    String format(String msg, Object... args);
}
