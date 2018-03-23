package org.yunshanmc.lmc.bukkit;

import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;
import org.yunshanmc.lmc.core.bukkit.utils.BukkitUtils;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;

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

    public static void mockBukkit() {
        try {
            f_platform.set(null, PlatformUtils.PlatformType.Bukkit);
            f_SENDER_CLASS.set(null, CommandSender.class);
            f_PLAYER_CLASS.set(null, Player.class);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Bukkit.setServer((Server) Proxy.newProxyInstance(Bukkit.class.getClassLoader(), new Class[]{Server.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "getName":
                        return "测试服务器";
                    case "getVersion":
                        return "测试版本";
                    case "getBukkitVersion":
                        return "测试Bukkit版本";
                    case "getPluginManager":
                        return Proxy.newProxyInstance(Bukkit.class.getClassLoader(), new Class[]{PluginManager.class}, (
                            (proxy1, method1, args1) -> null));
                    case "getLogger":
                        return Logger.getLogger("Test");
                    default:
                        return null;
                }
            })
        );
        try {
            Class.forName(BukkitUtils.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
