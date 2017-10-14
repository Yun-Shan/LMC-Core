/*
 * Author: Yun-Shan
 * Date: 2017/06/22
 */
package org.yunshanmc.lmc.core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.yunshanmc.lmc.core.resource.Resource;

import java.util.List;
import java.util.Map;

/**
 * //TODO 注释.
 */
public interface ConfigManager {

    FileConfiguration getConfig(String path);

    FileConfiguration getDefaultConfig(String path);

    FileConfiguration getUserConfig(String path);

    Map<String, FileConfiguration> getConfigs(String path, boolean deep);

    Map<String, FileConfiguration> getDefaultConfigs(String path, boolean deep);

    Map<String, FileConfiguration> getUserConfigs(String path, boolean deep);

    FileConfiguration getPluginConfig();

    FileConfiguration readConfig(Resource resource);
}
