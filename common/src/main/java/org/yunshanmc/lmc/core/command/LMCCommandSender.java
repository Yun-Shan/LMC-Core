package org.yunshanmc.lmc.core.command;

import org.yunshanmc.lmc.core.message.MessageSender;

/**
 * @author Yun-Shan
 */
public class LMCCommandSender {

    private final Object handle;
    protected MessageSender messageSender;

    public LMCCommandSender(Object handle) {
        this.handle = handle;
    }

    public LMCCommandSender(Object handle, MessageSender messageSender) {
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
    public void debug(int debugLevel, String msgKey, Object... args) {
        this.messageSender.debug(debugLevel, (Object) getHandle(), msgKey, args);
    }

    /**
     * 发送错误信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    public void error(String msgKey, Object... args) {
        this.messageSender.error((Object) getHandle(), msgKey, args);
    }

    /**
     * 发送警告信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    public void warning(String msgKey, Object... args) {
        this.messageSender.warning((Object) getHandle(), msgKey, args);
    }

    /**
     * 发送普通信息
     *
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    public void info(String msgKey, Object... args) {
        this.messageSender.info((Object) getHandle(), msgKey, args);
    }

    /**
     * 发送信息
     *
     * @param type   信息类型
     * @param msgKey 信息key
     * @param args   信息文本中的参数列表
     */
    public void message(String type, String msgKey, Object... args) {
        this.messageSender.error((Object) getHandle(), msgKey, args);
    }
}
