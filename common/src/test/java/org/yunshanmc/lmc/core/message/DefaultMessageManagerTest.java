package org.yunshanmc.lmc.core.message;

import org.junit.BeforeClass;
import org.junit.Test;
import org.yunshanmc.lmc.core.MockPlugin;

import static org.junit.Assert.*;

public class DefaultMessageManagerTest {

    private static BaseMessageManager messageManager;

    @BeforeClass
    public static void setUp() {
        messageManager = (BaseMessageManager) MockPlugin.newInstance().setGroupMessage(false).getMessageManager();
    }

    @Test
    public void getMessage() {
        assertEquals("gg", getRawMessage("_test1"));
        assertEquals("$测试2", getRawMessage("_test2.test2_1.test2_1_1"));
        assertEquals("成功覆盖", getRawMessage("cover.BeCover_test.gg"));
        assertEquals("§c未知的提示信息模板：null_key",
            messageManager.getMessage("null_key").getMessage(null));
    }

    private String getRawMessage(String key) {
        return messageManager.getMessage(key).getRawMessage();
    }

}