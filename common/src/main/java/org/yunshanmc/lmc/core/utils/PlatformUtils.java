package org.yunshanmc.lmc.core.utils;

import com.google.common.base.Preconditions;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.config.bukkitcfg.file.YamlConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.internal.LMCCoreUtils;
import org.yunshanmc.lmc.core.resource.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PlatformUtils {

    private PlatformUtils(){}// 禁止实例化

    private static final PlatformType PLATFORM;
    static {
        PlatformType type;
        LMCPlugin lmc = LMCCoreUtils.getLMCCorePlugin();
        if (lmc != null) {
            switch (lmc.getClass().getName()) {
                case "org.yunshanmc.lmc.core.bukkit.LMCCoreBukkitPlugin": type = PlatformType.Bukkit; break;
                case "org.yunshanmc.lmc.core.bungee.LMCCoreBungeeCordPlugin": type = PlatformType.BungeeCord; break;
                default: type = PlatformType.Unknown; break;
            }
        } else {
            type = PlatformType.Unknown;
        }
        PLATFORM = type;
    }
    private static final Class<?> SENDER_CLASS;
    static {
        Class<?> senderCls;
        try {
            switch (PLATFORM) {
                case Bukkit: senderCls = Class.forName("org.bukkit.command.CommandSender"); break;
                case BungeeCord: senderCls = Class.forName("net.md_5.bungee.api.CommandSender"); break;
                default: senderCls = null;
            }
        } catch (ClassNotFoundException ignored) {
            senderCls = null;
        }
        SENDER_CLASS = senderCls;
    }
    private static final Class<?> PLAYER_CLASS;
    static {
        Class<?> playerCls;
        try {
            switch (PLATFORM) {
                case Bukkit: playerCls = Class.forName("org.bukkit.entity.Player"); break;
                case BungeeCord: playerCls = Class.forName("net.md_5.bungee.api.connection.ProxiedPlayer"); break;
                default: playerCls = null;
            }
        } catch (ClassNotFoundException ignored) {
            playerCls = null;
        }
        PLAYER_CLASS = playerCls;
    }

    private static Consumer<String> ConsoleRawMessageSender;
    private static Function<String, Object> PluginGetter;

    public static boolean isBukkit() {
        return PlatformType.Bukkit.equals(PLATFORM);
    }

    public static boolean isBungeeCord() {
        return PlatformType.BungeeCord.equals(PLATFORM);
    }

    public static PlatformType getPlatform() {
        return PLATFORM;
    }

    public static Class<?> getCommandSenderClass() {
        return SENDER_CLASS;
    }

    public static void checkPlayer(Object player) {
        Preconditions.checkNotNull(player);
        Preconditions.checkArgument(PLAYER_CLASS.isInstance(player));
    }

    public static void checkCommandSender(Object sender) {
        Preconditions.checkNotNull(sender);
        Preconditions.checkArgument(SENDER_CLASS.isInstance(sender));
    }

    public static synchronized void setConsoleRawMessageSender(Consumer<String> consoleRawMessageSender) {
        ReflectUtils.checkSafeCall();
        if (PlatformUtils.ConsoleRawMessageSender != null) throw new IllegalStateException();
        PlatformUtils.ConsoleRawMessageSender = consoleRawMessageSender;
    }

    public static void setPluginGetter(Function<String, Object> pluginGetter) {
        ReflectUtils.checkSafeCall();
        if (PlatformUtils.PluginGetter != null) throw new IllegalStateException();
        PlatformUtils.PluginGetter = pluginGetter;
    }

    public static Object getPlugin(String name) {
        return PlatformUtils.PluginGetter.apply(name);
    }

    public enum PlatformType {
        Bukkit,
        BungeeCord,

        Unknown
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
    public static List<String> tracePluginsName(StackTraceElement[] stackTrace, boolean duplicate, boolean reverse) {
        Function<String, List<String>> fetcher = fileName -> {
            List<Resource> resList = ReflectUtils.traceResources(stackTrace, fileName, reverse);
            if (!resList.isEmpty()) {
                List<String> result = new ArrayList<>();
                for (Resource res : resList) {
                    try {
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                            new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8));
                        String plugin = yml.getString("name");
                        if (plugin != null &&
                            (!duplicate || (result.isEmpty() || !plugin.equals(result.get(result.size() - 1)))))
                            result.add(plugin);
                    } catch (IOException e) {
                        ExceptionHandler.handle(e);
                    }
                }
            }
            return null;
        };

        List<String> result = null;
        if (isBungeeCord()) {
            result = fetcher.apply("bungee.yml");
        }
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
    public static String traceFirstPluginName(StackTraceElement[] stackTrace, boolean skipSelf) {
        List<String> resList = PlatformUtils.tracePluginsName(stackTrace, false, false);
        if (!resList.isEmpty() && (!skipSelf || resList.size() > 1)) {
            return resList.get(skipSelf ? 1 : 0);
        }
        return null;
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
    public static List<Object> tracePlugins(StackTraceElement[] stackTrace, boolean duplicate, boolean reverse) {
        Function<String, List<Object>> fetcher = fileName -> {
            List<Resource> resList = ReflectUtils.traceResources(stackTrace, fileName, reverse);
            if (!resList.isEmpty()) {
                List<Object> result = new ArrayList<>();
                for (Resource res : resList) {
                    try {
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                            new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8));
                        Object plugin = getPlugin(yml.getString("name"));
                        if (plugin != null &&
                            (!duplicate || (result.isEmpty() || !plugin.equals(result.get(result.size() - 1)))))
                            result.add(plugin);
                    } catch (IOException e) {
                        ExceptionHandler.handle(e);
                    }
                }
            }
            return null;
        };

        List<Object> result = null;
        if (isBungeeCord()) {
            result = fetcher.apply("bungee.yml");
        }
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
    public static Object traceFirstPlugin(StackTraceElement[] stackTrace, boolean skipSelf) {
        List<String> resList = PlatformUtils.tracePluginsName(stackTrace, false, false);
        if (!resList.isEmpty() && (!skipSelf || resList.size() > 1)) {
            return resList.get(skipSelf ? 1 : 0);
        }
        return null;
    }

    public static void sendRawConsoleMessage(String msg) {
        ConsoleRawMessageSender.accept(msg);
    }
}
