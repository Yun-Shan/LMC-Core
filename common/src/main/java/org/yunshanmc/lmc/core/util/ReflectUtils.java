/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.util;

import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.resource.URLResource;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 反射相关工具.
 * <p>
 *
 * @author Yun-Shan
 */
public final class ReflectUtils {

    private ReflectUtils() {
        // 禁止实例化
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
                if (url != null) {
                    ress.add(new URLResource(new URL(URLDecoder.decode(url.toString(), "UTF-8"))));
                }
            } catch (ClassNotFoundException e) {
                ExceptionHandler.handle(e);
            } catch (MalformedURLException | UnsupportedEncodingException ignored) {
            }
        }
        if (reverse) {
            Collections.reverse(ress);
        }
        return ress;
    }

    public static StackTraceElement[] captureStackTrace() {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        return Arrays.copyOfRange(stack, 1, stack.length);
    }

    public static void checkSafeCall() {
        // 测试环境不进行安全检查
        if (isInTest()) {
            return;
        }

        // 正式环境
        StackTraceElement[] elements = new Throwable().getStackTrace();

        //noinspection ConstantConditions
        do {
            try {
                Class<?> beCalled = Class.forName(elements[1].getClassName());
                Class cls = Class.forName(elements[2].getClassName());
                // 使用特殊方法(如JVMTI)加载的类没有ProtectionDomain或CodeSource
                if (cls.getProtectionDomain() == null || cls.getProtectionDomain().getCodeSource() == null) {
                    break;
                }

                CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
                if (codeSource.equals(beCalled.getProtectionDomain().getCodeSource())) {
                    return;
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
                break;
            }
        } while (false);
        throw new Error("unsafe call");
    }

    public static boolean isInTest() {
        // 为了安全起见 不使用PlatformUtils.isInTest判断平台而是直接判断
        try {
            assert false;
            return false;
        } catch (AssertionError e) {
            return true;
        }
    }

}
