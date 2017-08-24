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

import java.lang.reflect.Proxy;

/**
 * //TODO
 */
public class DefaultMessageSender implements MessageSender {

    public static final Player FAKE_PLAYER_BUKKIT = (Player) Proxy.newProxyInstance(
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

    public static final ProxiedPlayer FAKE_PLAYER_BUNGEE = (ProxiedPlayer) Proxy.newProxyInstance(
            DefaultMessageFormat.class.getClassLoader(),
            new Class<?>[]{Player.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "sendMessage":
                        if (args[0] instanceof String) {
                            ProxyServer.getInstance().getConsole().sendMessage((String) args[0]);
                        } else if (args[0] instanceof String[]) {
                            for (String msg : (String[])args[0]) {
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

    private final MessageManager messageManager;

    private int debugLevel;

    public DefaultMessageSender(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Override
    public String getMessage(String msgKey, Object... args) {
        return this.messageManager.getMessage(msgKey).getMessage(FAKE_PLAYER_BUKKIT, args);
    }

    @Override
    public MessageSender message(CommandSender receiver, String type, String msgKey, Object... args) {
        Player player = receiver instanceof Player ? (Player) receiver : FAKE_PLAYER_BUKKIT;
        String[] msgs = this.messageManager.getMessage(msgKey).getMessages(player, args);
        // 将信息放入类型模板
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = this.messageManager.getMessage("message.type." + type).getMessage(player, msgs[i]);
        }

        receiver.sendMessage(msgs);
        return this;
    }

    @Override
    public MessageSender message(net.md_5.bungee.api.CommandSender receiver, String type, String msgKey, Object... args) {
        ProxiedPlayer player = receiver instanceof ProxiedPlayer ? (ProxiedPlayer)receiver : FAKE_PLAYER_BUNGEE;
        String[] msgs = this.messageManager.getMessage(msgKey).getMessages(player, args);
        for (int i = 0; i < msgs.length; i++) {
            // 将信息放入类型模板
            String msg = this.messageManager.getMessage("message.type." + type).getMessage(player, msgs[i]);
            receiver.sendMessage(TextComponent.fromLegacyText(msg));
        }
        return this;
    }

    @Override
    public MessageSender messageConsole(String type, String msgKey, Object... args) {
        this.message(FAKE_PLAYER_BUKKIT, type, msgKey, args);
        return this;
    }

    @Override
    public MessageSender info(CommandSender receiver, String msgKey, Object... args) {
        this.message(receiver, "info", msgKey, args);
        return this;
    }

    @Override
    public MessageSender info(net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args) {
        return null;
    }

    @Override
    public MessageSender infoConsole(String msgKey, Object... args) {
        this.messageConsole("info", msgKey, args);
        return this;
    }

    @Override
    public MessageSender warning(CommandSender receiver, String msgKey, Object... args) {
        this.message(receiver, "warning", msgKey, args);
        return this;
    }

    @Override
    public MessageSender warning(net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args) {
        return null;
    }

    @Override
    public MessageSender warningConsole(String msgKey, Object... args) {
        this.messageConsole("warning", msgKey, args);
        return this;
    }

    @Override
    public MessageSender error(CommandSender receiver, String msgKey, Object... args) {
        this.message(receiver, "error", msgKey, args);
        return this;
    }

    @Override
    public MessageSender error(net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args) {
        return null;
    }

    @Override
    public MessageSender errorConsole(String msgKey, Object... args) {
        this.messageConsole("error", msgKey, args);
        return this;
    }

    @Override
    public MessageSender debug(int debugLevel, CommandSender receiver, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) this.message(receiver, "debug", msgKey, args);
        return this;
    }

    @Override
    public MessageSender debug(int debugLevel, net.md_5.bungee.api.CommandSender receiver, String msgKey, Object... args) {
        return null;
    }

    @Override
    public MessageSender debugConsole(int debugLevel, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) this.messageConsole("debug", msgKey, args);
        return this;
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
