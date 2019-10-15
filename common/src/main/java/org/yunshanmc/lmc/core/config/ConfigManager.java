/*
 * Author: Yun-Shan
 * Date: 2017/06/22
 */
package org.yunshanmc.lmc.core.config;

import org.yunshanmc.lmc.core.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * 配置管理器.
 *
 * @author Yun-Shan
 */
public interface ConfigManager {

    /**
     * 通过注解读取配置
     * TODO 读取过程中出现异常应抛出一个自定义异常，保证正常返回时必然不是null
     *
     * @param clazz 任意类，需要保证有一个无参构造函数
     * @param <T>   任意类
     * @return 实例化后的配置类，如果读取过程中出现异常则返回null
     * @see ConfigFile
     */
    <T> T getConfig(Class<T> clazz);

    /**
     * 获取配置.
     * <p>
     * 相当于<br>
     * <pre>
     * FileConfiguration cfg = {@link #getUserConfig(String) getUserConfig}(path);
     * if (cfg == null) {
     *     cfg = {@link #getDefaultConfig(String) getDefaultConfig}(path);
     * }
     * </pre>
     *
     * @param path 配置文件的相对路径
     * @return 获取到的配置，当配置文件不存在或读取失败时返回null
     */
    @Nullable
    LMCConfiguration getConfig(String path);

    /**
     * 从自身Jar获取配置.
     * <p>
     *
     * @param path 配置文件的相对路径
     * @return 获取到的配置，当配置文件不存在或读取失败时返回null
     */
    @Nullable
    LMCConfiguration getDefaultConfig(String path);

    /**
     * 从插件文件夹获取配置.
     * <p>
     *
     * @param path 配置文件的相对路径
     * @return 获取到的配置，当配置文件不存在或读取失败时返回null
     */
    @Nullable
    LMCConfiguration getUserConfig(String path);

    /**
     * 获取多个配置.
     * <p>
     * 相当于
     * <pre>
     * Map{@code <String, FileConfiguration>} cfgs = {@link #getUserConfigs(String, boolean) getUserConfigs}(path);
     * if (cfgs == null) {
     *     cfgs = {@link #getDefaultConfigs(String, boolean) getDefaultConfigs}(path);
     * }
     * </pre>
     *
     * @param path 配置文件夹的相对路径
     * @param deep 是否搜索子文件夹
     * @return 获取到的配置(K: 文件相对路径, V: 配置)，当找不到任何配置文件或读取失败时返回null
     */
    @Nullable
    Map<String, LMCConfiguration> getConfigs(String path, boolean deep);

    /**
     * 从自身Jar获取多个配置.
     * <p>
     *
     * @param path 配置文件夹的相对路径
     * @param deep 是否搜索子文件夹
     * @return 获取到的配置(K: 文件相对路径, V: 配置)，当找不到任何配置文件或读取失败时返回null
     */
    @Nullable
    Map<String, LMCConfiguration> getDefaultConfigs(String path, boolean deep);

    /**
     * 从插件文件夹获取多个配置.
     * <p>
     *
     * @param path 配置文件夹的相对路径
     * @param deep 是否搜索子文件夹
     * @return 获取到的配置(K: 文件相对路径, V: 配置)，当找不到任何配置文件或读取失败时返回null
     */
    @Nullable
    Map<String, LMCConfiguration> getUserConfigs(String path, boolean deep);

    /**
     * 获取主配置.
     * <p>
     * 相当于调用<code>{@link #getConfig(String) getConfig}("config.yml")</code>
     *
     * @return 主配置(若没有配置也不会返回null ， 而是返回一个空的配置)
     */
    @Nonnull
    LMCConfiguration getMainConfig();

    /**
     * 从资源中读取配置
     *
     * @param resource 配置所在资源
     * @return 读取到的配置，读取失败返回null
     */
    @Nullable
    LMCConfiguration readConfig(Resource resource);
}
