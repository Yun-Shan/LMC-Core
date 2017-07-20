/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import org.bukkit.entity.Player;

/**
 * //TODO 注释
 */
public interface MessageFormat {
    
    String format(Player player, String msg, Object... args);
}
