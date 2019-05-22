package org.yunshanmc.lmc.core.message;

import org.yaml.snakeyaml.Yaml;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 分组信息管理器.
 * <p>
 *
 * @author Yun-Shan
 */
public abstract class BaseGroupMessageManager extends BaseMessageManager {

    private static final String MESSAGE_DIR = "messages";
    private static final String YML_EXT = ".yml";
    private static final String PATH_REGEX = "(/)|(\\\\)";
    private static final char CFG_PATH_SEPARATOR = '.';

    private MessageGroup defMsgGroup;
    private String userMsgPath;

    public BaseGroupMessageManager(LMCPlugin plugin, ResourceManager resourceManager) {
        this(plugin, resourceManager, MESSAGE_DIR);
    }

    public BaseGroupMessageManager(LMCPlugin plugin, ResourceManager resourceManager, String defMsgPath) {
        super(plugin, resourceManager);
        this.userMsgPath = defMsgPath;
        // init default message name
        Map<String, Resource> cfgs = resourceManager.getSelfResources(defMsgPath, name -> name.endsWith("" + ".yml"), true);
        MessageGroup msgGroup = new MessageGroup("", new HashMap<>(), new HashMap<>());
        // "xxx" + "/"
        int startIdx = defMsgPath.length() + 1;
        cfgs.forEach((path, res) -> {
            MessageGroup group = msgGroup;
            MessageGroup sub;
            if (path.endsWith(YML_EXT)) {
                path = path.substring(startIdx, path.length() - 4);
            }

            for (String key : path.split(PATH_REGEX)) {
                sub = group.subGroups.get(key);
                if (sub == null) {
                    sub = new MessageGroup(key, new HashMap<>(), new HashMap<>());
                    group.subGroups.put(key, sub);
                    group = sub;
                }
            }
            try {
                @SuppressWarnings("unchecked")
                Map<String, ?> dataMap = new Yaml().loadAs(res.getInputStream(), Map.class);
                group.msgMap.putAll(super.resolveMap(dataMap));
            } catch (IOException e) {
                ExceptionHandler.handle(e);
            }
        });
        this.defMsgGroup = msgGroup;
    }

    @Override
    protected Message getMessageFromResource(String key, MessageContext context) {
        // 根目录的messages.yml的优先级高于messages目录下的分组信息
        Message msg = super.getMessageFromResource(key, context);
        if (msg != null) {
            return msg;
        }

        String path = this.userMsgPath + File.separator + key;
        int firstSep;
        Resource cfg;
        do {
            char[] chars = path.toCharArray();

            String cfgPath = path.indexOf('.') > 0 ? new String(chars, 0, path.indexOf('.')) : path;
            cfg = this.resourceManager.getFolderResource(cfgPath + ".yml");
            if (cfg != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, ?> dataMap = new Yaml().loadAs(cfg.getInputStream(), Map.class);
                    Map<String, String> msgMap = super.resolveMap(dataMap);
                    if (msgMap.containsKey(key.substring(path.indexOf('.') - this.userMsgPath.length()))) {
                        return this.newMessage(msgMap.get(key.substring(path.indexOf('.') - this.userMsgPath.length())), context);
                    }
                } catch (IOException e) {
                    ExceptionHandler.handle(e);
                }
            }

            // 用户配置中未找到，尝试在默认配置中查找
            if ((firstSep = path.indexOf(CFG_PATH_SEPARATOR)) == -1) {
                return this.defMsgGroup.getMessage(key, context);
            }

            chars[firstSep] = File.separatorChar;
            path = new String(chars);
        } while (true);
    }

    private class MessageGroup {

        private final String name;
        private final Map<String, String> msgMap;
        private final Map<String, MessageGroup> subGroups;

        public MessageGroup(String name, Map<String, ?> dataMap, Map<String, MessageGroup> subGroups) {
            this.name = name;
            this.msgMap = BaseGroupMessageManager.super.resolveMap(dataMap);
            this.subGroups = subGroups;
        }

        public Message getMessage(String key, MessageContext context) {
            Message msg = null;
            if (this.msgMap != null && this.msgMap.containsKey(key)) {
                msg = BaseGroupMessageManager.this.newMessage(this.msgMap.get(key), context);
            } else {// 搜索子组
                int groupSep = key.indexOf('.');
                if (groupSep > 0) {
                    String group = key.substring(0, groupSep);
                    MessageGroup sub = this.subGroups.get(group);
                    if (sub != null) {
                        msg = sub.getMessage(key.substring(groupSep + 1), context);
                    }
                }
            }
            return msg;
        }

        public String getName() {
            return this.name;
        }
    }
}
