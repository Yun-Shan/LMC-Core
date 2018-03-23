package org.yunshanmc.lmc.core.config;

import com.google.common.collect.Maps;
import org.yunshanmc.lmc.core.config.bukkitcfg.InvalidConfigurationException;
import org.yunshanmc.lmc.core.config.bukkitcfg.file.FileConfiguration;
import org.yunshanmc.lmc.core.config.bukkitcfg.file.YamlConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 默认配置管理器
 * TODO 提取重复代码
 * @author Yun-Shan
 */
public class DefaultConfigManager implements ConfigManager {

    private final ResourceManager resourceManager;

    public DefaultConfigManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public FileConfiguration getDefaultConfig(String path) {
        Resource res = this.resourceManager.getSelfResource(path);
        if (res == null) {
            return null;
        }
        return this.readConfig(res);
    }

    @Override
    public FileConfiguration getUserConfig(String path) {
        Resource res = this.resourceManager.getFolderResource(path);
        if (res == null) {
            return null;
        }
        return this.readConfig(res);
    }

    @Override
    public FileConfiguration getConfig(String path) {
        FileConfiguration cfg = this.getUserConfig(path);
        FileConfiguration def = this.getDefaultConfig(path);
        if (cfg == null && def != null) {
            cfg = def;
        } else if (cfg != null && def != null) {
            cfg.addDefaults(def);
        }
        return cfg;
    }

    @Override
    public Map<String, FileConfiguration> getDefaultConfigs(String path, boolean deep) {
        Map<String, Resource> res = this.resourceManager.getSelfResources(path, name -> name.endsWith(".yml"), deep);
        if (res == null) {
            return null;
        }
        return Maps.transformValues(res, this::readConfig);
    }

    @Override
    public Map<String, FileConfiguration> getUserConfigs(String path, boolean deep) {
        Map<String, Resource> res = this.resourceManager.getFolderResources(path, name -> name.endsWith(".yml"), deep);
        if (res == null) {
            return null;
        }
        return Maps.transformValues(res, this::readConfig);
    }

    @Override
    public Map<String, FileConfiguration> getConfigs(String path, boolean deep) {
        Map<String, FileConfiguration> cfgs = this.getUserConfigs(path, deep);
        if (cfgs != null) {
            cfgs.forEach((k, v) -> {
                FileConfiguration def = this.getDefaultConfig(k);
                if (def != null) {
                    v.addDefaults(def);
                }
            });
        } else {
            cfgs = this.getDefaultConfigs(path, deep);
        }
        return cfgs;
    }

    @Override
    public FileConfiguration getPluginConfig() {
        FileConfiguration cfg = getConfig("config.yml");
        if (cfg == null) {
            cfg = new YamlConfiguration();
        }
        return cfg;
    }

    @Override
    public FileConfiguration readConfig(Resource resource) {
        YamlConfiguration cfg = new YamlConfiguration();
        try {
            cfg.load(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException | InvalidConfigurationException e) {
            ExceptionHandler.handle(e);
        }
        return cfg;
    }
}
