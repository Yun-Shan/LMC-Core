package org.yunshanmc.lmc.core.bungee.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.yunshanmc.lmc.core.message.BaseMessageSender;
import org.yunshanmc.lmc.core.message.DefaultMessageFormat;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author Yun-Shan
 */
public class BungeeMessageSender extends BaseMessageSender {

    private static final ProxiedPlayer FAKE_PLAYER_BUNGEE;

    static {
        FAKE_PLAYER_BUNGEE = (ProxiedPlayer) Proxy.newProxyInstance(
            DefaultMessageFormat.class.getClassLoader(),
            new Class<?>[]{ProxiedPlayer.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "sendMessage":
                        if (args[0] instanceof String) {
                            ProxyServer.getInstance().getConsole()
                                .sendMessage(TextComponent.fromLegacyText((String) args[0]));
                        } else if (args[0] instanceof String[]) {
                            for (String msg : (String[]) args[0]) {
                                ProxyServer.getInstance().getConsole()
                                    .sendMessage(TextComponent.fromLegacyText(msg));
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
                        return "[$LMC-Mock$]";
                    default:
                        throw new UnsupportedOperationException();
                }
            });
    }


    public BungeeMessageSender(MessageManager messageManager) {
        super(messageManager);
    }

    @Override
    public String getMessage(String msgKey, Object player, Object... args) {
        return this.messageManager.getMessage(msgKey).getMessage(player, args);
    }

    @Override
    public String getMessage(String msgKey, Object... args) {
        return this.getMessage(msgKey, FAKE_PLAYER_BUNGEE, args);
    }

    @Override
    public void message(UUID playerId, String type, String msgKey, Object... args) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerId);
        if (p != null) {
            this.message(p, type, msgKey, args);
        }
    }

    @Override
    public void message(Object receiver, String type, String msgKey, Object... args) {
        PlatformUtils.checkCommandSender(receiver);
        ProxiedPlayer player = receiver instanceof ProxiedPlayer ? (ProxiedPlayer) receiver : FAKE_PLAYER_BUNGEE;
        String[] msgs = this.messageManager.getMessage(msgKey).getMessages(player, args);
        for (String msg : msgs) {
            // 将信息放入类型模板
            msg = this.messageManager.getMessage("message.type." + type).getMessage(player, msg);
            ((CommandSender) receiver).sendMessage(TextComponent.fromLegacyText(msg));
        }
    }

    @Override
    public void messageConsole(String type, String msgKey, Object... args) {
        this.message(ProxyServer.getInstance().getConsole(), type, msgKey, args);
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
        if (this.getDebugLevel() >= debugLevel) this.message(receiver, "debug", msgKey, args);
    }

}
