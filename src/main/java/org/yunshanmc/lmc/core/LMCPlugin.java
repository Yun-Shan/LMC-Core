/*
 * Author: Yun-Shan
 * Date: 2017/06/16
 */
package org.yunshanmc.lmc.core;

import org.bukkit.plugin.java.JavaPlugin;
import org.yunshanmc.lmc.core.config.ConfigManager;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.locale.LocaleManager;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.resource.I18nResourceManager;
import org.yunshanmc.lmc.core.resource.ResourceManager;
import org.yunshanmc.lmc.core.resource.StandardResourceManager;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * //TODO
 */
public abstract class LMCPlugin extends JavaPlugin {
    
    protected boolean useI18n;
    
    protected LocaleManager localeManager;
    protected ResourceManager resourceManager;
    protected ConfigManager configManager;
    protected MessageManager messageManager;
    protected BiConsumer<Throwable, String> exceptionHandler;
    
    @Override
    public final void onLoad() {
        this.init();
        this.setup();
    }
    
    /**
     * 子类进行初始化定制
     */
    protected void init() {}
    
    // 初始化
    private boolean setup() {
        if (this.localeManager == null) this.localeManager = new LocaleManager();
        
        try {
            if (this.resourceManager == null) {
                // 由于使用三元操作符导致自动格式化太难看，且懒得调，故使用if-else
                if (this.useI18n) this.resourceManager = new I18nResourceManager(this, this.localeManager);
                else this.resourceManager = new StandardResourceManager(this);
            }
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return false;
        }
        
        if (this.exceptionHandler != null) this.exceptionHandler = (err, desc) -> {
        
        };
        ExceptionHandler.setHandler(this, this.exceptionHandler);
        return true;
    }
}
