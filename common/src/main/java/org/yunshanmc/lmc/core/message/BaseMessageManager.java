/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import org.yaml.snakeyaml.Yaml;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.internal.LMCCoreUtils;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * 默认信息管理器.
 * <p>
 *
 * @author Yun-Shan
 */
public abstract class BaseMessageManager implements MessageManager {

    protected final LMCPlugin plugin;
    protected final ResourceManager resourceManager;

    protected MessageContext context;

    private Map<String, Message> messageCache = new HashMap<>();
    private Map<String, String> defaultMsg;
    private int debugLevel;

    private static final String MESSAGE_PATH = "messages.yml";
    private static final String INTERNAL_START_PATH = "__internal.";

    public BaseMessageManager(LMCPlugin plugin, ResourceManager resourceManager) {
        this(plugin, resourceManager, MESSAGE_PATH);
    }

    public BaseMessageManager(LMCPlugin plugin, ResourceManager resourceManager, String defMsgPath) {
        this.plugin = plugin;
        this.resourceManager = resourceManager;
        Resource defMsgRes = resourceManager.getSelfResource(defMsgPath);
        if (defMsgRes != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, ?> defaultMsg = new Yaml().loadAs(defMsgRes.getInputStream(), Map.class);
                this.defaultMsg = resolveMap(defaultMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.defaultMsg = new HashMap<>();
        }

        this.context = new MessageContext(plugin, this);
    }

    @Override
    public MessageContext getContext() {
        return this.context;
    }

    @Override
    public Message getMessage(String key) {
        return this.getMessage(key, null);
    }

    @Override
    public Message getMessage(String key, MessageContext context) {
        if (context == null) {
            context = this.context;
        }
        Message message = this.messageCache.get(key);
        if (message == null) {
            message = this.getMessageFromResource(key, context);
            if (message != null) {
                this.messageCache.put(key, message);
            }
        }
        if (message == null && !key.startsWith(INTERNAL_START_PATH)) {
            LMCPlugin lmcCore = LMCCoreUtils.getLMCCorePlugin();
            MessageManager coreMsgManager = lmcCore.getMessageManager();

            if (lmcCore == context.getPlugin() || this != coreMsgManager) {
                // 如果是在自己在自己的messageManager里搜索，则尝试搜索__internal
                // 自己的插件在别人的messageManager 或 别的插件在自己的messageManager 里搜索时不尝试__internal
                message = this.getMessage(INTERNAL_START_PATH + key, context);
            }

            if (message == null || message.isMissingMessage()) {
                if (this != coreMsgManager) {
                    // 自己的__internal未找到且当前不是LMC-Core插件，则尝试LMC-Core提供的公共message
                    message = coreMsgManager.getMessage(key, context);
                } else {
                    // __internal的MissingMessage会换成没有__internal.前缀的key的MissingMessage
                    message = Message.getMissingMessage(key);
                }
            }
        }
        if (message == null) {
            return Message.getMissingMessage(key);
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

    protected Message getMessageFromResource(String key, MessageContext context) {
        Resource res = this.resourceManager.getFolderResource(MESSAGE_PATH);
        String msg = null;
        try {
            // 尝试从用户配置获取
            if (res != null) {
                @SuppressWarnings("unchecked")
                Map<String, ?> dataMap = new Yaml().loadAs(res.getInputStream(), Map.class);
                Map<String, String> msgMap = resolveMap(dataMap);
                msg = msgMap.get(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 用户配置文件不存在或配置项不存在，尝试从默认配置获取
        if (msg == null && this.defaultMsg.containsKey(key)) {
            msg = this.defaultMsg.get(key);
        }
        // 存在用户配置或默认配置
        if (msg != null) {
            return this.newMessage(msg, context);
        }
        // 用户配置和默认配置都不存在
        return null;
    }

    /**
     * 构建新信息
     *
     * @param msg 原始信息字符串
     * @param context 信息上下文
     * @return 构建出的新信息
     */
    protected abstract Message newMessage(String msg, MessageContext context);

    @SuppressWarnings("unchecked")
    protected final Map<String, String> resolveMap(Map<String, ?> map) {
        Map<String, String> msg = new HashMap<>(map.size());
        AtomicReference<BiConsumer<String, Object>> resolver = new AtomicReference<>();
        resolver.set((k, data) -> {
            if (data instanceof String) {
                msg.put(k, (String) data);
            } else if (data instanceof Map) {
                ((Map<String, ?>) data)
                    .forEach((nextK, nextData) ->
                        resolver.get().accept((k.isEmpty() ? "" : k + '.') + nextK, nextData)
                    );
            }
        });
        resolver.get().accept("", map);
        return msg;
    }
}
