/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.bukkit;

import org.bukkit.plugin.PluginManager;
import org.yunshanmc.lmc.core.bukkit.listener.GuiListener;
import org.yunshanmc.lmc.core.bukkit.util.BukkitUtils;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.internal.LMCCoreUtils;

/**
 * LMCCore插件 BungeeCord端主类
 *
 * @author Yun-Shan
 */
public class LMCCoreBukkitPlugin extends BaseLMCBukkitPlugin {

    @Override
    protected void init() {
        this.useGroupMessage = true;
    }

    @Override
    public void onEnable() {
        LMCCoreUtils.setLMCCorePlugin(this);
        BukkitUtils.init();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new GuiListener(), this);
    }

    @Override
    public void onDisable() {
        ExceptionHandler.stop();
    }
}
