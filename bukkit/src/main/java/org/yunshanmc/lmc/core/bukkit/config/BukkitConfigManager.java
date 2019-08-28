package org.yunshanmc.lmc.core.bukkit.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.yunshanmc.lmc.core.config.AbstractConfigManager;
import org.yunshanmc.lmc.core.config.LMCConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BukkitConfigManager extends AbstractConfigManager {

    public BukkitConfigManager(ResourceManager resourceManager) {
        super(resourceManager);
    }

    @Override
    public LMCConfiguration readConfig(Resource resource) {
        YamlConfiguration cfg;
        try {
            cfg = YamlConfiguration.loadConfiguration(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            return new BukkitLMCConfiguration(cfg);
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }
}
