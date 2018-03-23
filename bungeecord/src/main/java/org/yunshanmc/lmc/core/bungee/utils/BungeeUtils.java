package org.yunshanmc.lmc.core.bungee.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.yunshanmc.lmc.core.bungee.command.BungeeLMCCommandSender;
import org.yunshanmc.lmc.core.command.AbstractParameterConverter;
import org.yunshanmc.lmc.core.config.bukkitcfg.file.YamlConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.utils.PlatformUtils;
import org.yunshanmc.lmc.core.utils.ReflectUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Bungee相关工具
 *
 * @author Yun-Shan
 */
public class BungeeUtils {

    static {
        PlatformUtils.setConsoleRawMessageSender(msg -> ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(msg)));

        AbstractParameterConverter.registerLMCSenderClass(BungeeLMCCommandSender.class);

        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        PlatformUtils.setPluginGetter(pm::getPlugin);
    }

    /**
     * 根据调用栈追踪插件
     * <p>
     * 会通过每个调用栈Class尝试获取插件
     *
     * @param stackTrace 调用栈
     * @param duplicate  连续插件是否重复，如果多个连续调用栈是同一个插件时是否重复记录插件(注意：同插件在不连续的调用栈上不会去重)
     * @return 追踪到的插件列表
     */
    public static List<Plugin> tracePlugins(StackTraceElement[] stackTrace, boolean duplicate) {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        Function<String, List<Plugin>> fetcher = fileName -> {
            List<Resource> resList = ReflectUtils.traceResources(stackTrace, fileName, false);
            if (!resList.isEmpty()) {
                List<Plugin> result = new ArrayList<>();
                for (Resource res : resList) {
                    try {
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                            new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8));
                        Plugin plugin = pm.getPlugin(yml.getString("name"));
                        if (plugin != null &&
                            (!duplicate || (result.isEmpty() || plugin != result.get(result.size() - 1))))
                            result.add(plugin);
                    } catch (IOException e) {
                        ExceptionHandler.handle(e);
                    }
                }
            }
            return null;
        };

        List<Plugin> result = fetcher.apply("bungee.yml");
        if (result == null) fetcher.apply("plugin.yml");
        if (result == null) result = Collections.emptyList();
        return result;
    }

    /**
     * 根据调用栈追踪插件
     * <p>
     * 会通过每个调用栈Class尝试获取插件，直到获取到第一个插件为止
     *
     * @param stackTrace 调用栈
     * @return 追踪到的调用栈上的第一个插件
     */
    public static Plugin traceFirstPlugin(StackTraceElement[] stackTrace) {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        Function<String, Plugin> fetcher = fileName -> {
            List<Resource> resList = ReflectUtils.traceResources(stackTrace, fileName, false);
            if (!resList.isEmpty()) {
                try {
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(resList.get(0).getInputStream(), StandardCharsets.UTF_8));
                    String plugin = yml.getString("name");
                    return pm.getPlugin(plugin);
                } catch (IOException e) {
                    ExceptionHandler.handle(e);
                    return null;
                }
            }
            return null;
        };

        Plugin result = fetcher.apply("bungee.yml");
        if (result == null) fetcher.apply("plugin.yml");
        return result;
    }
}
