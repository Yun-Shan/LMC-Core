/*
 * Author: Yun-Shan
 * Date: 2017/06/16
 */
package org.yunshanmc.lmc.core.locale;

import org.yunshanmc.lmc.core.internal.BuiltinMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * 国际化管理器.
 * <p>
 * 可设置当前所使用的区域({@link Locale})
 *
 * @author Yun-Shan
 */
public class DefaultLocaleManager implements LocaleManager {
    
    private Locale current;
    
    private final List<BiConsumer<Locale, Locale>> listeners = new ArrayList<>();
    
    public DefaultLocaleManager() {
        this.setLocale(Locale.getDefault());
    }
    
    @Override
    public Locale getLocale() {
        return this.current;
    }
    
    @Override
    public void setLocale(Locale locale) {
        Locale oldLocale = this.current;
        this.current = locale;
        BuiltinMessage.setLocale(locale);
        this.listeners.forEach(listener -> listener.accept(oldLocale, locale));
    }
    

    @Override
    public void addListener(BiConsumer<Locale, Locale> listener) {
        this.listeners.add(listener);
    }
}
