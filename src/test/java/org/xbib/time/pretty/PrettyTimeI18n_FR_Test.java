package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class PrettyTimeI18n_FR_Test {

    private Locale defaultLocale;

    private Locale locale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        locale = Locale.FRENCH;
        Locale.setDefault(locale);
    }

    @Test
    public void testPrettyTimeFRENCH() {
        // The FRENCH resource bundle should be used
        PrettyTime p = new PrettyTime(locale);
        assertEquals("à l'instant", p.format(LocalDateTime.now()));
    }

    @Test
    public void testPrettyTimeFRENCHCenturies() {
        PrettyTime p = new PrettyTime((3155692597470L * 3L), locale);
        assertEquals(p.format(0), "il y a 3 siècles");
    }

    @Test
    public void testPrettyTimeViaDefaultLocaleFRENCH() {
        PrettyTime p = new PrettyTime();
        assertEquals(p.format(LocalDateTime.now()), "à l'instant");
    }

    @Test
    public void testPrettyTimeFRENCHLocale() {
        long t = 1L;
        PrettyTime p = new PrettyTime((0), locale);
        while (1000L * 60L * 60L * 24L * 365L * 1000000L > t) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
            assertTrue(p.format(localDateTime).startsWith("dans") || p.format(localDateTime).startsWith("à l'instant"));
            t *= 2L;
        }
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
