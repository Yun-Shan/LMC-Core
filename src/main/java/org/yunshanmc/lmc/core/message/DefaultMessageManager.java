/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * //TODO
 */
public class DefaultMessageManager implements MessageManager {
    
    protected final ResourceManager resourceManager;
    
    private Map<String, Message> messageCache = new HashMap<>();
    
    public DefaultMessageManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
    
    
    @Override
    public MessageSender createMessageSender() {
        return null;
    }
    
    @Override
    public Message getMessage(String key) {
        Message message = this.messageCache.get(key);
        if (message == null) {
            message = this.getMessageFromResource(key);
            this.messageCache.put(key, message);
        }
        return message;
    }
    
    protected Message getMessageFromResource(String key) {
        return null;
    }
}
