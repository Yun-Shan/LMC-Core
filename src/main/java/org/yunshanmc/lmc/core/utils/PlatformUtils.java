package org.yunshanmc.lmc.core.utils;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.internal.Utils;

public final class PlatformUtils {

    private PlatformUtils(){}// 禁止实例化

    private static final PlatformType PLATFORM;

    static {
        LMCPlugin lmc = Utils.getLMCCorePlugin();
        PlatformType type;
        switch (lmc.getClass().getName()) {
            case "org.yunshanmc.lmc.core.bukkit.LMCCoreBukkitPlugin": type = PlatformType.Bukkit; break;
            case "org.yunshanmc.lmc.core.bungee.LMCCoreBungeeCordPlugin": type = PlatformType.BungeeCord; break;
            case "org.yunshanmc.lmc.core.MockPlugin": type = PlatformType.Test; break;
            default: type = PlatformType.Unknown;
        }
        PLATFORM = type;
    }

    public static boolean isBukkit() {
        return PlatformType.Bukkit.equals(PLATFORM);
    }

    public static boolean isBungeeCord() {
        return PlatformType.BungeeCord.equals(PLATFORM);
    }

    public static boolean isTest() {
        return PlatformType.Test.equals(PLATFORM);
    }

    public static PlatformType getPlatform() {
        return PLATFORM;
    }

    public enum PlatformType {
        Bukkit,
        BungeeCord,
        Test,

        Unknown
    }
}
