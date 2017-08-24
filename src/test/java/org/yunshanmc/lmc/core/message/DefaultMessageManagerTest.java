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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultMessageManagerTest {

    private DefaultMessageManager messageManager;

    private static final String MESSAGES_YML = "" +
            "test1: abc\n" +
            "test2: qqq\n" +
            "test3:\n" +
            "  test3_1:\n" +
            "    test3_1_1: 测试\n" +
            "    test3_1_2: $测试2\n";

    @Before
    public void setUp() throws Exception {
        File testDir = new File("build" + File.separator + "testing");
        if (!testDir.exists()) assertTrue(testDir.mkdirs());

        File testJar = new File(testDir, "DefaultMM.jar");
        JarOutputStream out = new JarOutputStream(new FileOutputStream(testJar));
        out.putNextEntry(new JarEntry("messages.yml"));
        out.write(MESSAGES_YML.getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.close();
        Constructor<StandardResourceManager> c = StandardResourceManager.class.getDeclaredConstructor(File.class,
                Path.class);
        c.setAccessible(true);
        ResourceManager resourceManager = c.newInstance(testJar, testDir.toPath());
        ConfigManager configManager = new DefaultConfigManager(resourceManager);
        this.messageManager = new DefaultMessageManager(configManager);
    }

    @Test
    public void getMessage() throws Exception {
        assertEquals("abc", getRawMessage("test1"));
        assertEquals("qqq", getRawMessage("test2"));
        assertEquals("测试", getRawMessage("test3.test3_1.test3_1_1"));
        assertEquals("$测试2", getRawMessage("test3.test3_1.test3_1_2"));
        assertEquals("§cMissingLanguage: null_key", this.messageManager.getMessage("null_key").getMessage((Player)null));
    }

    private String getRawMessage(String key) {
        return this.messageManager.getMessage(key).getRawMessage();
    }

}