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
 * 用来标识一个插件
 * XXX: 由于没法必须要继承各个端的插件抽象类，然而没法多继承，暂时找不到好方法，目前所有LMC的实现都会和LMCBukkitPlugin的代码几乎一致
 * //TODO 根据需要改接口的方法随时会增加
 */
public interface LMCPlugin {

    File getDataFolder();

    String getName();

    LocaleManager getLocaleManager();

    ResourceManager getResourceManager();

    ConfigManager getConfigManager();

    MessageManager getMessageManager();
}
