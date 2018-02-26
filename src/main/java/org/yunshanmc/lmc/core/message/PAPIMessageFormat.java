/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

/**
 * PlaceholderAPI 格式化
 */
public class PAPIMessageFormat extends DefaultMessageFormat {

    private static boolean isPlaceholderAPIEnable = false;

    static {
        if (PlatformUtils.isBukkit() && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            isPlaceholderAPIEnable = true;
        }
    }

    public PAPIMessageFormat(MessageContext context) {
        super(context);
    }

    @Override
    public String format(Player player, String msg, Object... args) {
        msg = super.format(player, msg, args);
        if (isPlaceholderAPIEnable) msg = PlaceholderAPI.setPlaceholders(player, msg);
        return msg;
    }
}
