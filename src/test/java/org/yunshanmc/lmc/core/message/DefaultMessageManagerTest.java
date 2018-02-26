package org.yunshanmc.lmc.core.message;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.yunshanmc.lmc.core.MockPlugin;

import static org.junit.Assert.assertEquals;

public class DefaultMessageManagerTest {

    private DefaultMessageManager messageManager;

    @Before
    public void setUp() {
        this.messageManager = (DefaultMessageManager) MockPlugin.newInstance().setGroupMessage(false).getMessageManager();
    }

    @Test
    public void getMessage() {
        assertEquals("gg", getRawMessage("_test1"));
        assertEquals("$测试2", getRawMessage("_test2.test2_1.test2_1_1"));
        assertEquals("成功覆盖", getRawMessage("cover.BeCover_test.gg"));
        assertEquals("§cMissingLanguage: null_key",
                     this.messageManager.getMessage("null_key").getMessage());
    }

    private String getRawMessage(String key) {
        return this.messageManager.getMessage(key).getRawMessage();
    }

}