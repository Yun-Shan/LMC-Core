package org.yunshanmc.lmc.core.config;

import org.bukkit.configuration.file.YamlConfiguration;
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
        return null;
    }

    @Override
    public YamlConfiguration getConfig(String path) {
        try {
            return YamlConfiguration.loadConfiguration(new InputStreamReader(this.resourceManager.getSelfResource(path).getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public YamlConfiguration readConfig(Resource resource) {
        return null;
    }
}
