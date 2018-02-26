/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * //TODO
 */
public class DefaultMessageSender implements MessageSender {

    public static final Player FAKE_PLAYER_BUKKIT;
    static {
        if (PlatformUtils.isBukkit()) {
            FAKE_PLAYER_BUKKIT = (Player) Proxy.newProxyInstance(
                    DefaultMessageFormat.class.getClassLoader(),
                    new Class<?>[]{Player.class},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "sendMessage":
                                if (args[0] instanceof String) Bukkit.getConsoleSender().sendMessage(
                                        (String) args[0]);
                                else if (args[0] instanceof String[]) Bukkit.getConsoleSender().sendMessage(
                                        (String[]) args[0]);
                                return null;
                            case "getName":
                            case "getDisplayName":
                            case "getCustomName":
                            case "getPlayerListName":
                                return "[$Console$]";
                            default:
                                throw new UnsupportedOperationException();
                        }
                    });
        } else {
            FAKE_PLAYER_BUKKIT = null;
        }
    }

    public static final ProxiedPlayer FAKE_PLAYER_BUNGEE;
    static {
        if (PlatformUtils.isBungeeCord()) {
            FAKE_PLAYER_BUNGEE = (ProxiedPlayer) Proxy.newProxyInstance(
                    DefaultMessageFormat.class.getClassLoader(),
                    new Class<?>[]{ProxiedPlayer.class},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "sendMessage":
                                if (args[0] instanceof String) {
                                    ProxyServer.getInstance().getConsole().sendMessage((String) args[0]);
                                } else if (args[0] instanceof String[]) {
                                    for (String msg : (String[]) args[0]) {
                                        ProxyServer.getInstance().getConsole().sendMessage(msg);
                                    }
                                } else if (args[0] instanceof BaseComponent) {
                                    ProxyServer.getInstance().getConsole().sendMessage((BaseComponent) args[0]);
                                } else if (args[0] instanceof BaseComponent[]) {
                                    ProxyServer.getInstance().getConsole().sendMessage((BaseComponent[]) args[0]);
                                } else if (args[0] instanceof ChatMessageType) {
                                    if (args[0] != ChatMessageType.CHAT) return null;
                                    if (args[1] instanceof BaseComponent) {
                                        ProxyServer.getInstance().getConsole().sendMessage((BaseComponent) args[1]);
                                    } else if (args[1] instanceof BaseComponent[]) {
                                        ProxyServer.getInstance().getConsole().sendMessage((BaseComponent[]) args[1]);
                                    }
                                }
                                return null;
                            case "getName":
                            case "getDisplayName":
                                return "[$Console$]";
                            default:
                                throw new UnsupportedOperationException();
                        }
                    });
        } else {
            FAKE_PLAYER_BUNGEE = null;
        }
    }

    private final MessageManager messageManager;

    private int debugLevel;

    public DefaultMessageSender(MessageManager messageManager) {
        this.messageManager = messageManager;
        this.debugLevel = messageManager.getDebugLevel();
    }

    @Override
    public String getMessage(String msgKey, Player player, Object... args) {
        return this.messageManager.getMessage(msgKey).getMessage(player, args);
    }

    @Override
    public String getMessage(String msgKey, ProxiedPlayer player, Object... args) {
        return this.messageManager.getMessage(msgKey).getMessage(player, args);
    }

    @Override
    public String getMessage(String msgKey, UUID playerId, Object... args) {
        return this.messageManager.getMessage(msgKey).getMessage(playerId, args);
    }

    @Override
    public String getMessage(String msgKey, Object... args) {
        if (PlatformUtils.isBukkit()) {
            return this.getMessage(msgKey, FAKE_PLAYER_BUKKIT, args);
        } else if (PlatformUtils.isBungeeCord()) {
            return this.getMessage(msgKey, FAKE_PLAYER_BUNGEE, args);
        } else if (PlatformUtils.isTest()) {
            return this.messageManager.getMessage(msgKey).getMessage(args);
        } else {
            throw new UnsupportedOperationException("Unsupported Platform");
        }
    }

    @Override
    public void message(CommandSender receiver, String type, String msgKey, Object... args) {
        Player player = receiver instanceof Player ? (Player) receiver : FAKE_PLAYER_BUKKIT;
        String[] msgs = this.messageManager.getMessage(msgKey).getMessages(player, args);
        // 将信息放入类型模板
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = this.messageManager.getMessage("message.type." + type).getMessage(player, msgs[i]);
        }
        receiver.sendMessage(msgs);
    }

    @Override
    public void message(net.md_5.bungee.api.CommandSender receiver, String type, String msgKey, Object... args) {
        ProxiedPlayer player = receiver instanceof ProxiedPlayer ? (ProxiedPlayer) receiver : FAKE_PLAYER_BUNGEE;
        String[] msgs = this.messageManager.getMessage(msgKey).getMessages(player, args);
        for (String msg : msgs) {
            // 将信息放入类型模板
            msg = this.messageManager.getMessage("message.type." + type).getMessage(player, msg);
            receiver.sendMessage(TextComponent.fromLegacyText(msg));
        }
    }

    @Override
    public void message(UUID playerId, String type, String msgKey, Object... args) {
        if (PlatformUtils.isBukkit()) {
            Player p = Bukkit.getPlayer(playerId);
            if (p != null) {
                this.message(p, type, msgKey, args);
            }
        } else if (PlatformUtils.isBungeeCord()) {
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerId);
            if (p != null) {
                this.message(p, type, msgKey, args);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported Platform");
        }
    }

    @Override
    public void messageConsole(String type, String msgKey, Object... args) {
        if (PlatformUtils.isBukkit()) {
            this.message(Bukkit.getConsoleSender(), type, msgKey, args);
        } else if (PlatformUtils.isBungeeCord()) {
            this.message(ProxyServer.getInstance().getConsole(), type, msgKey, args);
        } else if (PlatformUtils.isTest()) {
            String[] msgs = this.messageManager.getMessage(msgKey).getMessages(args);
            for (String msg : msgs) {
                // 将信息放入类型模板
                msg = this.messageManager.getMessage("message.type." + type).getMessage(msg);
                System.out.println(msg);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported Platform");
        }
    }

    @Override
    public void info(CommandSender receiver, String msgKey, Object... args) {
        this.message(receiver, "info", msgKey, args);
    }

    @Override
    public void info(net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args) {
        this.message(receiver, "info", msgKey, args);
    }

    @Override
    public void info(UUID playerId, String msgKey, Object... args) {
        this.message(playerId, "info", msgKey, args);
    }

    @Override
    public void infoConsole(String msgKey, Object... args) {
        this.messageConsole("info", msgKey, args);
    }

    @Override
    public void warning(CommandSender receiver, String msgKey, Object... args) {
        this.message(receiver, "warning", msgKey, args);
    }

    @Override
    public void warning(net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args) {
        this.message(receiver, "warning", msgKey, args);
    }

    @Override
    public void warning(UUID playerId, String msgKey, Object... args) {
        this.message(playerId, "warning", msgKey, args);
    }

    @Override
    public void warningConsole(String msgKey, Object... args) {
        this.messageConsole("warning", msgKey, args);
    }

    @Override
    public void error(CommandSender receiver, String msgKey, Object... args) {
        this.message(receiver, "error", msgKey, args);
    }

    @Override
    public void error(net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args) {
        this.message(receiver, "error", msgKey, args);
    }

    @Override
    public void error(UUID playerId, String msgKey, Object... args) {
        this.message(playerId, "error", msgKey, args);
    }

    @Override
    public void errorConsole(String msgKey, Object... args) {
        this.messageConsole("error", msgKey, args);
    }

    @Override
    public void debug(int debugLevel, CommandSender receiver, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) this.message(receiver, "debug", msgKey, args);
    }

    @Override
    public void debug(int debugLevel, net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) this.message(receiver, "debug", msgKey, args);
    }

    @Override
    public void debug(int debugLevel, UUID playerId, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) this.message(playerId, "debug", msgKey, args);
    }

    @Override
    public void debugConsole(int debugLevel, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) this.messageConsole("debug", msgKey, args);
    }

    @Override
    public MessageSender setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
        return this;
    }

    @Override
    public int getDebugLevel() {
        return this.debugLevel;
    }
}
