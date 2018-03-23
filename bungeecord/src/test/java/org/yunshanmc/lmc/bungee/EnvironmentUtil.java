package org.yunshanmc.lmc.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.sf.cglib.proxy.*;
import org.yunshanmc.lmc.core.bungee.util.BungeeUtils;
import org.yunshanmc.lmc.core.util.PlatformUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;

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
            f_PLAYER_CLASS.set(null, ProxiedPlayer.class);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        AtomicReference<PluginManager> pmar = new AtomicReference<>();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ProxyServer.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if ("getPluginManager".equals(method.getName())) {
                return pmar.get();
            }
            return null;
        });
        ProxyServer.setInstance((ProxyServer) enhancer.create());
        pmar.set(new PluginManager(ProxyServer.getInstance()));

        try {
            Class.forName(BungeeUtils.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
