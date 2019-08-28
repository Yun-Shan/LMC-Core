package org.yunshanmc.lmc.core.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.bungee.config.BungeeCordConfigManager;
import org.yunshanmc.lmc.core.bungee.message.BungeeGroupMessageManager;
import org.yunshanmc.lmc.core.bungee.message.BungeeMessageManager;
import org.yunshanmc.lmc.core.config.ConfigManager;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.locale.DefaultLocaleManager;
import org.yunshanmc.lmc.core.locale.I18nResourceManager;
import org.yunshanmc.lmc.core.locale.LocaleManager;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.message.MessageSender;
import org.yunshanmc.lmc.core.resource.ResourceManager;
import org.yunshanmc.lmc.core.resource.StandardResourceManager;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * LMC插件的BungeeCord实现
 * XXX: 由于必须要继承各个端的插件抽象类，暂时找不到好方法，目前所有LMC的实现都会和LMCBukkitPlugin的代码几乎一致
 *
 * @author Yun-Shan
 */
public abstract class BaseLMCBungeeCordPlugin extends Plugin implements LMCPlugin {

    protected boolean useI18n;
    protected boolean useGroupMessage;

    protected LocaleManager localeManager;
    protected ResourceManager resourceManager;
    protected ConfigManager configManager;
    protected MessageManager messageManager;
    protected Consumer<ExceptionHandler.ExceptionInfo> exceptionHandler;

    protected MessageSender messageSender;

    @Override
    public final void onLoad() {
        this.init();
        this.setup();
    }

    /**
     * 子类进行初始化定制
     */
    protected void init() {}

    /**
     * 初始化
     *
     * @return 是否初始化成功
     */
    private boolean setup() {
        if (this.localeManager == null) {
            this.localeManager = new DefaultLocaleManager();
        }

        //noinspection Duplicates
        try {
            if (this.resourceManager == null) {
                // 由于使用三元操作符导致自动格式化太难看，且懒得调整，故使用if-else
                if (this.useI18n) {
                    this.resourceManager = new I18nResourceManager(this, this.localeManager);
                } else {
                    this.resourceManager = new StandardResourceManager(this);
                }
            }
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return false;
        }
        if (this.configManager == null) {
            this.configManager = new BungeeCordConfigManager(this.resourceManager);
        }
        if (this.messageManager == null) {
            this.messageManager = this.useGroupMessage
                ? new BungeeGroupMessageManager(this, this.resourceManager)
                : new BungeeMessageManager(this, this.resourceManager);
        }

        this.messageSender = this.messageManager.getMessageSender();

        /* 为避免插件信息相关功能初始化失败导致报错信息异常，
         * 在资源管理器和信息管理器都初始化完毕之后才设置异常处理器，
         * 若资源管理器和信息管理器初始化出现异常，将由默认异常处理器处理
         */
        if (this.exceptionHandler == null) {
            this.exceptionHandler = ExceptionHandler.DEFAULT_HANDLER;
        }
        ExceptionHandler.setHandler(this, this.exceptionHandler);
        return true;
    }

    @Override
    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public String getName() {
        return this.getDescription().getName();
    }
}
