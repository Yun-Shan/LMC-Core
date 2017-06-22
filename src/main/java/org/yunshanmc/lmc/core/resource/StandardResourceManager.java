/*
 * Author: Yun-Shan
 * Date: 2017/06/14
 */
package org.yunshanmc.lmc.core.resource;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.plugin.Plugin;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 标准资源管理器
 */
public class StandardResourceManager implements ResourceManager {
    
    private final JarFile jar;
    private final Path pluginFolder;
    
    private String jarRootPath;
    
    /**
     * 通过Bukkit插件实例构造一个标准资源管理器
     *
     * @param plugin Bukkit插件实例
     * @throws IOException 当读取插件Jar文件失败时抛出
     */
    public StandardResourceManager(Plugin plugin) throws IOException {
        this(new JarFile(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()),
             plugin.getDataFolder().toPath());
    }
    
    private StandardResourceManager(JarFile jar, Path pluginFolder) {
        this.jar = jar;
        this.pluginFolder = pluginFolder;
    }
    
    @Override
    public Resource getSelfResource(String path) {
        return this.getSelfResource(this.checkResourcePath(path));
    }
    
    @Override
    public Resource getFileResource(String path) {
        return this.getFileResource(this.checkResourcePath(path));
    }
    
    @Override
    public Map<String, Resource> getFolderResources(String path, Predicate<String> nameFilter, boolean deep) {
        return this.getFolderResources(this.checkResourcePath(path), nameFilter, deep);
    }
    
    protected Resource getSelfResource(Path resPath) {
        ZipEntry entry = this.jar.getEntry(resPath.toString());
        if (entry == null || entry.isDirectory()) return null;
        try {
            return new InputStreamResource(this.jar.getInputStream(entry));
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }
    
    protected Resource getFileResource(Path resPath) {
        resPath = this.pluginFolder.resolve(resPath);
        if (Files.isReadable(resPath) && Files.isRegularFile(resPath, LinkOption.NOFOLLOW_LINKS)) {
            return new FileResource(resPath.toFile());
        }
        return null;
    }
    
    protected Map<String, Resource> getFolderResources(Path dirPath, Predicate<String> nameFilter, boolean deep) {
        dirPath = this.pluginFolder.resolve(dirPath);
        if (!Files.isDirectory(dirPath, LinkOption.NOFOLLOW_LINKS)) return null;
        try {
            Map<String, Resource> allRes = Maps.newLinkedHashMap();
            ResourceFileVisitor visitor = deep ? new DeepFileVisitor(nameFilter) : new NotDeepFileVisitor(nameFilter);
            Files.walkFileTree(dirPath, visitor);
            List<Path> resPaths = visitor.getResourcePaths();
            for (Path resPath : resPaths) {
                allRes.put(this.pluginFolder.relativize(resPath).toString(), new FileResource(resPath.toFile()));
            }
            if (!allRes.isEmpty()) return allRes;
        } catch (IOException e) {
            ExceptionHandler.handle(e);
        }
        return null;
    }
    
    @Override
    public boolean writeResource(String path, InputStream resToWrite, boolean force) {
        Path resPath = this.checkResourcePath(path);
        resPath = this.pluginFolder.resolve(resPath);
        if (!force && Files.exists(resPath, LinkOption.NOFOLLOW_LINKS)) {
            return true;// 资源已存在，且参数force设为不覆盖，直接返回
        }
        try {
            File f = resPath.toFile();
            if (f.exists() || (f.getParentFile().mkdirs() && f.createNewFile())) {
                Files.copy(resToWrite, resPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return false;
        }
    }
    
    /**
     * 检查资源路径合法性并将字符串资源路径转为Path类型
     * <p>
     * 资源路径不能为空，不能尝试切换到资源根目录的父级目录
     *
     * @param path 资源路径
     * @return 转换为Path类型的资源路径
     * @throws IllegalArgumentException 当资源路径不合法时抛出
     */
    protected Path checkResourcePath(String path) {
        Path resPath = resolvePath(path);
        if (resPath == null) throw new IllegalArgumentException("Invalid Path: " + path); // TODO I18n
        return resPath;
    }
    
    private static Path resolvePath(String path) {
        if (Strings.isNullOrEmpty(path)) return null;
        path = path.replace('\\', '/');
        List<String> subs = Lists.newArrayList();
        Splitter.on('/').omitEmptyStrings().split(path).forEach(subs::add);
        if (subs.isEmpty()) return null;
        int size = subs.size();
        Path resPath = size == 1 ? Paths.get(subs.get(0)) : Paths.get(subs.get(0),
                                                                      Arrays.copyOfRange(subs.toArray(new String[size]),
                                                                                         1,
                                                                                         size - 1));
        resPath = resPath.normalize();
        if (resPath.startsWith("..")) {// 禁止切到父级目录
            resPath = resPath.subpath(1, resPath.getNameCount());
        }
        if (resPath.toString().length() == 0) return null;
        return resPath;
    }
    
    /**
     * 设置Jar资源的根路径
     * <p>
     * 资源管理器会在读取Jar资源时自动在最前面添加根路径
     *
     * @param jarRootPath Jar资源的根路径
     */
    public void setJarRootPath(String jarRootPath) {
        this.jarRootPath = !Strings.isNullOrEmpty(jarRootPath) ? jarRootPath : "";
    }
    
    private static abstract class ResourceFileVisitor extends SimpleFileVisitor<Path> {
        
        private static final Predicate<String> DEFAULT_NAME_FILTER = name -> true;
        
        private final Predicate<String> nameFilter;
        private final List<Path> resPaths = Lists.newLinkedList();
        
        protected ResourceFileVisitor(Predicate<String> nameFilter) {
            if (nameFilter != null) {
                this.nameFilter = nameFilter;
            } else {
                this.nameFilter = DEFAULT_NAME_FILTER;
            }
        }
        
        @Override
        public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
            Objects.requireNonNull(filePath);
            Objects.requireNonNull(attrs);
            File file = filePath.toFile();
            if (file.canRead() && this.nameFilter.test(file.getName())) {
                this.resPaths.add(filePath);
            }
            return FileVisitResult.CONTINUE;
        }
        
        public List<Path> getResourcePaths() {
            return this.resPaths;
        }
    }
    
    private static class NotDeepFileVisitor extends ResourceFileVisitor {
        
        protected NotDeepFileVisitor(Predicate<String> nameFilter) {
            super(nameFilter);
        }
        
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.SKIP_SUBTREE;
        }
    }
    
    private static class DeepFileVisitor extends ResourceFileVisitor {
        
        protected DeepFileVisitor(Predicate<String> nameFilter) {
            super(nameFilter);
        }
        
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Objects.requireNonNull(dir);
            Objects.requireNonNull(attrs);
            return FileVisitResult.CONTINUE;
        }
    }
}
