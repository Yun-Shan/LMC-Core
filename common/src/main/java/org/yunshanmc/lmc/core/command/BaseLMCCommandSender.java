package org.yunshanmc.lmc.core.command;

import org.yunshanmc.lmc.core.message.MessageSender;

/**
 * @author Yun-Shan
 */
public abstract class BaseLMCCommandSender {

    private final Object handle;
    protected MessageSender messageSender;

    public BaseLMCCommandSender(Object handle) {
        this.handle = handle;
    }
    public BaseLMCCommandSender(Object handle, MessageSender messageSender) {
        this.handle = handle;
        this.setMessageSender(messageSender);
    }

    @SuppressWarnings("unchecked")
    public <T> T getHandle() {
        return (T) this.handle;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    /**
     * 发送调试信息
     *
     * @param debugLevel 调试等级，当且仅当传入的调试等级小于等于设置的调试等级时才会发生调试信息
     * @param msgKey     信息key
     * @param args       信息文本中的参数列表
     */
    public abstract void debug(int debugLevel, String msgKey, Object... args);

    /**
     * 发送错误信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    public abstract void error(String msgKey, Object... args);

    /**
     * 发送警告信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    public abstract void warning(String msgKey, Object... args);

    /**
     * 发送普通信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    public abstract void info(String msgKey, Object... args);

    /**
     * 发送信息
     *
     * @param type   信息类型
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    public abstract void message(String type, String msgKey, Object... args);
}
