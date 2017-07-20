/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * //TODO 注释
 */
public class PAPIMessageFormat extends DefaultMessageFormat {
    
    private static final PAPIMessageFormat INSTANCE = new PAPIMessageFormat();
    
    private PAPIMessageFormat() {super();}
    
    public static PAPIMessageFormat getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String format(Player player, String msg, Object... args) {
        return PlaceholderAPI.setPlaceholders(player, super.format(player,  msg, args));
    }
}
