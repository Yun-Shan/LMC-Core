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
     * 创建消息发送者
     *
     * @return 新建的消息发送者
     */
    MessageSender createMessageSender();
    
    /**
     * 获取本地化信息的MessageFormat对象
     *
     * @param key
     *            本地化信息对应的键
     * @return 本地化信息的MessageFormat对象
     */
    Message getMessage(String key);

    void setDebugLevel(int debugLevel);

    int getDebugLevel();
}
