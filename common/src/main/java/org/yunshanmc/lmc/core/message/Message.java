/*
 * Author: Yun-Shan
 * Date: 2017/07/05
 */
package org.yunshanmc.lmc.core.message;

import org.yunshanmc.lmc.core.internal.BuiltinMessage;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * TODO 注释
 *
 * @author Yun-Shan
 */
public interface Message {

    boolean isMissingMessage();

    String getRawMessage();

    String getMessageForPlayer(Object player, Object... args);

    String[] getMessagesForPlayer(Object player, Object... args);

    String getMessageForPlayer(UUID playerId, Object... args);

    String[] getMessagesForPlayer(UUID playerId, Object... args);

    String getMessage(Object... args);

    String[] getMessages(Object... args);

    static Message getMissingMessage(String msg) {
        String missingMsg = BuiltinMessage.getMessage("MissingLanguage", msg);
        String[] missingMsgArray = new String[]{ missingMsg };
        return (Message) Proxy.newProxyInstance(
            Message.class.getClassLoader(),
            new Class[]{Message.class},
            (proxy, method, args) -> {
                if ("translateAlternateColorCodes".equals(method.getName())) {
                    return msg;
                } else if ("isMissingMessage".equals(method.getName())) {
                    return true;
                }
                if (method.getReturnType().equals(String.class)) {
                    return missingMsg;
                } else if (method.getReturnType().equals(String[].class)) {
                    return missingMsgArray;
                } else {
                    return null;
                }
            });
    }
}