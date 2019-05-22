package org.yunshanmc.lmc.core.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.yunshanmc.lmc.core.CommonEnvironmentUtil;
import org.yunshanmc.lmc.core.bungee.util.BungeeUtils;
import org.yunshanmc.lmc.core.util.PlatformUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class EnvironmentUtil extends CommonEnvironmentUtil {

    private EnvironmentUtil() {
        // 禁止实例化
        throw new Error();
    }

    public static void mockBungeeCord() {
        try {
            f_platform.set(null, PlatformUtils.PlatformType.BungeeCord);
            f_SENDER_CLASS.set(null, CommandSender.class);
            f_PLAYER_CLASS.set(null, ProxiedPlayer.class);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        AtomicReference<PluginManager> pmar = new AtomicReference<>();
        Map<String, ServerInfo> servers = new HashMap<>();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ProxyServer.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            switch (method.getName()) {
                case "getPluginManager":
                    return pmar.get();
                case "getServers":
                    return servers;
                default:
                    return null;
            }
        });
        ProxyServer.setInstance((ProxyServer) enhancer.create());
        pmar.set(new PluginManager(ProxyServer.getInstance()));

        BungeeUtils.init();
    }

}
