/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Proxy;

/**
 * //TODO
 */
public class DefaultMessageSender implements MessageSender {
    
    public static final Player CONSOLE_FAKE_PLAYER = (Player) Proxy.newProxyInstance(DefaultMessageFormat.class.getClassLoader(),
                                                                                     new Class<?>[]{ Player.class },
                                                                                     (proxy, method, args) -> {
                                                                                         switch (method.getName()) {
                                                                                             case "sendMessage":
                                                                                                 Bukkit.getConsoleSender().sendMessage(
                                                                                                         (String) args[0]);
                                                                                                 return null;
                                                                                             case "getName":
                                                                                             case "getDisplayName":
                                                                                             case "getCustomName":
                                                                                             case "getPlayerListName":
                                                                                                 return "[$Console]";
                                                                                             default:
                                                                                                 return null;
                                                                                         }
                                                                                     });
    
    private MessageManager messageManager;

    private int debugLevel;

    public DefaultMessageSender(MessageManager messageManager) {
        this.messageManager = messageManager;
    }
    
    @Override
    public MessageSender message(Player receiver, String type, String msgKey, Object... args) {
        String msg = this.messageManager.getMessage(msgKey).getMessage(receiver, args);
        msg = this.messageManager.getMessage("message.type." + type).getMessage(receiver, msg);// 将信息放入类型模板

        receiver.sendMessage(msg);
        return this;
    }
    
    @Override
    public MessageSender messageConsole(String type, String msgKey, Object... args) {
        this.message(CONSOLE_FAKE_PLAYER, type, msgKey, args);
        return this;
    }

    @Override
    public MessageSender info(Player receiver, String msgKey, Object... args) {
        this.message(receiver, "info", msgKey, args);
        return this;
    }

    @Override
    public MessageSender infoConsole(String msgKey, Object... args) {
        this.messageConsole("info", msgKey, args);
        return this;
    }

    @Override
    public MessageSender warning(Player receiver, String msgKey, Object... args) {
        this.message(receiver, "warning", msgKey, args);
        return this;
    }

    @Override
    public MessageSender warningConsole(String msgKey, Object... args) {
        this.messageConsole("warning", msgKey, args);
        return this;
    }

    @Override
    public MessageSender error(Player receiver, String msgKey, Object... args) {
        this.message(receiver, "error", msgKey, args);
        return this;
    }

    @Override
    public MessageSender errorConsole(String msgKey, Object... args) {
        this.messageConsole("error", msgKey, args);
        return this;
    }

    @Override
    public MessageSender debug(int debugLevel, Player receiver, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) this.message(receiver, "debug", msgKey, args);
        return this;
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
