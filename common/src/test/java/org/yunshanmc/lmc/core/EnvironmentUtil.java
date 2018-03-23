package org.yunshanmc.lmc.core;

import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class EnvironmentUtil {

    private EnvironmentUtil() {
        // 禁止实例化
        throw new Error();
    }

    private static Field f_platform;
    private static Field f_SENDER_CLASS;
    private static Field f_PLAYER_CLASS;
    static {
        try {
            Field f = PlatformUtils.class.getDeclaredField("PLATFORM");
            Field f_m = Field.class.getDeclaredField("modifiers");
            f_m.setAccessible(true);
            f_m.setInt(f, Modifier.PUBLIC | Modifier.STATIC);
            f.setAccessible(true);
            f_platform = f;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void mockBukkit() {
        try {
            f_platform.set(null, PlatformUtils.PlatformType.Bukkit);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
