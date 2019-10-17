package org.yunshanmc.lmc.core.resource;

import com.google.common.io.ByteStreams;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.*;

public class StandardResourceManagerTest {

    private static Method m_resolvePath;
    private static StandardResourceManager resourceManager;

    private static File testDir;
    private static File testJar;

    @BeforeClass
    public static void setUp() throws Exception {
        // 由于该类中的测试有直接写jar文件的操作，为避免影响其它测试，使用自己独立的jar文件

        m_resolvePath = StandardResourceManager.class.getDeclaredMethod("resolvePath", String.class, boolean.class, FileSystem.class);
        m_resolvePath.setAccessible(true);
        testDir = Paths.get("build", "resources", "test").toAbsolutePath().toFile();
        if (!testDir.exists()) assertTrue(testDir.mkdirs());

        testJar = new File(testDir, "standardRM.jar");
        if (testJar.exists()) assertTrue(testJar.delete());
        JarOutputStream out = new JarOutputStream(new FileOutputStream(testJar));
        out.putNextEntry(new JarEntry("a"));
        out.write("TEST a".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("b/c"));
        out.write("TEST a-b-c".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.flush();
        out.close();
        Constructor<StandardResourceManager> c = StandardResourceManager.class.getDeclaredConstructor(File.class, Path.class);
        c.setAccessible(true);
        resourceManager = c.newInstance(testJar, testDir.toPath());
    }

    @Test
    public void getSelfResource() throws Exception {
        assertNotNull(resourceManager.getSelfResource("a"));
        assertArrayEquals(ByteStreams.toByteArray(resourceManager.getSelfResource("a").getInputStream()),
            "TEST a".getBytes(StandardCharsets.UTF_8));
        assertNotNull(resourceManager.getSelfResource("b/c"));
        assertArrayEquals(ByteStreams.toByteArray(resourceManager.getSelfResource("b/c").getInputStream()),
            "TEST a-b-c".getBytes(StandardCharsets.UTF_8));
        // Jar文件更新，并通知资源管理器
        JarOutputStream out = new JarOutputStream(new FileOutputStream(testJar));
        out.putNextEntry(new JarEntry("a"));
        out.write("TEST a".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("e/f/g"));
        out.write("TEST e-f-g".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("q"));
        out.write("TEST a-b-c".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("b/c"));
        out.write("TEST a-b-c-d".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.flush();
        out.close();
        resourceManager.updateJar();
        assertEquals(4, resourceManager.getSelfResources("/", null, true).values().size());
        Resource res = resourceManager.getSelfResource("b/c");
        assertNotNull(res);
        assertArrayEquals(ByteStreams.toByteArray(res.getInputStream()),
            "TEST a-b-c-d".getBytes(StandardCharsets.UTF_8));
        res = resourceManager.getSelfResource("e/f/g");
        assertNotNull(res);
        assertArrayEquals(ByteStreams.toByteArray(res.getInputStream()),
            "TEST e-f-g".getBytes(StandardCharsets.UTF_8));
        res = resourceManager.getSelfResource("q");
        assertNotNull(res);
        assertArrayEquals(ByteStreams.toByteArray(res.getInputStream()),
            "TEST a-b-c".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void getFileResource() throws Exception {/// TODO

    }

    @Test
    public void getFolderResources() throws Exception {
    }

    @Test
    public void writeResource() throws Exception {
        Resource res = resourceManager.getSelfResource("a");
        assertNotNull(res);
        assertArrayEquals(ByteStreams.toByteArray(res.getInputStream()), "TEST a".getBytes(StandardCharsets.UTF_8));
        File f = new File(testDir, "a");
        if (f.exists()) assertTrue(f.delete());
        assertTrue(resourceManager.writeResource("a", resourceManager.getSelfResource("a"), true));
        assertTrue(f.exists());
        assertEquals(new String(ByteStreams.toByteArray(Files.newInputStream(f.toPath())),
            StandardCharsets.UTF_8), "TEST a");
    }


    @Test
    public void resolvePath() throws Exception {
        assertEquals(Paths.get("/"), resolvePath("/"));
        assertEquals(Paths.get("a"), resolvePath("a"));
        assertEquals(Paths.get("c/d"), resolvePath("a/b/../../c/d"));
        try {
            resolvePath(null);
        } catch (InvocationTargetException e) {
            assertEquals("Invalid Path: (null or empty)", e.getCause().getMessage());
        }
        try {
            resolvePath("a/b/../../../../../c/d");
        } catch (InvocationTargetException e) {
            assertEquals("Invalid Path: a/b/../../../../../c/d", e.getCause().getMessage());
        }
        try {
            resolvePath("a/..");
        } catch (InvocationTargetException e) {
            assertEquals("Invalid Path: a/.. (only allow file, but there is a directory)", e.getCause().getMessage());
        }
    }

    private Object resolvePath(String path) throws Exception {
        return this.resolvePath(path, FileSystems.getDefault());
    }

    private Object resolvePath(String path, FileSystem fs) throws Exception {
        return m_resolvePath.invoke(null, path, true, fs);
    }
}