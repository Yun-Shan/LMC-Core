/*
 * Author: Yun-Shan
 * Date: 2017/06/22
 */
package org.yunshanmc.lmc.core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.yunshanmc.lmc.core.resource.Resource;

/**
 * //TODO 注释.
 */
public interface ConfigManager {

    FileConfiguration getConfig(String path);

    FileConfiguration getDefaultConfig(String path);

    FileConfiguration getUserConfig(String path);

    FileConfiguration getPluginConfig();

    FileConfiguration readConfig(Resource resource);
}
