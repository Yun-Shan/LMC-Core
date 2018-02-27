/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.bukkit;

import org.yunshanmc.lmc.core.internal.Utils;

/**
 * //TODO
 */
public class LMCCoreBukkitPlugin extends LMCBukkitPlugin {

    @Override
    protected void init() {
        Utils.setLMCCorePlugin(this);
        this.useGroupMessage = true;
    }

    @Override
    public void onEnable() {
    }
}
