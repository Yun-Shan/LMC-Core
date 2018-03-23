package org.yunshanmc.lmc.core.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.yunshanmc.lmc.core.bukkit.command.BukkitLMCCommandSender;
import org.yunshanmc.lmc.core.command.AbstractParameterConverter;
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

/**
 * Bukkit相关工具
 *
 * @author Yun-Shan
 */
public class BukkitUtils {

    static {
        PlatformUtils.setConsoleRawMessageSender(msg -> Bukkit.getConsoleSender().sendMessage(msg.split("\\n")));

        AbstractParameterConverter.registerLMCSenderClass(BukkitLMCCommandSender.class);

        PluginManager pm = Bukkit.getPluginManager();
        PlatformUtils.setPluginGetter(pm::getPlugin);
    }

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
                            (duplicate || (result.isEmpty() || plugin != result.get(result.size() - 1))))
                        result.add(plugin);
                } catch (IOException e) {
                    ExceptionHandler.handle(e);
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    /**
     * 根据调用栈追踪插件
     * <p>
     * 会通过每个调用栈Class尝试获取插件，直到获取到第一个插件为止
     *
     * @param stackTrace 调用栈
     * @param skipSelf   是否要跳过调用者
     * @return 追踪到的调用栈上的第一个插件，获取失败或调用栈中没有插件时返回null
     */
    public static Plugin traceFirstPlugin(StackTraceElement[] stackTrace, boolean skipSelf) {
        List<Plugin> resList = BukkitUtils.tracePlugins(stackTrace, false, false);
        if (!resList.isEmpty() && (!skipSelf || resList.size() > 1)) {
            return resList.get(skipSelf ? 1 : 0);
        }
        return null;
    }
}
