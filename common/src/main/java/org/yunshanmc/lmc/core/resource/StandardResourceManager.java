/*
 * Author: Yun-Shan
 * Date: 2017/06/14
 */
package org.yunshanmc.lmc.core.resource;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 标准资源管理器.
 * <p>
 *
 * @author Yun-Shan
 */
public class StandardResourceManager implements ResourceManager {

    private File jarFile;
    private FileSystem jarFileSystem;
    private Path jarRoot;

    private final Path pluginFolder;

    /**
     * 通过插件实例构造一个标准资源管理器
     *
     * @param plugin Bukkit插件实例
     * @throws IOException 当读取插件Jar文件失败时抛出
     */
    public StandardResourceManager(LMCPlugin plugin) throws IOException {
        this(new File(URLDecoder.decode(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile(),
            "UTF-8")),
            plugin.getDataFolder().toPath());
    }

    private StandardResourceManager(File jarFile, Path pluginFolder) throws IOException {
        this.pluginFolder = pluginFolder;

        this.jarFile = jarFile;
        this.jarFileSystem = FileSystems.newFileSystem(Paths.get(this.jarFile.toURI()), null);
        this.setJarRootPath("/");
    }

    @Override
    public void updateJar() throws IOException {
        this.jarFileSystem = FileSystems.newFileSystem(Paths.get(this.jarFile.toURI()), null);
        this.setJarRootPath(this.jarRoot.toString());
    }

    @Override
    public Resource getSelfResource(String path) {
        Path resPath = this.checkResourcePath(path, true, true);
        resPath = this.jarRoot.resolve(resPath);
        return this.getSelfResource(resPath);
    }

    @Override
    public Map<String, Resource> getSelfResources(String path, Predicate<String> nameFilter, boolean deep) {
        Path resPath = this.checkResourcePath(path, false, true);
        resPath = this.jarRoot.resolve(resPath);
        return this.getSelfResources(resPath, nameFilter, deep);
    }

    @Override
    public Resource getFolderResource(String path) {
        return this.getFileResource(this.checkResourcePath(path, true, false));
    }

    @Override
    public Map<String, Resource> getFolderResources(String path, Predicate<String> nameFilter, boolean deep) {
        return this.getFolderResources(this.checkResourcePath(path, false, false), nameFilter, deep);
    }

    protected Resource getSelfResource(Path resPath) {
        if (Files.notExists(resPath) || Files.isDirectory(resPath)) {
            return null;
        }
        try {
            return new InputStreamResource(Files.newInputStream(resPath));
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }

    protected Map<String, Resource> getSelfResources(Path dirPath, Predicate<String> nameFilter, boolean deep) {
        if (!Files.isDirectory(dirPath, LinkOption.NOFOLLOW_LINKS)) {
            return null;
        }
        try {
            Map<String, Resource> allRes = Maps.newLinkedHashMap();
            BaseResourceFileVisitor visitor = deep ? new DeepFileVisitor(nameFilter) : new NotDeepFileVisitor(nameFilter);
            Files.walkFileTree(dirPath, visitor);
            List<Path> resPaths = visitor.getResourcePaths();
            for (Path resPath : resPaths) {
                allRes.put(resPath.toString().substring(1), new InputStreamResource(Files.newInputStream(resPath)));
            }
            if (!allRes.isEmpty()) {
                return allRes;
            }
        } catch (IOException e) {
            ExceptionHandler.handle(e);
        }
        return null;
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
        if (!Files.isDirectory(dirPath, LinkOption.NOFOLLOW_LINKS)) {
            return null;
        }
        try {
            Map<String, Resource> allRes = Maps.newLinkedHashMap();
            BaseResourceFileVisitor visitor = deep ? new DeepFileVisitor(nameFilter) : new NotDeepFileVisitor(nameFilter);
            Files.walkFileTree(dirPath, visitor);
            List<Path> resPaths = visitor.getResourcePaths();
            for (Path resPath : resPaths) {
                allRes.put(this.pluginFolder.relativize(resPath).toString(), new FileResource(resPath.toFile()));
            }
            if (!allRes.isEmpty()) {
                return allRes;
            }
        } catch (IOException e) {
            ExceptionHandler.handle(e);
        }
        return null;
    }

    @Override
    public boolean writeResource(String path, Resource resource, boolean force) {
        Path resPath = this.checkResourcePath(path, true, false);
        resPath = this.pluginFolder.resolve(resPath);
        if (!force && Files.exists(resPath, LinkOption.NOFOLLOW_LINKS)) {
            // 资源已存在，且参数force设为不覆盖，直接返回
            return true;
        }
        try {
            File f = resPath.toFile();
            boolean validFile = f.exists();
            if (!validFile) {
                // 文件不存在时尝试创建文件
                boolean createParent = f.getParentFile().exists() || f.getParentFile().mkdirs();
                if (createParent && f.createNewFile()) {
                    validFile = true;
                }
            }

            if (validFile) {
                InputStream stream = resource.getInputStream();
                if (stream == null) {
                    return false;
                }
                Files.copy(stream, resPath, StandardCopyOption.REPLACE_EXISTING);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            ExceptionHandler.handle(e);
            return false;
        }
    }

    @Override
    public boolean saveDefaultResource(String selfPath, String dirPath, boolean force) {
        Resource res = this.getSelfResource(selfPath);
        return res != null && this.writeResource(dirPath, res, force);
    }

    /**
     * 检查资源路径合法性并将字符串资源路径转为Path类型
     * <p>
     * 不能尝试切换到资源根目录的父级目录
     *
     * @param path   资源路径
     * @param isFile 资源是文件还是目录
     * @param isJar  是否为自身Jar的路径，该方法会根据此参数选择相应的文件管理器
     * @return 转换为Path类型的资源路径
     * @throws IllegalArgumentException 当资源路径不合法时抛出
     */
    protected Path checkResourcePath(String path, boolean isFile, boolean isJar) {
        return resolvePath(path, isFile, isJar ? this.jarRoot.getFileSystem() : this.pluginFolder.getFileSystem());
    }

    private static final String PARENT_DIR_NAME = "..";

    private static Path resolvePath(String path, boolean isFile, FileSystem fs) {
        if (Strings.isNullOrEmpty(path)) {
            throw new IllegalArgumentException("Invalid Path: (null or empty)");
        }
        path = path.replace('\\', '/');
        List<String> subs = Lists.newArrayList();
        Splitter.on('/').omitEmptyStrings().split(path).forEach(subs::add);
        if (subs.isEmpty()) {
            return fs.getPath("/");
        }
        int size = subs.size();
        Path resPath = size == 1 ? fs.getPath(subs.get(0)) : fs.getPath(subs.get(0),
            Arrays.copyOfRange(
                subs.toArray(new String[size]),
                1,
                size));
        resPath = resPath.normalize();
        // 禁止切到父级目录
        if (resPath.startsWith(PARENT_DIR_NAME)) {
            throw new IllegalArgumentException("Invalid Path: " + path);
        }
        // 确认是文件还是目录
        if (isFile && Files.isDirectory(resPath)) {
            throw new IllegalArgumentException("Invalid Path: " + path + " (only allow file, but there is a directory)");
        }
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
        this.jarRoot = this.jarFileSystem.getPath(Strings.nullToEmpty(jarRootPath)).normalize();
    }

    private static abstract class BaseResourceFileVisitor extends SimpleFileVisitor<Path> {

        private static final Predicate<String> DEFAULT_NAME_FILTER = name -> true;

        private final Predicate<String> nameFilter;
        private final List<Path> resPaths = Lists.newLinkedList();

        protected BaseResourceFileVisitor(Predicate<String> nameFilter) {
            if (nameFilter != null) {
                this.nameFilter = nameFilter;
            } else {
                this.nameFilter = DEFAULT_NAME_FILTER;
            }
        }

        @Override
        public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) {
            Objects.requireNonNull(filePath);
            Objects.requireNonNull(attrs);
            if (Files.isRegularFile(filePath, LinkOption.NOFOLLOW_LINKS) && Files.isReadable(filePath) && this.nameFilter.test(filePath.getFileName().toString())) {
                this.resPaths.add(filePath);
            }
            return FileVisitResult.CONTINUE;
        }

        public List<Path> getResourcePaths() {
            return this.resPaths;
        }
    }

    private static class NotDeepFileVisitor extends BaseResourceFileVisitor {

        protected NotDeepFileVisitor(Predicate<String> nameFilter) {
            super(nameFilter);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            return FileVisitResult.SKIP_SUBTREE;
        }
    }

    private static class DeepFileVisitor extends BaseResourceFileVisitor {

        protected DeepFileVisitor(Predicate<String> nameFilter) {
            super(nameFilter);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            Objects.requireNonNull(dir);
            Objects.requireNonNull(attrs);
            return FileVisitResult.CONTINUE;
        }
    }
}
