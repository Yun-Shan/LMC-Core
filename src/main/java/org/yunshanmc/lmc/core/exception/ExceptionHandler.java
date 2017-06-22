/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.exception;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.utils.ReflectUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

/**
 * 异常处理器
 */
public final class ExceptionHandler {
    
    private ExceptionHandler() {}// 禁止实例化
    
    static {
        new ExceptionHandlerThread().start();
    }
    
    /**
     * 默认的异常处理器 //TODO 实现默认异常处理器
     */
    private static BiConsumer<Throwable, String> DEFAULT_HANDLER;
    
    private static final Queue<ExceptionInfo> QUEUE = new ConcurrentLinkedQueue<>();
    
    private static final Map<String, BiConsumer<Throwable, String>> HANDLERS = new HashMap<>();
    
    public static void setHandler(Plugin plugin, BiConsumer<Throwable, String> handler) {
         HANDLERS.put(plugin.getName(), handler);
    }
    
    public static void handle(Throwable t) {
        handle(t, null);
    }
    
    public static void handle(Throwable t, String description) {
        QUEUE.offer(new ExceptionInfo(t, description));
    }
    
    private static class ExceptionHandlerThread extends Thread {
        
        public ExceptionHandlerThread() {
            super("LMC Exception Handler");
        }
    
        @Override
        public void run() {
            ExceptionInfo err;
            while ((err = QUEUE.poll()) != null) {
                List<Resource> resList = ReflectUtils.traceResources(err.getThrowable().getStackTrace(), "plugin.yml");
                BiConsumer<Throwable, String> handler = DEFAULT_HANDLER;
                
                if (!resList.isEmpty()) {
                    try {
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(new InputStreamReader(resList.get(0).getInputStream(),
                                                                                  StandardCharsets.UTF_8));
                        handler = HANDLERS.get(yml.getString("name"));
                    } catch (IOException e) {
                        ExceptionHandler.handle(e);
                    }
                }
                
                handler.accept(err.getThrowable(), err.getDescription());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ExceptionHandler.handle(e);
            }
        }
    }
    
    private static class ExceptionInfo {
        
        private Throwable throwable;
        private String description;
        
        public ExceptionInfo(Throwable err, String description) {
            this.throwable = err;
            this.description = description;
        }
        
        public Throwable getThrowable() {
            return this.throwable;
        }
        
        public String getDescription() {
            return this.description;
        }
    }
}
