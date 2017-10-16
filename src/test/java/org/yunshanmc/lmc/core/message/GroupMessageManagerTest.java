package org.yunshanmc.lmc.core.message;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.yunshanmc.lmc.core.config.ConfigManager;
import org.yunshanmc.lmc.core.config.DefaultConfigManager;
import org.yunshanmc.lmc.core.resource.ResourceManager;
import org.yunshanmc.lmc.core.resource.StandardResourceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.*;

public class GroupMessageManagerTest {

    private GroupMessageManager messageManager;

    private static final String MESSAGES_YML_GROUP_A = "" +
                                                       "A_test1: ga\n" +
                                                       "A_test2:\n" +
                                                       "  test2_1:\n" +
                                                       "    test2_1_1: $测试2\n";
    private static final String MESSAGES_YML_GROUP_B_C = "" +
                                                         "BC_test1: gbc\n" +
                                                         "BC_test2:\n" +
                                                         "  test2_1:\n" +
                                                         "    test2_1_1: $测试2\n";
    private static final String MESSAGES_YML_GROUP_D_E_F = "" +
                                                           "DEF_test1: gdef\n" +
                                                           "DEF_test2:\n" +
                                                           "  test2_1:\n" +
                                                           "    test2_1_1: $测试2\n";
    private static final String MESSAGES_YML_GROUP_COVER = "" +
                                                           "BeCover_test:\n" +
                                                           "  gg: 被覆盖";
    private static final String MESSAGES_YML_NO_GROUP = "" +
                                                        "_test1: gg\n" +
                                                        "_test2:\n" +
                                                        "  test2_1:\n" +
                                                        "    test2_1_1: $测试2\n" +
                                                        "cover.BeCover_test:\n" +
                                                        "  gg: 成功覆盖";
    private static final String MESSAGES_YML_USER_GROUP_A = "" +
                                                       "A_test1: ga\n" +
                                                       "A_test2:\n" +
                                                       "  test2_1:\n" +
                                                       "    test2_1_1: $测试2\n";
    private static final String MESSAGES_YML_USER_GROUP_B_C = "" +
                                                         "BC_test1: gbc\n" +
                                                         "BC_test2:\n" +
                                                         "  test2_1:\n" +
                                                         "    test2_1_1: $测试2\n";
    private static final String MESSAGES_YML_USER_GROUP_D_E_F = "" +
                                                           "DEF_test1: gdef\n" +
                                                           "DEF_test2:\n" +
                                                           "  test2_1:\n" +
                                                           "    test2_1_1: $测试2\n";
    private static final String MESSAGES_YML_USER_GROUP_COVER = "" +
                                                           "BeCover_test:\n" +
                                                           "  gg: 被覆盖";
    private static final String MESSAGES_YML_USER_NO_GROUP = "" +
                                                        "u_test1: gg\n" +
                                                        "u_test2:\n" +
                                                        "  test2_1:\n" +
                                                        "    test2_1_1: $测试2\n" +
                                                        "uCover.BeCover_test:\n" +
                                                        "  gg: 成功覆盖";

    @Before
    public void setUp() throws Exception {
        File testDir = new File("build" + File.separator + "testing");
        if (!testDir.exists()) assertTrue(testDir.mkdirs());

        File testJar = new File(testDir, "DefaultGMM.jar");
        JarOutputStream out = new JarOutputStream(new FileOutputStream(testJar));
        out.putNextEntry(new JarEntry("messages/A.yml"));
        out.write(MESSAGES_YML_GROUP_A.getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("messages/B/C.yml"));
        out.write(MESSAGES_YML_GROUP_B_C.getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("messages/D/E/F.yml"));
        out.write(MESSAGES_YML_GROUP_D_E_F.getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("messages.yml"));
        out.write(MESSAGES_YML_NO_GROUP.getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("messages/cover.yml"));
        out.write(MESSAGES_YML_GROUP_COVER.getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.close();

        // =====================================================================================

        File user = new File(testDir, "messages/uA.yml");
        if (!user.exists()) {
            if (!user.getParentFile().exists()) assertTrue(user.getParentFile().mkdirs());
            assertTrue(user.createNewFile());
        }
        FileOutputStream userOut = new FileOutputStream(user);
        userOut.write(MESSAGES_YML_USER_GROUP_A.getBytes(StandardCharsets.UTF_8));
        userOut.close();

        user = new File(testDir, "messages/uB/C.yml");
        if (!user.exists()) {
            if (!user.getParentFile().exists()) assertTrue(user.getParentFile().mkdirs());
            assertTrue(user.createNewFile());
        }
        userOut = new FileOutputStream(user);
        userOut.write(MESSAGES_YML_USER_GROUP_B_C.getBytes(StandardCharsets.UTF_8));
        userOut.close();

        user = new File(testDir, "messages/uD/E/F.yml");
        if (!user.exists()) {
            if (!user.getParentFile().exists()) assertTrue(user.getParentFile().mkdirs());
            assertTrue(user.createNewFile());
        }
        userOut = new FileOutputStream(user);
        userOut.write(MESSAGES_YML_USER_GROUP_D_E_F.getBytes(StandardCharsets.UTF_8));
        userOut.close();

        user = new File(testDir, "messages.yml");
        if (!user.exists()) {
            if (!user.getParentFile().exists()) assertTrue(user.getParentFile().mkdirs());
            assertTrue(user.createNewFile());
        }
        userOut = new FileOutputStream(user);
        userOut.write(MESSAGES_YML_USER_NO_GROUP.getBytes(StandardCharsets.UTF_8));
        userOut.close();

        user = new File(testDir, "messages/uCover.yml");
        if (!user.exists()) {
            if (!user.getParentFile().exists()) assertTrue(user.getParentFile().mkdirs());
            assertTrue(user.createNewFile());
        }
        userOut = new FileOutputStream(user);
        userOut.write(MESSAGES_YML_USER_GROUP_COVER.getBytes(StandardCharsets.UTF_8));
        userOut.close();

        Constructor<StandardResourceManager> c = StandardResourceManager.class.getDeclaredConstructor(File.class,
                                                                                                      Path.class);
        c.setAccessible(true);
        ResourceManager resourceManager = c.newInstance(testJar, testDir.toPath());
        ConfigManager configManager = new DefaultConfigManager(resourceManager);
        this.messageManager = new GroupMessageManager(configManager);
    }

    @Test
    public void getMessageFromResource() throws Exception {
        assertEquals("ga", getRawMessage("A.A_test1"));
        assertEquals("$测试2", getRawMessage("A.A_test2.test2_1.test2_1_1"));
        assertEquals("gbc", getRawMessage("B.C.BC_test1"));
        assertEquals("$测试2", getRawMessage("B.C.BC_test2.test2_1.test2_1_1"));
        assertEquals("gdef", getRawMessage("D.E.F.DEF_test1"));
        assertEquals("$测试2", getRawMessage("D.E.F.DEF_test2.test2_1.test2_1_1"));
        assertEquals("gg", getRawMessage("_test1"));
        assertEquals("$测试2", getRawMessage("_test2.test2_1.test2_1_1"));
        assertEquals("成功覆盖", getRawMessage("cover.BeCover_test.gg"));

        assertEquals("ga", getRawMessage("uA.A_test1"));
        assertEquals("$测试2", getRawMessage("uA.A_test2.test2_1.test2_1_1"));
        assertEquals("gbc", getRawMessage("uB.C.BC_test1"));
        assertEquals("$测试2", getRawMessage("uB.C.BC_test2.test2_1.test2_1_1"));
        assertEquals("gdef", getRawMessage("uD.E.F.DEF_test1"));
        assertEquals("$测试2", getRawMessage("uD.E.F.DEF_test2.test2_1.test2_1_1"));
        assertEquals("gg", getRawMessage("u_test1"));
        assertEquals("$测试2", getRawMessage("u_test2.test2_1.test2_1_1"));
        assertEquals("成功覆盖", getRawMessage("uCover.BeCover_test.gg"));
    }

    private String getRawMessage(String key) {
        return this.messageManager.getMessage(key).getRawMessage();
    }

}