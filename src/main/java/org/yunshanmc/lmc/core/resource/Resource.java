/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.resource;

import javax.annotation.Nullable;
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
    @Nullable
    URL getURL();
    
    /**
     * 获取资源的输入流
     *
     * @return 资源的输入法
     * @throws IOException 打开输入流失败时抛出
     */
    InputStream getInputStream() throws IOException;
    
    
}
