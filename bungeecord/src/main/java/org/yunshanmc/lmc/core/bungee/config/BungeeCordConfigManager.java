package org.yunshanmc.lmc.core.bungee.config;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.yunshanmc.lmc.core.config.AbstractConfigManager;
import org.yunshanmc.lmc.core.config.LMCConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BungeeCordConfigManager extends AbstractConfigManager {

    private static YamlConfiguration YAML;

    public BungeeCordConfigManager(ResourceManager resourceManager) {
        super(resourceManager);
    }

    @Override
    public LMCConfiguration readConfig(Resource resource) {
        Configuration cfg;
        try {
            if (YAML == null) {
                // 由于并发几率极低而且本身就是获取的同一实例(只是为了下面简化代码并且不用再get一次)，所以不使用同步锁
                YAML = (YamlConfiguration) ConfigurationProvider.getProvider(YamlConfiguration.class);
            }
            cfg = YAML.load(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            return new BungeeCordLMCConfiguration(cfg);
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }

    @Nonnull
    @Override
    protected LMCConfiguration createEmptyConfig() {
        return new BungeeCordLMCConfiguration(new Configuration());
    }
}
