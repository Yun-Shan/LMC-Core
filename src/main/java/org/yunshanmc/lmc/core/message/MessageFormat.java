/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;

/**
 * //TODO 注释
 */
public interface MessageFormat {
    String format(Player player, String msg, Object... args);

    String format(ProxiedPlayer player, String msg, Object... args);
}
