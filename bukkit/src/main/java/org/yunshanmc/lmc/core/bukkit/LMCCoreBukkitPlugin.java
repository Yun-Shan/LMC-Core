/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.bukkit;

import org.yunshanmc.lmc.core.internal.LMCCoreUtils;

/**
 * LMCCore插件 BungeeCord端主类
 *
 * @author Yun-Shan
 */
public class LMCCoreBukkitPlugin extends BaseLMCBukkitPlugin {

    @Override
    protected void init() {
        LMCCoreUtils.setLMCCorePlugin(this);
        this.useGroupMessage = true;
    }

    @Override
    public void onEnable() {
    }
}
