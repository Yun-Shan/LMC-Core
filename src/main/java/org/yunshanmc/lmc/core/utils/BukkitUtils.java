package org.yunshanmc.lmc.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bukkit相关工具
 */
public class BukkitUtils {

    /**
     * 根据调用栈追踪插件
     * <p>
     * 会通过每个调用栈Class尝试获取插件
     *
     * @param stackTrace 调用栈
     * @param duplicate  连续插件是否重复，如果多个连续调用栈是同一个插件时是否重复记录插件(注意：同插件在不连续的调用栈上不会去重)
     * @param reverse    由于调用栈是倒序的，该参数指定是否将倒序转为正序，true即转为正序，false即保持倒序
     * @return 追踪到的插件列表
     */
    public static List<Plugin> tracePlugins(StackTraceElement[] stackTrace, boolean duplicate, boolean reverse) {
        List<Resource> resList = ReflectUtils.traceResources(stackTrace, "plugin.yml", reverse);
        if (!resList.isEmpty()) {
            PluginManager pm = Bukkit.getPluginManager();
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
        return Collections.emptyList();
    }

    /**
     * 根据调用栈追踪插件
     * <p>
     * 会通过每个调用栈Class尝试获取插件，直到获取到第一个插件为止
     *
     * @param stackTrace 调用栈
     * @return 追踪到的调用栈上的第一个插件，获取失败或调用栈中没有插件时返回null
     */
    public static Plugin traceFirstPlugin(StackTraceElement[] stackTrace) {
        List<Resource> resList = ReflectUtils.traceResources(stackTrace, "plugin.yml", false);
        if (!resList.isEmpty()) {
            try {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(resList.get(0).getInputStream(), StandardCharsets.UTF_8));
                String plugin = yml.getString("name");
                return Bukkit.getPluginManager().getPlugin(plugin);
            } catch (IOException e) {
                ExceptionHandler.handle(e);
            }
        }
        return null;
    }
}
