package org.yunshanmc.lmc.core.locale;

import java.util.Locale;
import java.util.function.Consumer;

public interface LocaleManager {
    Locale getLocale();

    void setLocale(Locale locale);

    void addListener(Consumer<Locale> listener);
}
