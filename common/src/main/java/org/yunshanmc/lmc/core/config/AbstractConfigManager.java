package org.yunshanmc.lmc.core.config;

import com.google.common.collect.Maps;
import org.yunshanmc.lmc.core.resource.InputStreamResource;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Objects;

/**
 * 默认配置管理器
 * TODO 提取重复代码
 *
 * @author Yun-Shan
 */
public abstract class AbstractConfigManager implements ConfigManager {

    protected final ResourceManager resourceManager;
    private LMCConfiguration mainConfig;

    public AbstractConfigManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.resourceManager.saveDefaultResource("config.yml", "config.yml", false);
    }

    @Override
    public <T> T getConfig(Class<T> clazz) {
        return ConfigFactory.loadConfig(this, clazz);
    }

    @Override
    public LMCConfiguration getDefaultConfig(String path) {
        Resource res = this.resourceManager.getSelfResource(path);
        if (res == null) {
            return null;
        }
        return this.readConfig(res);
    }

    @Override
    public LMCConfiguration getUserConfig(String path) {
        Resource res = this.resourceManager.getFolderResource(path);
        if (res == null) {
            return null;
        }
        return this.readConfig(res);
    }

    @Override
    public LMCConfiguration getConfig(String path) {
        LMCConfiguration cfg = this.getUserConfig(path);
        LMCConfiguration def = this.getDefaultConfig(path);
        if (cfg == null && def != null) {
            cfg = def;
        } else if (cfg != null && def != null) {
            cfg.addDefaults(def);
        }
        return cfg;
    }

    @Override
    public Map<String, LMCConfiguration> getDefaultConfigs(String path, boolean deep) {
        Map<String, Resource> res = this.resourceManager.getSelfResources(path, name -> name.endsWith(".yml"), deep);
        if (res == null) {
            return null;
        }
        return Maps.transformValues(res, this::readConfig);
    }

    @Override
    public Map<String, LMCConfiguration> getUserConfigs(String path, boolean deep) {
        Map<String, Resource> res = this.resourceManager.getFolderResources(path, name -> name.endsWith(".yml"), deep);
        if (res == null) {
            return null;
        }
        return Maps.transformValues(res, this::readConfig);
    }

    @Override
    public Map<String, LMCConfiguration> getConfigs(String path, boolean deep) {
        Map<String, LMCConfiguration> cfgs = this.getUserConfigs(path, deep);
        if (cfgs != null) {
            cfgs.forEach((k, v) -> {
                LMCConfiguration def = this.getDefaultConfig(k);
                if (def != null) {
                    v.addDefaults(def);
                }
            });
        } else {
            cfgs = this.getDefaultConfigs(path, deep);
        }
        return cfgs;
    }

    @Nonnull
    @Override
    public LMCConfiguration getMainConfig() {
        if (this.mainConfig == null) {
            this.mainConfig = getConfig("config.yml");
            if (this.mainConfig == null) {
                this.resourceManager.writeResource("config.yml", new InputStreamResource(new ByteArrayInputStream(new byte[0])), false);
            }
            this.mainConfig = getUserConfig("config.yml");
            Objects.requireNonNull(this.mainConfig);
        }
        return this.mainConfig;
    }
}
