package org.xbib.time.pretty.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.xbib.time.pretty.PrettyTime;
import org.xbib.time.pretty.TimeFormatProvider;

import java.util.Locale;
import java.util.ResourceBundle;

public class TimeFormatProviderTest {

    @Test
    public void test() {
        Locale defaultLocale = Locale.getDefault();
        Locale locale = new Locale("xx");
        Locale.setDefault(locale);
        ResourceBundle bundle = ResourceBundle.getBundle(Resources.class.getName(), locale);
        assertTrue(bundle instanceof TimeFormatProvider);
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void testFormatFromDirectFormatOverride() throws Exception {
        Locale defaultLocale = Locale.getDefault();
        Locale locale = new Locale("xx");
        Locale.setDefault(locale);
        PrettyTime prettyTime = new PrettyTime(locale);
        String result = prettyTime.format(System.currentTimeMillis() + 1000 * 60 * 6);
        assertEquals("6 minutes from now", result);
        Locale.setDefault(defaultLocale);
    }

}
