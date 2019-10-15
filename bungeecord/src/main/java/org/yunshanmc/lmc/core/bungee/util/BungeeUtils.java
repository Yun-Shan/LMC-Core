package org.yunshanmc.lmc.core.bungee.util;

import com.google.common.base.Strings;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.yaml.snakeyaml.Yaml;
import org.yunshanmc.lmc.core.bungee.command.BungeeLMCCommandSender;
import org.yunshanmc.lmc.core.command.AbstractParameterConverter;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.util.PlatformUtils;
import org.yunshanmc.lmc.core.util.ReflectUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Bungee相关工具
 *
 * @author Yun-Shan
 */
public class BungeeUtils {

    private static volatile boolean inited = false;

    public static synchronized void init() {
        if (inited) {
            return;
        }
        ReflectUtils.checkSafeCall();

        PlatformUtils.setConsoleRawMessageSender(msg -> ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(msg)));

        AbstractParameterConverter.registerLMCSenderClass(BungeeLMCCommandSender.class);

        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        PlatformUtils.setPluginGetter(pm::getPlugin);
        PlatformUtils.setPlayerNameGetter(id -> {
            // TODO 文件缓存的方式保存UUID对应玩家名
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(id);
            return player != null ? player.getName() : null;
        });

        inited = true;
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
        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        Function<String, List<Plugin>> fetcher = fileName -> {
            List<Resource> resList = ReflectUtils.traceResources(stackTrace, fileName, reverse);
            if (!resList.isEmpty()) {
                List<Plugin> result = new ArrayList<>();
                //noinspection Duplicates
                for (Resource res : resList) {
                    try {
                        Map<String, String> map = new Yaml().load(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8));
                        String name = map.get("name");
                        if (Strings.isNullOrEmpty(name)) {
                            continue;
                        }
                        Plugin plugin = pm.getPlugin(name);
                        if (plugin == null) {
                            continue;
                        }
                        boolean canAdd = true;
                        if (!duplicate) {
                            canAdd = result.isEmpty() || !plugin.equals(result.get(result.size() - 1));
                        }
                        if (canAdd) {
                            result.add(plugin);
                        }
                    } catch (IOException e) {
                        ExceptionHandler.handle(e);
                    }
                }
                return result;
            }
            return null;
        };

        List<Plugin> result = fetcher.apply("bungee.yml");
        if (result == null) {
            result = fetcher.apply("plugin.yml");
        }
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    /**
     * 根据调用栈追踪插件
     * <p>
     * 会通过每个调用栈Class尝试获取插件，直到获取到第一个插件为止
     *
     * @param stackTrace 调用栈
     * @param skipSelf   是否要跳过调用者
     * @return 追踪到的调用栈上的第一个插件
     */
    public static Plugin traceFirstPlugin(StackTraceElement[] stackTrace, boolean skipSelf) {
        List<Plugin> resList = BungeeUtils.tracePlugins(stackTrace, false, false);
        boolean hasValid = !resList.isEmpty() && (!skipSelf || resList.size() > 1);
        if (hasValid) {
            return resList.get(skipSelf ? 1 : 0);
        }
        return null;
    }
}
