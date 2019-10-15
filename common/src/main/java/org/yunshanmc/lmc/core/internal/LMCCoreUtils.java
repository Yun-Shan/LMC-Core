package org.yunshanmc.lmc.core.internal;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.util.ReflectUtils;

/**
 * @author Yun-Shan
 */
public class LMCCoreUtils {

    private LMCCoreUtils() {
        // 禁止实例化
        throw new Error();
    }

    private static LMCPlugin LMCCore;

    public static synchronized void setLMCCorePlugin(LMCPlugin LMCCore) {
        ReflectUtils.checkSafeCall();
        if (LMCCoreUtils.LMCCore != null) {
            throw new IllegalStateException();
        }
        LMCCoreUtils.LMCCore = LMCCore;
    }

    public static LMCPlugin getLMCCorePlugin() {
        if (ReflectUtils.isInTest() || LMCCoreUtils.LMCCore.getClass().getProtectionDomain().equals(LMCCoreUtils.class.getProtectionDomain())) {
            return LMCCoreUtils.LMCCore;
        }
        throw new IllegalStateException();
    }
}
