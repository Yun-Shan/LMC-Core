package org.yunshanmc.lmc.core.message;

import org.yunshanmc.lmc.core.LMCPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * 信息上下文.
 * <p>
 *
 * @author Yun-Shan
 */
public class MessageContext {

    private final LMCPlugin plugin;
    private final MessageManager messageManager;
    private final Map<String, Object> valMap = new HashMap<>();

    public MessageContext(LMCPlugin plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.put("PluginName", plugin.getName());
    }

    public void put(String name, Object value) {
        this.valMap.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) this.valMap.get(name);
    }
    public String getString(String name) {
        return String.valueOf(this.valMap.get(name));
    }

    public LMCPlugin getPlugin() {
        return this.plugin;
    }

    public MessageManager getMessageManager() {
        return this.messageManager;
    }
}
