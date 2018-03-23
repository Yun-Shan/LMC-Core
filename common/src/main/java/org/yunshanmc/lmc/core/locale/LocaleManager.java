package org.yunshanmc.lmc.core.locale;

import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * 本地化管理器.
 * <p>
 *
 * @author Yun-Shan
 */
public interface LocaleManager {

    /**
     * 获取当前区域.
     * <p>
     *
     * @return 当前区域
     */
    Locale getLocale();

    /**
     * 设置当前区域.
     * <p>
     *
     * @param locale 当前区域
     */
    void setLocale(Locale locale);

    /**
     * 添加监听器，在当前区域改变时得到通知.
     * <p>
     * 当前区域改变时调用传入的监听器{@link BiConsumer}
     *
     * @param listener 监听器，被调用时将传入旧的区域和新的区域
     *                 ({@link Locale} oldLocale, {@link Locale} newLocale)作为参数
     */
    void addListener(BiConsumer<Locale, Locale> listener);
}
