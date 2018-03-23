package org.yunshanmc.lmc.core.resource;

import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.ResourceManager;
import org.yunshanmc.lmc.core.resource.StandardResourceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.assertTrue;

public class MockResourceManager implements ResourceManager {

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

    private StandardResourceManager standardResourceManager;

    public MockResourceManager() throws Exception {
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
        this.standardResourceManager = c.newInstance(testJar, testDir.toPath());
    }


    @Override
    public void updateJar() throws IOException {
        standardResourceManager.updateJar();
    }

    @Override
    public Resource getSelfResource(String path) {
        return standardResourceManager.getSelfResource(path);
    }

    @Override
    public Map<String, Resource> getSelfResources(String path, Predicate<String> nameFilter, boolean deep) {
        return standardResourceManager.getSelfResources(path, nameFilter, deep);
    }

    @Override
    public Resource getFolderResource(String path) {
        return standardResourceManager.getFolderResource(path);
    }

    @Override
    public Map<String, Resource> getFolderResources(String path, Predicate<String> nameFilter, boolean deep) {
        return standardResourceManager.getFolderResources(path, nameFilter, deep);
    }

    @Override
    public boolean writeResource(String path, Resource resource, boolean force) {
        return standardResourceManager.writeResource(path, resource, force);
    }

    @Override
    public boolean saveDefaultResource(String selfPath, String dirPath, boolean force) {
        return standardResourceManager.saveDefaultResource(selfPath, dirPath, force);
    }
}
