package org.yunshanmc.lmc.core.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DefaultConfigManager implements ConfigManager {

    private final ResourceManager resourceManager;

    public DefaultConfigManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public YamlConfiguration getDefaultConfig(String path) {
        return this.readConfig(this.resourceManager.getSelfResource(path));
    }

    @Override
    public YamlConfiguration getUserConfig(String path) {
        return this.readConfig(this.resourceManager.getFolderResource(path));
    }

    @Override
    public YamlConfiguration getConfig(String path) {
        YamlConfiguration cfg = this.getUserConfig(path);
        if (cfg == null) cfg = this.getDefaultConfig(path);
        return cfg;
    }

    @Override
    public YamlConfiguration readConfig(Resource resource) {
        try {
            return YamlConfiguration.loadConfiguration(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }
}
