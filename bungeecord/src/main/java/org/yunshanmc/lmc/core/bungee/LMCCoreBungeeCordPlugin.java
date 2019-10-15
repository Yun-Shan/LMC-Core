/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.bungee;

import org.yunshanmc.lmc.core.bungee.util.BungeeUtils;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.internal.LMCCoreUtils;

/**
 * LMCCore插件 BungeeCord端主类
 *
 * @author Yun-Shan
 */
public class LMCCoreBungeeCordPlugin extends BaseLMCBungeeCordPlugin {

    @Override
    protected void init() {
        this.useGroupMessage = true;
    }

    @Override
    public void onEnable() {
        LMCCoreUtils.setLMCCorePlugin(this);
        BungeeUtils.init();
    }

    @Override
    public void onDisable() {
        ExceptionHandler.stop();
    }
}
