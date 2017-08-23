/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import org.bukkit.entity.Player;

/**
 * //TODO 注释
 */
public class DefaultMessageFormat implements MessageFormat {

    @Override
    public String format(Player player, String msg, Object... args) {
        for (int i = 0; i < args.length; i++) {
            msg = msg.replace("{" + (i + 1) + '}', String.valueOf(args[i]));
        }
        return msg;
    }
}
