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
                                                                                                 return "Console";
                                                                                             default:
                                                                                                 return null;
                                                                                         }
                                                                                     });
    
    private MessageManager messageManager;
    
    public DefaultMessageSender(MessageManager messageManager) {
        this.messageManager = messageManager;
    }
    
    @Override
    public void message(Player receiver, String type, String msgKey, Object... args) {
        type = this.messageManager.getMessage(msgKey).getMessage(receiver, args);// TODO type
        receiver.sendMessage(this.messageManager.getMessage(msgKey).getMessage(receiver, args).replace("", type));
    }
    
    @Override
    public void messageConsole(String type, String msgKey, Object... args) {
        this.message(CONSOLE_FAKE_PLAYER, type, msgKey, args);
    }
    
}
