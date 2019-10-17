/*
 * Author: Yun-Shan
 * Date: 2017/06/16
 */
package org.yunshanmc.lmc.core.locale;

import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.StandardResourceManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 本地化资源管理器
 * <p>
 * 自动提取属于当前区域({@link Locale})的资源，若当前区域没有相应的资源则提取默认资源<br>
 * 注意: {@link #writeResource(String, Resource, boolean)}仍然会按照给定的路径写入，
 * 如需根据当前使用的Locale写入本地化资源请使用{@link #writeI18nResource(String, Resource, boolean)}
 *
 * @author Yun-Shan
 */
public class I18nResourceManager extends StandardResourceManager {

    private final LocaleManager localeManager;

    private Path[] localeTags;

    /**
     * 构造本地化资源管理器
     *
     * @param plugin        LMC插件实例
     * @param localeManager 本地化管理器
     * @throws IOException 当读取插件Jar文件失败时抛出
     */
    public I18nResourceManager(LMCPlugin plugin, LocaleManager localeManager) throws IOException {
        super(plugin);
        this.localeManager = localeManager;
        localeManager.addListener((old, locale) -> {
            List<Locale> list = new ArrayList<>(3);
            // 当前使用的Locale
            list.add(locale);
            // 本机默认的Locale
            if (!list.contains(Locale.getDefault())) {
                list.add(Locale.getDefault());
            }
            // 由于国际化时默认资源都是英文并放在en-US似乎比较好(英文使用范围最广.jpg)，所以以上都没有时使用en-US
            if (!list.contains(Locale.US)) {
                list.add(Locale.US);
            }
            this.localeTags = Stream.concat(
                // 转换为语言标签
                list.stream().map(l -> Paths.get(l.toLanguageTag())),
                // 空路径为尚未国际化的资源路径
                Stream.of(Paths.get(""))
            ).toArray(Path[]::new);
        });
        this.localeManager.setLocale(this.localeManager.getLocale());
    }

    @Override
    protected Resource getSelfResource(Path resPath) {
        Resource res = null;
        for (Path locale : this.localeTags) {
            if ((res = super.getSelfResource(locale.resolve(resPath))) != null) {
                break;
            }
        }
        return res;
    }

    @Override
    protected Resource getFileResource(Path resPath) {
        Resource res = null;
        for (Path locale : this.localeTags) {
            if ((res = super.getFileResource(locale.resolve(resPath))) != null) {
                break;
            }
        }
        return res;
    }

    @Override
    protected Map<String, Resource> getFolderResources(Path dirPath, Predicate<String> nameFilter, boolean deep) {
        Map<String, Resource> res = null;
        for (Path locale : this.localeTags) {
            if ((res = super.getFolderResources(locale.resolve(dirPath), nameFilter, deep)) != null) {
                break;
            }
        }
        return res;
    }

    public boolean writeI18nResource(String path, Resource resource, boolean force) {
        return super.writeResource(this.localeTags[0].resolve(path).toString(), resource, force);
    }
}
