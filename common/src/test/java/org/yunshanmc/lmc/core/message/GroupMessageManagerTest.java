package org.yunshanmc.lmc.core.message;

import org.junit.BeforeClass;
import org.junit.Test;
import org.yunshanmc.lmc.core.MockPlugin;

import static org.junit.Assert.*;

public class GroupMessageManagerTest {

    private static BaseGroupMessageManager messageManager;

    @BeforeClass
    public static void setUp() {
        messageManager = (BaseGroupMessageManager) MockPlugin.newInstance().setGroupMessage(true).getMessageManager();
    }

    @Test
    public void getMessageFromResource() {
        assertEquals("ga", getRawMessage("A.A_test1"));
        assertEquals("$测试2", getRawMessage("A.A_test2.test2_1.test2_1_1"));
        assertEquals("gbc", getRawMessage("B.C.BC_test1"));
        assertEquals("$测试2", getRawMessage("B.C.BC_test2.test2_1.test2_1_1"));
        assertEquals("gdef", getRawMessage("D.E.F.DEF_test1"));
        assertEquals("$测试2", getRawMessage("D.E.F.DEF_test2.test2_1.test2_1_1"));
        assertEquals("gg", getRawMessage("test1"));
        assertEquals("$测试2", getRawMessage("test2.test2_1.test2_1_1"));

        assertEquals("ga", getRawMessage("uA.A_test1"));
        assertEquals("$测试2", getRawMessage("uA.A_test2.test2_1.test2_1_1"));
        assertEquals("gbc", getRawMessage("uB.C.BC_test1"));
        assertEquals("$测试2", getRawMessage("uB.C.BC_test2.test2_1.test2_1_1"));
        assertEquals("gdef", getRawMessage("uD.E.F.DEF_test1"));
        assertEquals("$测试2", getRawMessage("uD.E.F.DEF_test2.test2_1.test2_1_1"));
        assertEquals("ggu", getRawMessage("u_test1"));
        assertEquals("$测试2u", getRawMessage("u_test2.test2_1.test2_1_1"));
        assertEquals("成功覆盖", getRawMessage("cover.BeCover_test.gg"));
        assertEquals("§c未知的提示信息模板： null_key", getRawMessage("null_key"));
    }

    private String getRawMessage(String key) {
        return messageManager.getMessage(key).getRawMessage();
    }

}