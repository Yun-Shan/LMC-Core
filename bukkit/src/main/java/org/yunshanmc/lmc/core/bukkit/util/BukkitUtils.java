package org.yunshanmc.lmc.core.bukkit.util;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.yunshanmc.lmc.core.bukkit.gui.BukkitInvProvider;
import org.yunshanmc.lmc.core.bukkit.gui.sign.BukkitSignEditorProvider;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.gui.GuiFactory;
import org.yunshanmc.lmc.core.gui.sign.SignHelper;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.util.PlatformUtils;
import org.yunshanmc.lmc.core.util.ReflectUtils;

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
public final class BukkitUtils {

    private static volatile boolean inited = false;

    public static synchronized void init() {
        if (inited) {
            return;
        }
        ReflectUtils.checkSafeCall();

        PlatformUtils.setConsoleRawMessageSender(msg -> Bukkit.getConsoleSender().sendMessage(msg.split("\\n")));

        PluginManager pm = Bukkit.getPluginManager();
        PlatformUtils.setPluginGetter(pm::getPlugin);
        PlatformUtils.setPlayerNameGetter(id -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            return (player instanceof Player || player.hasPlayedBefore()) ? player.getName() : null;
        });

        if (!PlatformUtils.isInTest()) {
            GuiFactory.setInvProvider(new BukkitInvProvider());
            SignHelper.setEditorProvider(BukkitSignEditorProvider.getInstance());
        }

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
        List<Resource> resList = ReflectUtils.traceResources(stackTrace, "plugin.yml", reverse);
        if (!resList.isEmpty()) {
            PluginManager pm = Bukkit.getPluginManager();
            List<Plugin> result = new ArrayList<>();
            //noinspection Duplicates
            for (Resource res : resList) {
                try {
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                            new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8));
                    String name = yml.getString("name");
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
        boolean hasValid = !resList.isEmpty() && (!skipSelf || resList.size() > 1);
        if (hasValid) {
            return resList.get(skipSelf ? 1 : 0);
        }
        return null;
    }

    /**
     * 正版玩家头颅预载
     * <p>
     * 服务器需要通过Mojang API获取正版玩家头颅皮肤，可能导致服务器顿卡。
     * 通过提前异步加载皮肤解决
     *
     * @param ownerNames 头颅所属正版玩家名
     */
    @SuppressWarnings("deprecation")
    public static void skullPreload(String... ownerNames) {
        //TODO 改为手动访问Mojang API批量获取玩家信息的方式，避免被Mojang服务器ban IP
        Inventory inv = Bukkit.createInventory(null, 9);
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        for (String name : ownerNames) {
            meta.setOwner(name);
            item.setItemMeta(meta);
            inv.setItem(0, item);
        }
    }
}
