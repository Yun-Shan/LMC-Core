/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.exception;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yunshanmc.lmc.core.internal.BuiltinMessage;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.utils.ReflectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * 异常处理器
 */
public final class ExceptionHandler {

    private ExceptionHandler() {
    }// 禁止实例化

    static {
        for (int i = 0; i < 3; i++) {
            new ExceptionHandlerThread(i + 1).start();
        }
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

        Bukkit.getConsoleSender().sendMessage(
                BuiltinMessage.getMessage("DefaultErrorHandler",
                                          info.getPlugin(),
                                          err.getClass().getName(),
                                          desc != null ? desc : (err.getMessage() != null ? err.getMessage() : "无"),
                                          new String(buffer.toByteArray())
                )
        );
    };

    private static final Queue<ExceptionInfo> QUEUE = new ConcurrentLinkedQueue<>();

    private static final Map<String, Consumer<ExceptionInfo>> HANDLERS = new HashMap<>();

    public static void setHandler(Plugin plugin, Consumer<ExceptionInfo> handler) {
        HANDLERS.put(plugin.getName(), handler);
    }

    public static void handle(Throwable t) {
        handle(t, null);
    }

    public static void handle(Throwable t, String description) {
        QUEUE.offer(new ExceptionInfo(t, description));
    }

    private static class ExceptionHandlerThread extends Thread {

        public ExceptionHandlerThread(int num) {
            super("LMC Exception Handler " + num);
        }

        @Override
        public void run() {
            ExceptionInfo err;
            while ((err = QUEUE.poll()) != null) {
                List<Resource> resList = ReflectUtils.traceResources(err.getThrowable().getStackTrace(), "plugin.yml");
                Consumer<ExceptionInfo> handler = DEFAULT_HANDLER;

                if (!resList.isEmpty()) {
                    try {
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                                new InputStreamReader(resList.get(0).getInputStream(),
                                                      StandardCharsets.UTF_8));
                        String plugin = yml.getString("name");
                        err.setPlugin(plugin);
                        handler = HANDLERS.get(plugin);
                    } catch (IOException e) {
                        ExceptionHandler.handle(e);
                    }
                }

                handler.accept(err);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    public static class ExceptionInfo {

        private Throwable throwable;
        private String description;
        private String plugin;// 出现异常的插件，该变量会在异常处理线程中被设置

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
