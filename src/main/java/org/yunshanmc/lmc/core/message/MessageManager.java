/*
 * Author: Yun-Shan
 * Date: 2017/06/22
 */
package org.yunshanmc.lmc.core.message;

/**
 * 信息管理器
 */
public interface MessageManager {

    /**
     * 获取消息上下文
     *
     * @return 消息上下文
     */
    MessageContext getContext();

    /**
     * 获取消息发送者
     *
     * @return 消息发送者
     */
    MessageSender getMessageSender();

    /**
     * 获取本地化信息的MessageFormat对象
     * <p>
     * 相当于调用{@link #getMessage(String, MessageContext)}的第二个参数传null
     * @param key     本地化信息对应的键
     * @return 本地化信息的MessageFormat对象
     */
    Message getMessage(String key);

    /**
     * 获取本地化信息的MessageFormat对象
     *
     * @param key     本地化信息对应的键
     * @param context 用于信息格式化的上下文对象，为null时使用默认的上下文
     * @return 本地化信息的MessageFormat对象
     */
    Message getMessage(String key, MessageContext context);

    void setDebugLevel(int debugLevel);

    int getDebugLevel();
}
