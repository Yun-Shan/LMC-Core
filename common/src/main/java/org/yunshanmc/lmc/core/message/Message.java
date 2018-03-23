/*
 * Author: Yun-Shan
 * Date: 2017/07/05
 */
package org.yunshanmc.lmc.core.message;

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

    String getMessage(Object player, Object... args);

    String[] getMessages(Object player, Object... args);

    String getMessage(UUID playerId, Object... args);

    String[] getMessages(UUID playerId, Object... args);

    static Message getMissingMessage(String msg) {
        String missingMsg = "§cMissingLanguage: " + msg;
        String[] missingMsgArray = new String[]{ missingMsg };
        return (Message) Proxy.newProxyInstance(
            Message.class.getClassLoader(),
            new Class[]{Message.class},
            (proxy, method, args) -> {
                if ("translateAlternateColorCodes".equals(method.getName())) return msg;
                else if ("isMissingMessage".equals(method.getName())) return true;
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