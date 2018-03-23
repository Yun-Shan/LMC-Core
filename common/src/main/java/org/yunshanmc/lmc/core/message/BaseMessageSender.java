/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import java.util.UUID;

/**
 * TODO 注释
 *
 * @author Yun-Shan
 */
public abstract class BaseMessageSender implements MessageSender {

    protected final MessageManager messageManager;

    private int debugLevel;

    public BaseMessageSender(MessageManager messageManager) {
        this.messageManager = messageManager;
        this.debugLevel = messageManager.getDebugLevel();
    }

    @Override
    public String getMessage(String msgKey, UUID playerId, Object... args) {
        return this.messageManager.getMessage(msgKey).getMessage(playerId, args);
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
    public void warning(UUID playerId, String msgKey, Object... args) {
        this.message(playerId, "warning", msgKey, args);
    }

    @Override
    public void warningConsole(String msgKey, Object... args) {
        this.messageConsole("warning", msgKey, args);
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
    public void debug(int debugLevel, UUID playerId, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) {
            this.message(playerId, "debug", msgKey, args);
        }
    }

    @Override
    public void debugConsole(int debugLevel, String msgKey, Object... args) {
        if (this.getDebugLevel() >= debugLevel) {
            this.messageConsole("debug", msgKey, args);
        }
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
