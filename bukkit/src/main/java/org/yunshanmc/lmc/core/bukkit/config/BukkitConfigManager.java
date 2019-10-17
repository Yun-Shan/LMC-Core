package org.yunshanmc.lmc.core.bukkit.config;

import org.yunshanmc.lmc.core.config.AbstractConfigManager;
import org.yunshanmc.lmc.core.config.LMCConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.IOException;

public class BukkitConfigManager extends AbstractConfigManager {

    public BukkitConfigManager(ResourceManager resourceManager) {
        super(resourceManager);
    }

    @Override
    public LMCConfiguration readConfig(Resource resource) {
        try {
            return new BukkitLMCConfiguration(resource);
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }
}
