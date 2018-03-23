package org.yunshanmc.lmc.core.message;

import java.util.UUID;

public class MockMessageSender extends BaseMessageSender {

    public MockMessageSender(MessageManager messageManager) {
        super(messageManager);
    }

    @Override
    public String getMessage(String msgKey, Object player, Object... args) {
        return messageManager.getMessage(msgKey).getMessage(player, args);
    }

    @Override
    public String getMessage(String msgKey, Object... args) {
        return messageManager.getMessage(msgKey).getMessage(null, args);
    }

    @Override
    public void message(Object receiver, String type, String msgKey, Object... args) {
        System.out.println(msgKey);
    }

    @Override
    public void message(UUID playerId, String type, String msgKey, Object... args) {
        System.out.println(msgKey);
    }

    @Override
    public void messageConsole(String type, String msgKey, Object... args) {
        System.out.println(msgKey);
    }

    @Override
    public void info(Object receiver, String msgKey, Object... args) {
        message(receiver, "info", msgKey, args);
    }

    @Override
    public void warning(Object receiver, String msgKey, Object... args) {
        message(receiver, "warning", msgKey, args);
    }

    @Override
    public void error(Object receiver, String msgKey, Object... args) {
        message(receiver, "error", msgKey, args);
    }

    @Override
    public void debug(int debugLevel, Object receiver, String msgKey, Object... args) {
        message(receiver, "debug", msgKey, args);
    }
}
