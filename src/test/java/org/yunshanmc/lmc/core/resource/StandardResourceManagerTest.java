package org.yunshanmc.lmc.core.resource;

import com.google.common.io.ByteStreams;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import static org.junit.Assert.*;

public class StandardResourceManagerTest {
    
    private static Method m_resolvePath;
    private static StandardResourceManager resourceManager;
    
    private static File testDir;
    private static File testJar;
    
    @Before
    public void setUp() throws Exception {
        m_resolvePath = StandardResourceManager.class.getDeclaredMethod("resolvePath", String.class);
        m_resolvePath.setAccessible(true);
        testDir = new File("build" + File.separator + "testing");
        if (!testDir.exists()) assertTrue(testDir.mkdirs());
        
        testJar = new File(testDir, "standardRM.jar");
        JarOutputStream out = new JarOutputStream(new FileOutputStream(testJar));
        out.putNextEntry(new JarEntry("a"));
        out.write("TEST a".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("b/c"));
        out.write("TEST a-b-c".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.close();
        Constructor<StandardResourceManager> c = StandardResourceManager.class.getDeclaredConstructor(File.class,
                                                                                                      Path.class);
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
        out.putNextEntry(new JarEntry("e/f/g"));
        out.write("TEST a".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.putNextEntry(new JarEntry("q"));
        out.write("TEST a-b-c".getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
        out.close();
        resourceManager.updateJar();
        assertNotNull(resourceManager.getSelfResource("e/f/g"));
        assertArrayEquals(ByteStreams.toByteArray(resourceManager.getSelfResource("e/f/g").getInputStream()),
                          "TEST a".getBytes(StandardCharsets.UTF_8));
        assertNotNull(resourceManager.getSelfResource("q"));
        assertArrayEquals(ByteStreams.toByteArray(resourceManager.getSelfResource("q").getInputStream()),
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
        assertTrue(resourceManager.writeResource("a", res, true));
        assertTrue(new File(testDir, "a").exists());
    }
    
    
    @Test
    public void resolvePath() throws Exception {
        assertEquals(null, resolvePath(null));
        assertEquals(null, resolvePath(""));
        assertEquals(Paths.get("a"), resolvePath("a"));
    }
    
    private static Object resolvePath(String path) throws Exception {
        return m_resolvePath.invoke(null, path);
    }
}