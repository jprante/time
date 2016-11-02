package org.xbib.time.pretty;

import java.util.Locale;

/**
 * An object that behaves differently for various {@link Locale} settings.
 *
 * @param <T> parameter type
 */
public interface LocaleAware<T> {
    /**
     * Set the {@link Locale} for which this instance should behave in.
     * @param locale locale
     * @return the type
     */
    T setLocale(Locale locale);

}
