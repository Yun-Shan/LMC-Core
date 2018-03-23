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
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 国际化资源管理器
 * <p>
 * 自动提取属于当前区域({@link Locale})的资源，若当前区域没有相应的资源则提取默认资源
 *
 * @author Yun-Shan
 */
public class I18nResourceManager extends StandardResourceManager {

    private final LocaleManager localeManager;

    private Path[] localeTags = {Paths.get(Locale.ENGLISH.toLanguageTag()), Paths.get(Locale.ENGLISH.getLanguage())};

    /**
     * 通过Bukkit插件实例构造一个国际化资源管理器
     *
     * @param plugin Bukkit插件实例
     * @throws IOException 当读取插件Jar文件失败时抛出
     */
    public I18nResourceManager(LMCPlugin plugin, LocaleManager localeManager) throws IOException {
        super(plugin);
        this.localeManager = localeManager;
        localeManager.addListener((old, locale) -> this.localeTags = new Path[]{
            Paths.get(locale.toLanguageTag()),
            Paths.get(new Locale(locale.getLanguage(), locale.getCountry()).toLanguageTag()),
            Paths.get(locale.getLanguage()),
            /* 空路径为资源未国际化时的默认路径 */
            Paths.get("")
        });
    }

    @Override
    protected Resource getSelfResource(Path resPath) {
        Resource res = null;
        for (Path locale : localeTags) {
            if ((res = super.getSelfResource(locale.resolve(resPath))) != null) {
                break;
            }
        }
        return res;
    }

    @Override
    protected Resource getFileResource(Path resPath) {
        Resource res = null;
        for (Path locale : localeTags) {
            if ((res = super.getFileResource(locale.resolve(resPath))) != null) {
                break;
            }
        }
        return res;
    }

    @Override
    protected Map<String, Resource> getFolderResources(Path dirPath, Predicate<String> nameFilter, boolean deep) {
        Map<String, Resource> res = null;
        for (Path locale : localeTags) {
            if ((res = super.getFolderResources(locale.resolve(dirPath), nameFilter, deep)) != null) {
                break;
            }
        }
        return res;
    }


}
