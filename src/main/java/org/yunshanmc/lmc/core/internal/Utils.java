package org.yunshanmc.lmc.core.internal;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.utils.ReflectUtils;

public final class Utils {

    private Utils() {
    }// 禁止实例化

    private static LMCPlugin LMCCore;

    public static void setLMCCorePlugin(LMCPlugin LMCCore) {
        ReflectUtils.checkSafeCall();
        Utils.LMCCore = LMCCore;
    }

    public static LMCPlugin getLMCCorePlugin() {
        return Utils.LMCCore;
    }
}
