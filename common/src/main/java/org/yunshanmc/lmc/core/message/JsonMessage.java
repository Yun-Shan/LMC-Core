package org.yunshanmc.lmc.core.message;

// TODO 实现Json信息
// TODO 转换发送方式，自动识别Json类型的信息
public abstract class JsonMessage extends BaseMessage {
    public JsonMessage(String msg, MessageContext context) {
        super(msg, context);
    }

    public JsonMessage(String msg, MessageContext context, MessageFormat format) {
        super(msg, context, format);
    }
}
