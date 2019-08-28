package org.yunshanmc.lmc.core.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author YunShan
 */
public interface LMCConfiguration {

    /**
     * 增加默认值
     *
     * @param defaults 默认值
     */
    void addDefaults(LMCConfiguration defaults);

    /**
     * 获取所有配置键
     * <p>
     * Note: 不会递归获取
     *
     * @return 所有配置键
     */
    Set<String> getKeys();

    /**
     * 获取指定路径的子配置
     *
     * @param path 配置路径
     * @return 对应路径的子配置，不存在时返回null
     */
    LMCConfiguration getSection(String path);

    /**
     * 获取指定路径的对象
     *
     * @param path 配置路径
     * @return 对应路径的对象，不存在时返回null
     */
    @Nullable
    Object get(@Nonnull String path);

    /**
     * 获取指定路径的对象
     *
     * @param path 配置路径
     * @param def  默认值
     * @param <T>  对象类型
     * @return 对应路径的对象，不存在时返回默认值
     */
    @Nullable
    <T> T get(@Nonnull String path, @Nullable T def);

    /**
     * 获取指定路径的字符串
     *
     * @param path 配置路径
     * @return 对应路径的字符串，不存在时返回null
     */
    @Nullable
    String getString(@Nonnull String path);

    /**
     * 获取指定路径的字符串
     *
     * @param path 配置路径
     * @param def  默认值
     * @return 对应路径的字符串，不存在时返回默认值
     */
    @Nullable
    String getString(@Nonnull String path, @Nullable String def);

    /**
     * 获取指定路径的int
     *
     * @param path 配置路径
     * @return 对应路径的int，不存在时返回0
     */
    int getInt(@Nonnull String path);

    /**
     * 获取指定路径的int
     *
     * @param path 配置路径
     * @param def  默认值
     * @return 对应路径的字符串，不存在时返回默认值
     */
    int getInt(@Nonnull String path, int def);
}
