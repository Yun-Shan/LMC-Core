package org.yunshanmc.lmc.core.internal;

import org.yunshanmc.lmc.core.LMCPlugin;

public class LMCCoreUtils {

    private LMCCoreUtils() {
    }// 禁止实例化

    private static LMCPlugin LMCCore;

    public static synchronized void setLMCCorePlugin(LMCPlugin LMCCore) {
        if (LMCCoreUtils.LMCCore != null) throw new IllegalStateException();
        LMCCoreUtils.LMCCore = LMCCore;
    }

    public static LMCPlugin getLMCCorePlugin() {
        return LMCCoreUtils.LMCCore;
    }
}
