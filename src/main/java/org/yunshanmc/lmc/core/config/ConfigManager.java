/*
 * Author: Yun-Shan
 * Date: 2017/06/22
 */
package org.yunshanmc.lmc.core.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.yunshanmc.lmc.core.resource.Resource;

/**
 * //TODO
 */
public interface ConfigManager {

    YamlConfiguration getDefaultConfig(String path);

    YamlConfiguration getConfig(String path);

    YamlConfiguration readConfig(Resource resource);
}
