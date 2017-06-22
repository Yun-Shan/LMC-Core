/*
 * Author: Yun-Shan
 * Date: 2017/06/22
 */
package org.yunshanmc.lmc.core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * //TODO
 */
public interface ConfigManager {

    FileConfiguration getDefaultConfig(String path);
    
    FileConfiguration getConfig(String path);

}
