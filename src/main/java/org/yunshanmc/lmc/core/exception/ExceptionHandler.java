/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.exception;

import org.bukkit.configuration.file.YamlConfiguration;
import org.yunshanmc.lmc.core.resource.Resource;
import org.yunshanmc.lmc.core.utils.ReflectUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

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
    private static Consumer<Throwable> DEFAULT_HANDLER;
    
    private static final Queue<Throwable> QUEUE = new ConcurrentLinkedQueue<>();
    
    private static final Map<String, Consumer<Throwable>> HANDLERS = new HashMap<>();
    
    public static void handle(Throwable t) {
        QUEUE.offer(t);
    }
    
    private static class ExceptionHandlerThread extends Thread {
        
        public ExceptionHandlerThread() {
            super("LMC Exception Handler");
        }
    
        @Override
        public void run() {
            Throwable t;
            while ((t = QUEUE.poll()) != null) {
                List<Resource> ress = ReflectUtils.traceResources(t.getStackTrace(), "plugin.yml");
                Consumer<Throwable> handler = DEFAULT_HANDLER;
                
                if (!ress.isEmpty()) {
                    try {
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(new InputStreamReader(ress.get(0).getInputStream(),
                                                                                  StandardCharsets.UTF_8));
                        handler = HANDLERS.get(yml.getString("name"));
                    } catch (IOException e) {
                        ExceptionHandler.handle(e);
                    }
                }
                
                handler.accept(t);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ExceptionHandler.handle(e);
            }
        }
    }
}
