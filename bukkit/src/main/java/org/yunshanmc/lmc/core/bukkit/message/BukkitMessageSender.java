package org.yunshanmc.lmc.core.bukkit.message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yunshanmc.lmc.core.message.BaseMessageSender;
import org.yunshanmc.lmc.core.message.DefaultMessageFormat;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.util.PlatformUtils;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author Yun-Shan
 */
public class BukkitMessageSender extends BaseMessageSender {

    private static final Player FAKE_PLAYER_BUKKIT;

    static {
        FAKE_PLAYER_BUKKIT = (Player) Proxy.newProxyInstance(
            DefaultMessageFormat.class.getClassLoader(),
            new Class<?>[]{Player.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "sendMessage":
                        if (args[0] instanceof String) {
                            Bukkit.getConsoleSender().sendMessage("[LMC-FakePlayer]" + args[0]);
                        } else if (args[0] instanceof String[]) {
                            for (String msg : (String[]) args[0]) {
                                Bukkit.getConsoleSender().sendMessage("[LMC-FakePlayer]" + msg);
                            }
                        }
                        return null;
                    case "getName":
                    case "getDisplayName":
                    case "getCustomName":
                    case "getPlayerListName":
                        return "[$LMC-FakePlayer$]";
                    default:
                        throw new UnsupportedOperationException();
                }
            });
    }

    public BukkitMessageSender(MessageManager messageManager) {
        super(messageManager);
    }

    @Override
    public String getMessage(String msgKey, Object player, Object... args) {
        return this.messageManager.getMessage(msgKey).getMessageForPlayer(player, args);
    }

    @Override
    public String getMessage(String msgKey, Object... args) {
        return this.getMessage(msgKey, FAKE_PLAYER_BUKKIT, args);
    }

    @Override
    public void message(UUID playerId, String type, String msgKey, Object... args) {
        Player p = Bukkit.getPlayer(playerId);
        if (p != null) {
            this.message(p, type, msgKey, args);
        }
    }

    @Override
    public void message(Object receiver, String type, String msgKey, Object... args) {
        PlatformUtils.checkCommandSender(receiver);
        Player player = receiver instanceof Player ? (Player) receiver : FAKE_PLAYER_BUKKIT;
        String[] msgs = this.messageManager.getMessage(msgKey).getMessagesForPlayer(player, args);
        // 将信息放入类型模板
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = this.messageManager.getMessage("message.type." + type).getMessageForPlayer(player, msgs[i]);
        }
        ((CommandSender) receiver).sendMessage(msgs);
    }

    @Override
    public void messageConsole(String type, String msgKey, Object... args) {
        String[] msgs = this.messageManager.getMessage(msgKey).getMessages(args);
        // 将信息放入类型模板
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = this.messageManager.getMessage("message.type." + type).getMessage(msgs[i]);
        }
        Bukkit.getConsoleSender().sendMessage(msgs);
    }

    @Override
    public void info(Object receiver, String msgKey, Object... args) {
        this.message(receiver, "info", msgKey, args);
    }

    @Override
    public void warning(Object receiver, String msgKey, Object... args) {
        this.message(receiver, "warning", msgKey, args);
    }

    @Override
    public void error(Object receiver, String msgKey, Object... args) {
        this.message(receiver, "error", msgKey, args);
    }

    @Override
    public void debug(int debugLevel, Object receiver, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) {
            this.message(receiver, "debug", msgKey, args);
        }
    }

}
