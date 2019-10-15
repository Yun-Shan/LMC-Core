package org.yunshanmc.lmc.core.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.*;

public class MockResourceManager implements ResourceManager {

    private StandardResourceManager standardResourceManager;

    public MockResourceManager() throws Exception {
        File testDir = Paths.get("build", "resources", "test").toAbsolutePath().toFile();
        assertTrue(testDir.exists() || testDir.mkdirs());

        File testJar = new File(testDir, "plugin-resource.jar");
        if (!testJar.exists()) {
            assertTrue(testJar.createNewFile());
            JarOutputStream jar = new JarOutputStream(new FileOutputStream(testJar));
            jar.close();
        }

        Constructor<StandardResourceManager> c = StandardResourceManager.class.getDeclaredConstructor(File.class, Path.class);
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