package org.yunshanmc.lmc.core.config;

import com.google.common.collect.Maps;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 默认配置管理器
 * // TODO 提取重复代码
 */
public class DefaultConfigManager implements ConfigManager {

    private final ResourceManager resourceManager;

    public DefaultConfigManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public FileConfiguration getDefaultConfig(String path) {
        Resource res = this.resourceManager.getSelfResource(path);
        if (res == null) return null;
        return this.readConfig(res);
    }

    @Override
    public FileConfiguration getUserConfig(String path) {
        Resource res = this.resourceManager.getFolderResource(path);
        if (res == null) return null;
        return this.readConfig(res);
    }

    @Override
    public FileConfiguration getConfig(String path) {
        FileConfiguration cfg = this.getUserConfig(path);
        if (cfg == null) cfg = this.getDefaultConfig(path);
        return cfg;
    }

    @Override
    public Map<String, FileConfiguration> getDefaultConfigs(String path, boolean deep) {
        Map<String, Resource> res = this.resourceManager.getSelfResources(path, name -> name.endsWith(".yml"), deep);
        if (res == null) return null;
        return Maps.transformValues(res, this::readConfig);
    }

    @Override
    public Map<String, FileConfiguration> getUserConfigs(String path, boolean deep) {
        Map<String, Resource> res = this.resourceManager.getFolderResources(path, name -> name.endsWith(".yml"), deep);
        if (res == null) return null;
        return Maps.transformValues(res, this::readConfig);
    }

    @Override
    public Map<String, FileConfiguration> getConfigs(String path, boolean deep) {
        Map<String, FileConfiguration> cfgs = this.getUserConfigs(path, deep);
        if (cfgs == null) cfgs = this.getDefaultConfigs(path, deep);
        return cfgs;
    }

    @Override
    public FileConfiguration getPluginConfig() {
        FileConfiguration cfg = getConfig("config.yml");
        if (cfg == null) cfg = new YamlConfiguration();
        return cfg;
    }

    @Override
    public FileConfiguration readConfig(Resource resource) {
        try {
            return YamlConfiguration.loadConfiguration(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }
}
