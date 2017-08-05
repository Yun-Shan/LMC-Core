/*
 * Author: Yun-Shan
 * Date: 2017/06/22
 */
package org.yunshanmc.lmc.core.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.yunshanmc.lmc.core.resource.Resource;

/**
 * //TODO 注释
 *
 */
public interface ConfigManager {

    YamlConfiguration getConfig(String path);

    YamlConfiguration getDefaultConfig(String path);

    YamlConfiguration getUserConfig(String path);

    YamlConfiguration readConfig(Resource resource);
}
