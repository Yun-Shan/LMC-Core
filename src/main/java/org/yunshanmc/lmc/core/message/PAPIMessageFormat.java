/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;

/**
 * //TODO 注释
 */
public class PAPIMessageFormat extends DefaultMessageFormat {

    public PAPIMessageFormat(MessageContext context) { super(context); }

    @Override
    public String format(Player player, String msg, Object... args) {
        return PlaceholderAPI.setPlaceholders(player, super.format(player, msg, args));
    }
}
