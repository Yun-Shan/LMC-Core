/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.utils;

import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.URLResource;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 反射相关工具
 */
public final class ReflectUtils {

    private ReflectUtils() {
    }

    /**
     * 根据调用栈追踪指定资源
     * <p>
     * 会通过每个调用栈Class获取指定资源
     *
     * @param stackTrace 调用栈
     * @param resPath    资源路径，会自动转换为根路径(/xxx)
     * @param reverse    由于调用栈是倒序的，该参数指定是否将倒序转为正序，true即转为正序，false即保持倒序
     * @return 追踪到的资源列表
     */
    public static List<Resource> traceResources(StackTraceElement[] stackTrace, String resPath, boolean reverse) {
        resPath = Resource.pathToRoot(resPath);
        List<Resource> ress = new ArrayList<>();
        for (StackTraceElement stack : stackTrace) {
            try {
                Class<?> cls = Class.forName(stack.getClassName());
                URL url = cls.getResource(resPath);
                if (url != null) ress.add(new URLResource(new URL(URLDecoder.decode(url.toString(), "UTF-8"))));
            } catch (ClassNotFoundException e) {
                ExceptionHandler.handle(e);
            } catch (MalformedURLException | UnsupportedEncodingException ignored) {
            }
        }
        if (reverse) Collections.reverse(ress);
        return ress;
    }

    public static void checkSafeCall() {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        try {
            Class<?> beCalled = Class.forName(elements[1].getClassName());
            Class cls = Class.forName(elements[2].getClassName());
            if (// 测试环境(测试环境代码不在jar中)
                !cls.getProtectionDomain().getCodeSource().getLocation().getFile().contains(".jar")
                // 正式环境
                || cls.getProtectionDomain().getCodeSource().equals(beCalled.getProtectionDomain().getCodeSource())
                ) return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new Error("unsafe call");
    }

}
