package org.yunshanmc.lmc.core.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.yunshanmc.lmc.core.CommonEnvironmentUtil;
import org.yunshanmc.lmc.core.MockPlugin;
import org.yunshanmc.lmc.core.bukkit.config.BukkitConfigManager;
import org.yunshanmc.lmc.core.bukkit.util.BukkitUtils;
import org.yunshanmc.lmc.core.util.PlatformUtils;

import java.lang.reflect.Proxy;
import java.util.logging.Logger;

public final class EnvironmentUtil extends CommonEnvironmentUtil {

    private EnvironmentUtil() {
        // 禁止实例化
        throw new Error();
    }

    private static boolean hasMocked;

    public static void mockBukkit() {
        if (hasMocked) {
            return;
        }
        try {
            f_platform.set(null, PlatformUtils.PlatformType.Bukkit);
            f_SENDER_CLASS.set(null, CommandSender.class);
            f_PLAYER_CLASS.set(null, Player.class);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        MockPlugin.ConfigManagerNewer = BukkitConfigManager::new;
        Bukkit.setServer((Server) Proxy.newProxyInstance(Bukkit.class.getClassLoader(), new Class[]{Server.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "getName":
                        return "JUnit测试服务器";
                    case "getVersion":
                        return "JUnit测试版本";
                    case "getBukkitVersion":
                        return "JUnit测试Bukkit版本";
                    case "getPluginManager":
                        return Proxy.newProxyInstance(Bukkit.class.getClassLoader(), new Class[]{PluginManager.class}, (
                            (proxy1, method1, args1) -> null));
                    case "getLogger":
                        return Logger.getLogger("JUnitTest");
                    default:
                        return null;
                }
            })
        );
        BukkitUtils.init();
        hasMocked = true;
    }

}
