/*
 * Author: Yun-Shan
 * Date: 2017/06/16
 */
package org.yunshanmc.lmc.core;

import org.yunshanmc.lmc.core.config.ConfigManager;
import org.yunshanmc.lmc.core.locale.LocaleManager;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.File;

/**
 * 用来标识一个插件.
 * <p>
 * XXX: 由于必须要继承各个端的插件抽象类，然而没法多继承，暂时找不到好方法，目前所有LMC的实现都会和LMCBukkitPlugin的代码几乎一致
 * NOTE: 根据需要该接口的方法随时会增加
 *
 * @author Yun-Shan
 */
public interface LMCPlugin {

    /**
     * 获取插件文件夹
     *
     * @return 插件文件夹(plugins/插件名)
     */
    File getDataFolder();

    /**
     * 获取插件名
     *
     * @return 插件名
     */
    String getName();

    /**
     * 获取本地化管理器
     *
     * @return 本地化管理器
     */
    LocaleManager getLocaleManager();

    /**
     * 获取资源管理器
     *
     * @return 资源管理器
     */
    ResourceManager getResourceManager();

    /**
     * 获取配置管理器
     *
     * @return 配置管理器
     */
    ConfigManager getConfigManager();

    /**
     * 获取信息管理器
     *
     * @return 信息管理器
     */
    MessageManager getMessageManager();
}
