/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import org.yaml.snakeyaml.Yaml;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.internal.LMCCoreUtils;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.IOException;
import java.util.*;
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

    /**
     * 储存已经读取过的文件
     */
    protected Map<String, Map<String, ?>> pathCache = new HashMap<>();
    /**
     * 储存已经解析完的信息
     */
    private Map<String, Message> messageCache = new HashMap<>();
    private Set<String> invalidKeyCache = new HashSet<>();
    private Map<String, ?> defaultMsg;
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
                this.defaultMsg = this.resolveResource(defMsgRes);
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
    public Message getMessage(final String key, MessageContext context) {
        if (context == null) {
            context = this.context;
        }
        Message message = this.messageCache.get(key);
        if (message == null) {
            if (this.invalidKeyCache.contains(key)) {
                return Message.getMissingMessage(key);
            }
            message = this.getMessageFromResource(key, context, true);
            if (message != null && !message.isMissingMessage()) {
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
                    // 这时message要么为null，是可以合并到下方的message == null判断的
                    // 但是也可能是MissingMessage且有__internal前缀，见上方：message = this.getMessage(INTERNAL_START_PATH + key, context)
                    // 这时就需要重新创建一个没有__internal前缀的
                    message = Message.getMissingMessage(key);
                }
            }
        }
        if (message == null) {
            message = Message.getMissingMessage(key);
        }
        if (message.isMissingMessage()) {
            this.invalidKeyCache.add(key);
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

    protected Message getMessageFromResource(String key, MessageContext context, boolean useDef) {
        Object msgObj = null;
        // 尝试从用户配置获取
        Map<String, ?> msgMap = this.pathCache.get(MESSAGE_PATH);
        if (msgMap == null) {
            try {
                Resource res = this.resourceManager.getFolderResource(MESSAGE_PATH);
                if (res != null) {
                    msgMap = this.resolveResource(res);
                    this.pathCache.put(MESSAGE_PATH, msgMap);
                }
            } catch (IOException e) {
                ExceptionHandler.handle(e);
            }
        }
        if (msgMap != null) {
            msgObj = msgMap.get(key);
        }

        // 用户配置文件不存在或配置项不存在，且需要使用默认值，尝试从默认配置获取
        if (msgObj == null && useDef) {
            msgObj = this.defaultMsg.get(key);
        }
        // msg != null时，存在用户配置或默认配置，会返回对应Message对象
        // msg == null时，用户配置和默认配置都不存在，会返回null
        return this.resolveMessage(msgObj);
    }

    /**
     * 构建新信息
     *
     * @param msg     原始信息字符串
     * @param context 信息上下文
     * @return 构建出的新信息
     */
    protected abstract Message newMessage(String msg, MessageContext context);

    @SuppressWarnings("unchecked")
    protected final Map<String, ?> resolveResource(Resource res) throws IOException {
        Map<String, ?> dataMap = new Yaml().loadAs(res.getInputStream(), Map.class);
        ;
        Map<String, Object> msg = new HashMap<>(dataMap != null ? dataMap.size() : 8);
        AtomicReference<BiConsumer<String, Object>> resolver = new AtomicReference<>();
        resolver.set((k, data) -> {
            if (data instanceof Map) {
                ((Map<String, ?>) data)
                    .forEach((nextK, nextData) ->
                        resolver.get().accept((k.isEmpty() ? "" : k + '.') + nextK, nextData)
                    );
            } else if (data instanceof List) {
                msg.put(k, data);
            } else {
                msg.put(k, String.valueOf(data));
            }
        });
        resolver.get().accept("", dataMap);
        return msg.isEmpty() ? null : msg;
    }

    protected Message resolveJsonMessage(List<?> data) {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected Message resolveMessage(Object msgObj) {
        if (msgObj != null) {
            if (msgObj instanceof List) {
                return resolveJsonMessage((List<?>) msgObj);
            } else if (msgObj instanceof Message) {
                return (Message) msgObj;
            } else {
                return newMessage(String.valueOf(msgObj), context);
            }
        } else {
            return null;
        }
    }
}
