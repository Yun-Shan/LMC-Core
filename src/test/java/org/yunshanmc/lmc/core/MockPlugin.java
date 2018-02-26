package org.yunshanmc.lmc.core;

import org.yunshanmc.lmc.core.config.ConfigManager;
import org.yunshanmc.lmc.core.config.DefaultConfigManager;
import org.yunshanmc.lmc.core.internal.Utils;
import org.yunshanmc.lmc.core.locale.LocaleManager;
import org.yunshanmc.lmc.core.message.DefaultMessageManager;
import org.yunshanmc.lmc.core.message.GroupMessageManager;
import org.yunshanmc.lmc.core.message.MessageManager;
import org.yunshanmc.lmc.core.resource.MockResourceManager;
import org.yunshanmc.lmc.core.resource.ResourceManager;

import java.io.File;

public class MockPlugin implements LMCPlugin {

    private MessageManager messageManager;
    private MockResourceManager resourceManager;
    private DefaultConfigManager configManager;

    public static MockPlugin getInstance() {
        return new MockPlugin();
    }

    private MockPlugin() {
        Utils.setLMCCorePlugin(this);
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public String getName() {
        return "Mock";
    }

    @Override
    public LocaleManager getLocaleManager() {
        return null;
    }

    @Override
    public ResourceManager getResourceManager() {
        if (this.resourceManager == null) {
            try {
                this.resourceManager = new MockResourceManager();
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
        return this.resourceManager;
    }

    @Override
    public ConfigManager getConfigManager() {
        if (this.configManager == null) this.configManager = new DefaultConfigManager(this.getResourceManager());
        return this.configManager;
    }

    private boolean isGroupMM = false;

    public MockPlugin setGroupMessage(boolean groupMessage) {
        isGroupMM = groupMessage;
        return this;
    }

    @Override
    public MessageManager getMessageManager() {
        if (this.messageManager == null) this.messageManager =
                this.isGroupMM ? new GroupMessageManager(this, this.getConfigManager())
                               : new DefaultMessageManager(this, this.getConfigManager());
        return this.messageManager;
    }


}
