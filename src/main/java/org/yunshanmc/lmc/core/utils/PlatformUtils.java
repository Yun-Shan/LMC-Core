package org.yunshanmc.lmc.core.utils;

public final class PlatformUtils {

    private PlatformUtils(){}// 禁止实例化

    private static PlatformType platform;

    static {
        try {
            Class.forName("org.bukkit.plugin.java.JavaPlugin");
            platform = PlatformType.Bukkit;
        } catch (ClassNotFoundException ignored) {
        }
        if (platform == null) {
            try {
                Class.forName("net.md_5.bungee.api.plugin.Plugin");
                platform = PlatformType.BungeeCord;
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (platform == null) platform = PlatformType.Unknown;
    }

    public static boolean isBukkit() {
        return platform == PlatformType.Bukkit;
    }

    public static boolean isBungeeCord() {
        return platform == PlatformType.BungeeCord;
    }

    public static boolean isTest() {
        try {
            return !PlatformUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile().contains(".jar");
        } catch (Throwable t) {
            return false;
        }
    }

    public static PlatformType getPlatformType() {
        return platform;
    }

    public enum PlatformType {
        Bukkit,
        BungeeCord,

        Unknown
    }
}
