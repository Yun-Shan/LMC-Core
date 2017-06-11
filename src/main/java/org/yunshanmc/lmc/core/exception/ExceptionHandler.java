/*
 * Author: Yun-Shan
 * Date: 2017/06/11
 */
package org.yunshanmc.lmc.core.exception;

import org.yunshanmc.lmc.core.utils.ReflectUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * 异常处理器
 */
public final class ExceptionHandler {
    
    private ExceptionHandler() {}// 禁止实例化
    
    static {
    
    }
    
    private static final Queue<Throwable> QUEUE = new ConcurrentLinkedQueue<>();
    
    private static final Map<String, Consumer<Throwable>> HANDLERS = new HashMap<>();
    
    public static void handle(Throwable t) {
      QUEUE.offer(t);
    }
    
    private class ExceptionHandlerThread extends Thread {
        public ExceptionHandlerThread() {
            super("LMC Exception Handler");
        }
    
        @Override
        public void run() {
            Throwable t;
            while ((t = QUEUE.poll()) != null) {
                ReflectUtils.traceResources(t.getStackTrace()).forEach(res -> {
                
                });
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ExceptionHandler.handle(e);
            }
        }
    }
}
