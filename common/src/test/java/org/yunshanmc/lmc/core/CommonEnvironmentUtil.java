package org.yunshanmc.lmc.core;

import org.yunshanmc.lmc.core.util.PlatformUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CommonEnvironmentUtil {

    protected CommonEnvironmentUtil() {
        // 禁止实例化
        throw new Error();
    }

    protected static Field f_platform;
    protected static Field f_SENDER_CLASS;
    protected static Field f_PLAYER_CLASS;
    static {
        try {
            Field f = PlatformUtils.class.getDeclaredField("PLATFORM");
            Field f_m = Field.class.getDeclaredField("modifiers");
            f_m.setAccessible(true);
            f_m.setInt(f, Modifier.PUBLIC | Modifier.STATIC);
            f.setAccessible(true);
            f_platform = f;

            f = PlatformUtils.class.getDeclaredField("SENDER_CLASS");
            f_m = Field.class.getDeclaredField("modifiers");
            f_m.setAccessible(true);
            f_m.setInt(f, Modifier.PUBLIC | Modifier.STATIC);
            f.setAccessible(true);
            f_SENDER_CLASS = f;

            f = PlatformUtils.class.getDeclaredField("PLAYER_CLASS");
            f_m = Field.class.getDeclaredField("modifiers");
            f_m.setAccessible(true);
            f_m.setInt(f, Modifier.PUBLIC | Modifier.STATIC);
            f.setAccessible(true);
            f_PLAYER_CLASS = f;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
