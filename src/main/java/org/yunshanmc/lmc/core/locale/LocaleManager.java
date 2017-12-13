/*
 * Author: Yun-Shan
 * Date: 2017/06/16
 */
package org.yunshanmc.lmc.core.locale;

import org.yunshanmc.lmc.core.internal.BuiltinMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * 国际化管理器
 *
 * 可设置当前所使用的区域({@link Locale})
 */
public class LocaleManager {
    
    private Locale current;
    
    private final List<Consumer<Locale>> listeners = new ArrayList<>();
    
    public LocaleManager() {
        this.setLocale(Locale.getDefault());
    }
    
    public Locale getLocale() {
        return this.current;
    }
    
    public void setLocale(Locale locale) {
        this.current = locale;
        BuiltinMessage.setLocale(locale);
        this.listeners.forEach(listener -> listener.accept(locale));
    }
    
    /**
     * 添加监听器，在当前区域改变时得到通知
     *
     * 当前区域改变时调用传入的监听器{@link Consumer}
     *
     * @param listener 监听器，被调用时将传入新的区域({@link Locale})作为参数
     */
    public void addListener(Consumer<Locale> listener) {
        this.listeners.add(listener);
    }
}
