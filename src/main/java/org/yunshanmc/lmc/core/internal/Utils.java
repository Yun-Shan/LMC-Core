package org.yunshanmc.lmc.core.internal;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.utils.PlatformUtils;
import org.yunshanmc.lmc.core.utils.ReflectUtils;

public final class Utils {

    private Utils() {
    }// 禁止实例化

    private static LMCPlugin LMCCore;

    public static synchronized void setLMCCorePlugin(LMCPlugin LMCCore) {
        if (Utils.LMCCore != null) throw new IllegalStateException();
        Utils.LMCCore = LMCCore;
    }

    public static LMCPlugin getLMCCorePlugin() {
        return Utils.LMCCore;
    }
}
