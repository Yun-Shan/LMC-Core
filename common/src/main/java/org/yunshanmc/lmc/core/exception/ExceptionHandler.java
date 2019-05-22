/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.exception;

import com.google.common.base.Strings;
import org.yunshanmc.lmc.core.LMCPlugin;
import org.yunshanmc.lmc.core.internal.BuiltinMessage;
import org.yunshanmc.lmc.core.util.PlatformUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 异常处理器
 *
 * @author Yun-Shan
 */
public final class ExceptionHandler {

    private ExceptionHandler() {
    }// 禁止实例化

    private static final int EXCEPTION_HANDLER_THREAD_COUNT = 3;

    private static final AtomicBoolean STOP_FLAG = new AtomicBoolean(false);
    static {
        for (int i = 0; i < EXCEPTION_HANDLER_THREAD_COUNT; i++) {
            new ExceptionHandlerThread(i + 1, STOP_FLAG).start();
        }
    }

    public static void stop() {
        STOP_FLAG.set(true);
    }


    /**
     * 默认的异常处理器
     */
    public static Consumer<ExceptionInfo> DEFAULT_HANDLER = info -> {
        Throwable err = info.getThrowable();
        String desc = info.getDescription();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(buffer);
        err.printStackTrace(writer);
        writer.flush();
        writer.close();
        String msg = BuiltinMessage.getMessage("DefaultErrorHandler", info.getPlugin(), err.getClass().getName(),
                                               Strings.nullToEmpty(desc), new String(buffer.toByteArray()));

        PlatformUtils.sendRawConsoleMessage(msg);
    };

    private static final Queue<ExceptionInfo> QUEUE = new ConcurrentLinkedQueue<>();

    private static final Map<String, Consumer<ExceptionInfo>> HANDLERS = new HashMap<>();

    public static void setHandler(LMCPlugin plugin, Consumer<ExceptionInfo> handler) {
        Objects.requireNonNull(handler);
        HANDLERS.put(plugin.getName(), handler);
    }

    public static void handle(Throwable t) {
        // 测试环境的错误不能被错误处理器掩盖
        try {
            assert false;
        } catch (AssertionError e) {
            t.printStackTrace();
            assert false;
            return;
        }
        handle(t, t.getMessage());
    }

    public static void handle(Throwable t, String description) {
        QUEUE.offer(new ExceptionInfo(t, description));
    }

    private static class ExceptionHandlerThread extends Thread {

        private final AtomicBoolean stopFlag;

        public ExceptionHandlerThread(int num, AtomicBoolean stopFlag) {
            super("LMC Exception Handler " + num);
            this.stopFlag = stopFlag;
        }

        @Override
        public void run() {
            ExceptionInfo err;
            while (true) {
                err = QUEUE.poll();
                try {
                    if (err == null) {
                        if (this.stopFlag.get()) {
                            break;
                        }
                        Thread.sleep(100);
                        continue;
                    }
                } catch (InterruptedException e) {
                    ExceptionHandler.handle(e);
                    continue;
                }
                if (this.stopFlag.get()) {
                    break;
                }
                Consumer<ExceptionInfo> handler = DEFAULT_HANDLER;
                try {
                    String pluginName = PlatformUtils.traceFirstPluginName(err.getThrowable().getStackTrace(), false);

                    if (pluginName == null) {
                        pluginName = BuiltinMessage.getMessage("InExceptionHandler_ExceptionDescription");
                    }

                    if (this.stopFlag.get()) {
                        break;
                    }
                    err.setPlugin(pluginName);
                    handler = HANDLERS.getOrDefault(pluginName, DEFAULT_HANDLER);
                } catch (Exception e) {
                    DEFAULT_HANDLER.accept(new ExceptionInfo(e, BuiltinMessage.getMessage(
                            "InExceptionHandler_ExceptionDescription")));
                }

                if (this.stopFlag.get()) {
                    break;
                }
                handler.accept(err);
            }
        }
    }

    public static class ExceptionInfo {

        private Throwable throwable;
        private String description;
        /**
         * 出现异常的插件(该变量会在异常处理线程中被设置)
         */
        private String plugin;

        public ExceptionInfo(Throwable err, String description) {
            this.throwable = err;
            this.description = description;
        }

        public void setPlugin(String plugin) {
            this.plugin = plugin;
        }

        public String getPlugin() {
            return plugin;
        }

        public Throwable getThrowable() {
            return this.throwable;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
