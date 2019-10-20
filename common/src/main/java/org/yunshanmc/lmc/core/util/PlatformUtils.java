package org.yunshanmc.lmc.core.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.yaml.snakeyaml.Yaml;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.internal.LMCCoreUtils;
import org.yunshanmc.lmc.core.resource.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 平台相关工具.
 * <p>
 *
 * @author Yun-Shan
 */
public final class PlatformUtils {

    private PlatformUtils() {
        // 禁止实例化
    }

    private static final PlatformType PLATFORM;
    private static final boolean IN_TEST;

    static {
        PlatformType type;
        LMCPlugin lmc = LMCCoreUtils.getLMCCorePlugin();
        if (lmc != null) {
            switch (lmc.getClass().getName()) {
                case "org.yunshanmc.lmc.core.bukkit.LMCCoreBukkitPlugin":
                    type = PlatformType.Bukkit;
                    break;
                case "org.yunshanmc.lmc.core.bungee.LMCCoreBungeeCordPlugin":
                    type = PlatformType.BungeeCord;
                    break;
                default:
                    type = PlatformType.Unknown;
                    break;
            }
        } else {
            type = PlatformType.Unknown;
        }
        PLATFORM = type;
        boolean test = false;
        try {
            assert false;
        } catch (AssertionError e) {
            test = true;
        }
        //noinspection ConstantConditions
        IN_TEST = test;
    }

    public static boolean isInTest() {
        return IN_TEST;
    }

    private static final Class<?> SENDER_CLASS;

    static {
        Class<?> senderCls;
        try {
            switch (PLATFORM) {
                case Bukkit:
                    senderCls = Class.forName("org.bukkit.command.CommandSender");
                    break;
                case BungeeCord:
                    senderCls = Class.forName("net.md_5.bungee.api.CommandSender");
                    break;
                default:
                    senderCls = null;
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
                case Bukkit:
                    playerCls = Class.forName("org.bukkit.entity.Player");
                    break;
                case BungeeCord:
                    playerCls = Class.forName("net.md_5.bungee.api.connection.ProxiedPlayer");
                    break;
                default:
                    playerCls = null;
            }
        } catch (ClassNotFoundException ignored) {
            playerCls = null;
        }
        PLAYER_CLASS = playerCls;
    }

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

    private static Consumer<String> ConsoleRawMessageSender;
    private static Function<String, Object> PluginGetter;
    private static Function<UUID, String> PlayerNameGetter;

    public static synchronized void setConsoleRawMessageSender(Consumer<String> consoleRawMessageSender) {
        ReflectUtils.checkSafeCall();
        if (PlatformUtils.ConsoleRawMessageSender != null) {
            throw new IllegalStateException();
        }
        PlatformUtils.ConsoleRawMessageSender = consoleRawMessageSender;
    }

    public static void setPluginGetter(Function<String, Object> pluginGetter) {
        ReflectUtils.checkSafeCall();
        if (PlatformUtils.PluginGetter != null) {
            throw new IllegalStateException();
        }
        PlatformUtils.PluginGetter = pluginGetter;
    }

    public static void setPlayerNameGetter(Function<UUID, String> playerNameGetter) {
        ReflectUtils.checkSafeCall();
        if (PlatformUtils.PlayerNameGetter != null) {
            throw new IllegalStateException();
        }
        PlatformUtils.PlayerNameGetter = playerNameGetter;
    }

    public static Object getPlugin(String name) {
        return PlatformUtils.PluginGetter.apply(name);
    }

    /**
     * 根据UUID获取玩家名
     * <p>
     * 注意：只有玩家曾经进入过服务器才能获取，如果对应UUID的玩家从未进入过服务器则返回null
     *
     * @param uuid 玩家UUID
     * @return 玩家名
     */
    public static String getPlayerName(UUID uuid) {
        return PlatformUtils.PlayerNameGetter.apply(uuid);
    }

    public enum PlatformType {
        /**
         * 基于Bukkit的服务端.
         * <p>
         * CraftBukkit, Cauldron及所有基于这些服务端开发的服务端(Spigot, PaperSpigot, KCauldron等)
         */
        Bukkit,
        /**
         * BungeeCord服务端.
         */
        BungeeCord,

        /**
         * 未知服务端.
         * <p>
         * 若获取到的平台是Unknown则说明出现了bug
         */
        Unknown
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
    public static List<String> tracePluginsName(StackTraceElement[] stackTrace, boolean duplicate, boolean reverse) {
        Function<String, List<String>> fetcher = fileName -> {
            List<Resource> resList = ReflectUtils.traceResources(stackTrace, fileName, reverse);
            if (!resList.isEmpty()) {
                List<String> result = new ArrayList<>();
                for (Resource res : resList) {
                    try {
                        Map<String, String> map = new Yaml().load(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8));
                        String plugin = map.get("name");
                        if (Strings.isNullOrEmpty(plugin)) {
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

        List<String> result = null;
        if (isBungeeCord()) {
            result = fetcher.apply("bungee.yml");
        }
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
    public static String traceFirstPluginName(StackTraceElement[] stackTrace, boolean skipSelf) {
        List<String> resList = PlatformUtils.tracePluginsName(stackTrace, false, false);
        boolean hasValid = !resList.isEmpty() && (!skipSelf || resList.size() > 1);
        if (hasValid) {
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
     * @param reverse    由于调用栈是倒序的，该参数指定是否将倒序转为正序，true即转为正序，false即保持倒序
     * @return 追踪到的插件列表
     */
    public static List<Object> tracePlugins(StackTraceElement[] stackTrace, boolean duplicate, boolean reverse) {
        Function<String, List<Object>> fetcher = fileName -> {
            List<Resource> resList = ReflectUtils.traceResources(stackTrace, fileName, reverse);
            if (!resList.isEmpty()) {
                List<Object> result = new ArrayList<>();
                for (Resource res : resList) {
                    try {
                        Map<String, String> map = new Yaml().load(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8));
                        Object plugin = getPlugin(map.get("name"));
                        boolean canAdd = true;
                        if (!duplicate) {
                            canAdd = result.isEmpty() || !plugin.equals(result.get(result.size() - 1));
                        }
                        if (plugin != null && canAdd) {
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

        List<Object> result = null;
        if (isBungeeCord()) {
            result = fetcher.apply("bungee.yml");
        }
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
    public static Object traceFirstPlugin(StackTraceElement[] stackTrace, boolean skipSelf) {
        List<Object> resList = PlatformUtils.tracePlugins(stackTrace, false, false);
        boolean hasValid = !resList.isEmpty() && (!skipSelf || resList.size() > 1);
        if (hasValid) {
            return resList.get(skipSelf ? 1 : 0);
        }
        return null;
    }

    public static void sendRawConsoleMessage(String msg) {
        ConsoleRawMessageSender.accept(msg);
    }
}
