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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StandardResourceManagerTest {
    
    private static Method m_resolvePath;
    private static StandardResourceManager resourceManager;
    
    @Before
    public void setup() throws Exception {
        m_resolvePath = StandardResourceManager.class.getDeclaredMethod("resolvePath", String.class);
        m_resolvePath.setAccessible(true);
        File testDir = new File("build" + File.separator + "testing");
        testDir.mkdirs();
        
        File testJar = new File(testDir, "standardRM.jar");
        if (!testJar.exists()) testJar.createNewFile();
        
        JarOutputStream out = new JarOutputStream(new FileOutputStream(testJar));
        out.putNextEntry(new JarEntry("a"));
        out.write("TEST a".getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.closeEntry();
        out.close();
        
        
        JarFile jar = new JarFile(testJar);
        Constructor<StandardResourceManager> c = StandardResourceManager.class.getDeclaredConstructor(JarFile.class,
                                                                                                      Path.class);
        c.setAccessible(true);
        resourceManager = c.newInstance(jar, testJar.toPath());
    }
    
    @Test
    public void getSelfResource() throws Exception {
        assertNotNull(resourceManager.getSelfResource("a"));
        assertArrayEquals(ByteStreams.toByteArray(resourceManager.getSelfResource("a").getInputStream()),
                          "TEST a".getBytes(StandardCharsets.UTF_8));
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