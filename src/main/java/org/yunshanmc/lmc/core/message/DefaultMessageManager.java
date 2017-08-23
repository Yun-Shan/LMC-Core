/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yunshanmc.lmc.core.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;

/**
 * //TODO
 */
public class DefaultMessageManager implements MessageManager {
    
    protected final ConfigManager configManager;
    
    private Map<String, Message> messageCache = new HashMap<>();
    private FileConfiguration defaultMsg;
    private int debugLevel;

    private static final String MESSAGE_PATH = "messages.yml";

    public DefaultMessageManager(ConfigManager configManager) {
        this.configManager = configManager;
        this.defaultMsg = configManager.getDefaultConfig(MESSAGE_PATH);
        // 避免空指针
        if (this.defaultMsg == null) this.defaultMsg = new YamlConfiguration();
    }

    @Override
    public MessageSender getMessageSender() {
        return new DefaultMessageSender(this).setDebugLevel(this.getDebugLevel());
    }
    
    @Override
    public Message getMessage(String key) {
        Message message = this.messageCache.get(key);
        if (message == null) {
            message = this.getMessageFromResource(key);
            this.messageCache.put(key, message);
        }
        if (message == null) {
            return new Message.MissingMessage(key);
        }
        return message;
    }

    @Override
    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }

    @Override
    public int getDebugLevel() {
        return this.debugLevel;
    }

    protected Message getMessageFromResource(String key) {
        FileConfiguration cfg = this.configManager.getConfig(MESSAGE_PATH);
        String msg = null;
        // 这段代码感觉特别不优雅，于是写清楚注释
        // 尝试从用户配置获取
        if (cfg != null && cfg.isString(key)) msg = cfg.getString(key);
        // 用户配置文件不存在或配置项不存在，尝试从默认配置获取
        if (msg == null && this.defaultMsg.isString(key)) msg = this.defaultMsg.getString(key);
        // 存在用户配置或默认配置
        if (msg != null) return new Message(msg);
        // 用户配置和默认配置都不存在
        return null;
    }
}
