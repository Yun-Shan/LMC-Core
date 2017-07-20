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
    
    private static final DefaultMessageFormat INSTANCE = new DefaultMessageFormat();
    
    // 为了PAPIMessageFormat可以单例 故使用package-private而非private
    DefaultMessageFormat() {}
    
    public static DefaultMessageFormat getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String format(Player player, String msg, Object... args) {
        return null;// TODO
    }
}
