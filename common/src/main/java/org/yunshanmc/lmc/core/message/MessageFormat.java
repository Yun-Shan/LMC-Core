/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

/**
 * //TODO 注释
 */
public interface MessageFormat {

    default String format(Object player, String msg, Object... args) {
        return this.format(msg, args);
    }

    String format(String msg, Object... args);
}
