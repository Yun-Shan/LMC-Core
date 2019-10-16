/*
 * Author: Yun-Shan
 * Date: 2017/07/05
 */
package org.yunshanmc.lmc.core.message;

/**
 * TODO 注释
 *
 * @author Yun-Shan
 */
public abstract class BaseMessage implements Message {

    protected final String msg;
    protected final MessageContext context;
    protected MessageFormat format;

    public BaseMessage(String msg, MessageContext context) {
        this(msg, context, new DefaultMessageFormat(context));
    }

    public BaseMessage(String msg, MessageContext context, MessageFormat format) {
        this.msg = this.translateAlternateColorCodes(msg);
        this.context = context;
        this.format = format;
    }

    /**
     * 转换颜色字符(&amp; -&gt; §).
     * <p>
     *
     * @param msg 原始信息
     * @return 转换颜色字符后的信息
     */
    protected abstract String translateAlternateColorCodes(String msg);

    @Override
    public final boolean isMissingMessage() {
        return false;
    }

    public MessageContext getContext() {
        return this.context;
    }

    @Override
    public String getRawMessage() {
        return this.msg;
    }

    @Override
    public String getMessage(Object... args) {
        return this.format.format(this.msg, args);
    }

    @Override
    public String[] getMessages(Object... args) {
        String[] msgs = this.msg.split("\n");
        for (int i = 0; i < msgs.length; i++) {
            msgs[i] = this.format.format(msgs[i], args);
        }
        return msgs;
    }
}