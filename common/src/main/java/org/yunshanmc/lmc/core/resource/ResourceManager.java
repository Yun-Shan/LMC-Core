/*
 * Author: Yun-Shan
 * Date: 2017/06/13
 */
package org.yunshanmc.lmc.core.resource;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 资源管理器，用于管理插件自身和相应文件夹的资源.
 * <p>
 *
 * @author Yun-Shan
 */
public interface ResourceManager {
    
    /**
     * 通知资源管理器更新Jar
     * <p>
     * 当Jar文件更新但不重载插件时可调用，通常用于调试<br>
     * <strong>重载插件时会重新构建资源管理器，故若重载插件则无需调用此方法主动更新</strong><br>
     * 注意：此处的"插件"指包括本插件在内的所有以本插件为开发框架制作的插件
     *
     * @throws IOException 更新Jar失败时抛出
     */
    void updateJar() throws IOException;
    
    /**
     * 获取自身Jar的资源
     *
     * @param path 资源路径(开头可加"/"可不加)
     * @return 表示资源的对象，当资源不存在或获取失败时返回null
     */
    Resource getSelfResource(String path);

    /**
     * 获取自身Jar文件夹的资源
     * <p>
     * 此方法会获取某个文件夹中所有符合条件的资源
     *
     * @param path       文件夹路径
     * @param nameFilter 文件名过滤器，传null视为获取所有文件资源
     * @param deep       是否深度遍历，为true时会遍历子文件夹的文件
     * @return 文件相对路径以及表示其资源的对象组成的Map，当文件夹不存在或没有符合条件的资源时返回null
     */
    Map<String, Resource> getSelfResources(String path, Predicate<String> nameFilter, boolean deep);

    /**
     * 获取插件文件夹中的文件资源
     *
     * @param path 资源路径
     * @return 表示资源的对象，当资源不存在时返回null
     */
    Resource getFolderResource(String path);
    
    /**
     * 获取插件文件夹的资源
     * <p>
     * 此方法会获取某个文件夹中所有符合条件的资源
     *
     * @param path       文件夹路径
     * @param nameFilter 文件名过滤器，传null视为获取所有文件资源
     * @param deep       是否深度遍历，为true时会遍历子文件夹的文件
     * @return 文件相对路径以及表示其资源的对象组成的Map，当文件夹不存在或没有符合条件的资源时返回null
     */
    Map<String, Resource> getFolderResources(String path, Predicate<String> nameFilter, boolean deep);
    
    /**
     * 向插件文件夹写入资源
     * <p>
     * <b>注意：若参数<code>force</code>设为false，则当资源已存在时会直接返回true，而不会尝试写入</b>
     *
     * @param path       资源路径
     * @param resource 要写入的资源
     * @param force      当资源已存在时是否要覆盖
     * @return 成功写入返回true，否则返回false
     */
    boolean writeResource(String path, Resource resource, boolean force);

    /**
     * 向插件文件夹写入默认资源
     * <p>
     * <b>注意：若参数<code>force</code>设为false，则当资源已存在时会直接返回true，而不会尝试写入</b>
     *
     * @param selfPath 资源在插件Jar中的路径
     * @param dirPath 资源要写入到插件文件夹的相对路径
     * @param force 当插件文件夹有同名文件存在时是否覆盖
     * @return 保存成功 或 已有同名文件且不强制覆盖 时返回true，否则返回false
     */
    boolean saveDefaultResource(String selfPath, String dirPath, boolean force);
}
