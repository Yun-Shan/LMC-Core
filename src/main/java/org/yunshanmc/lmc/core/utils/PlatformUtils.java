package org.yunshanmc.lmc.core.utils;

public final class PlatformUtils {

    private PlatformUtils(){}// 禁止实例化

    private static boolean Bukkit = false;
    private static boolean BungeeCord = false;
    static {
        try {
            Class.forName("org.bukkit.plugin.java.JavaPlugin");
            Bukkit = true;
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Class.forName("net.md_5.bungee.api.plugin.Plugin");
            BungeeCord = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static boolean isBukkit() {
        return Bukkit;
    }

    public static boolean isBungeeCord() {
        return BungeeCord;
    }
}
