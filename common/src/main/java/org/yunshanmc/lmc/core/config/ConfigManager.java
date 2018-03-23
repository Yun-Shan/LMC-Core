/*
 * Author: Yun-Shan
 * Date: 2017/06/22
 */
package org.yunshanmc.lmc.core.config;

import org.yunshanmc.lmc.core.config.bukkitcfg.file.FileConfiguration;
import org.yunshanmc.lmc.core.resource.Resource;

import java.util.Map;

/**
 * 配置管理器.
 *
 * @author Yun-Shan
 */
public interface ConfigManager {

    /**
     * 获取配置.
     * <p>
     * 相当于<br>
     * <pre>
     *     FileConfiguration> cfg = {@link #getUserConfig(String) getUserConfig}(path);
     *     if (cfg == null) {
     *         cfg = {@link #getDefaultConfig(String) getDefaultConfig}(path);
     *     }
     * </pre>
     *
     * @param path 配置文件的相对路径
     * @return 获取到的配置，当配置文件不存在或读取失败时返回null
     */
    FileConfiguration getConfig(String path);

    /**
     * 从自身Jar获取配置.
     * <p>
     *
     * @param path 配置文件的相对路径
     * @return 获取到的配置，当配置文件不存在或读取失败时返回null
     */
    FileConfiguration getDefaultConfig(String path);

    /**
     * 从插件文件夹获取配置.
     * <p>
     *
     * @param path 配置文件的相对路径
     * @return 获取到的配置，当配置文件不存在或读取失败时返回null
     */
    FileConfiguration getUserConfig(String path);

    /**
     * 获取多个配置.
     * <p>
     * 相当于
     * <pre>
     *     Map<String, FileConfiguration> cfgs = {@link #getUserConfigs(String, boolean) getUserConfigs}(path);
     *     if (cfgs == null) {
     *         cfgs = {@link #getDefaultConfigs(String, boolean) getDefaultConfigs}(path);
     *     }
     * </pre>
     *
     *
     * @param path 配置文件夹的相对路径
     * @param deep 是否搜索子文件夹
     * @return 获取到的配置(K -> 文件相对路径, V -> 配置)，当找不到任何配置文件或读取失败时返回null
     */
    Map<String, FileConfiguration> getConfigs(String path, boolean deep);

    /**
     * 从自身Jar获取多个配置.
     * <p>
     *
     * @param path 配置文件夹的相对路径
     * @param deep 是否搜索子文件夹
     * @return 获取到的配置(K -> 文件相对路径, V -> 配置)，当找不到任何配置文件或读取失败时返回null
     */
    Map<String, FileConfiguration> getDefaultConfigs(String path, boolean deep);

    /**
     * 从插件文件夹获取多个配置.
     * <p>
     *
     * @param path 配置文件夹的相对路径
     * @param deep 是否搜索子文件夹
     * @return 获取到的配置(K -> 文件相对路径, V -> 配置)，当找不到任何配置文件或读取失败时返回null
     */
    Map<String, FileConfiguration> getUserConfigs(String path, boolean deep);

    /**
     * 获取插件配置.
     * <p>
     * 相当于调用<code>{@link #getConfig(String) getConfig}("config.yml")</code>
     *
     * @return 插件配置，若没有配置也不会返回null
     */
    FileConfiguration getPluginConfig();

    /**
     * 从资源中读取配置
     *
     * @param resource 配置所在资源
     * @return 读取到的配置，读取失败返回null
     */
    FileConfiguration readConfig(Resource resource);
}
