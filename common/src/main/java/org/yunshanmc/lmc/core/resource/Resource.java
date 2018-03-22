/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.resource;

import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 表示一个资源
 * <p>
 * 通常是文件
 */
public interface Resource {
    
    /**
     * 获取资源的URL，若该资源没有有效的URL则返回null
     *
     * @return 资源的URL
     */
    URL getURL();
    
    /**
     * 获取资源的输入流
     *
     * @return 资源的输入法
     * @throws IOException 打开输入流失败时抛出
     */
    InputStream getInputStream() throws IOException;
    
    static String pathToRoot(String path) {
        if (Strings.isNullOrEmpty(path)) return "/";
        if (path.charAt(0) == '/') return path;
        return '/' + path;
    }

    static File urlToFile(URL url) {
        switch (url.getProtocol()) {
            case "file": return new File(url.getPath());
            case "jar": {
                String path = url.getPath();
                int sep = path.indexOf("!/");
                return new File(path.substring(6 /* file:/ */, sep));
            }
            default: throw new UnsupportedOperationException();
        }
    }
}
