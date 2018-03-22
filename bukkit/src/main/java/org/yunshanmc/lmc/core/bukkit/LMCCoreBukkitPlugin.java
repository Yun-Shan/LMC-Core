/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.bukkit;

import org.yunshanmc.lmc.core.internal.LMCCoreUtils;

/**
 * //TODO
 */
public class LMCCoreBukkitPlugin extends LMCBukkitPlugin {

    @Override
    protected void init() {
        LMCCoreUtils.setLMCCorePlugin(this);
        this.useGroupMessage = true;
    }

    @Override
    public void onEnable() {
    }
}
