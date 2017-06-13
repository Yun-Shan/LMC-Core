/*
 * Author: Yun-Shan
 * Date: 2017/06/13
 */
package org.yunshanmc.lmc.core.resource;

import com.google.common.base.Strings;

/**
 * 路径工具类
 */
public final class Path {
    
    private Path() {}// 禁止实例化
    
    public static String toRoot(String path) {
        if (Strings.isNullOrEmpty(path)) return "/";
        if (path.charAt(0) == '/') return path;
        return '/' + path;
    }
}
