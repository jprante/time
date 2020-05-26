package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Locale;

public class PrettyTimeI18n_DA_Test {

    private Locale defaultLocale;

    private Locale locale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        locale = new Locale("da");
        Locale.setDefault(locale);
    }

    @Test
    public void testPrettyTime() {
        PrettyTime p = new PrettyTime(locale);
        assertEquals("straks", p.format(LocalDateTime.now()));
    }

    @Test
    public void testPrettyTimeCenturies() {
        PrettyTime p = new PrettyTime(3155692597470L * 3L, locale);
        assertEquals("3 århundreder siden", p.format(0));

        p = new PrettyTime(0, locale);
        assertEquals("3 århundreder fra nu", p.format(3155692597470L * 3L));
    }

    @Test
    public void testCeilingInterval() {
        LocalDateTime then = LocalDateTime.of(2009, 5, 20, 0, 0);
        LocalDateTime ref = LocalDateTime.of(2009, 6, 17, 0, 0);
        PrettyTime t = new PrettyTime(ref, locale);
        assertEquals("1 måned siden", t.format(then));
    }

    @Test
    public void testRightNow() {
        PrettyTime t = new PrettyTime(locale);
        assertEquals("straks", t.format(LocalDateTime.now()));
    }

    @Test
    public void testRightNowVariance() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("straks", t.format(600));
    }

    @Test
    public void testMinutesFromNow() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("om 12 minutter", t.format(1000 * 60 * 12));
    }

    @Test
    public void testHoursFromNow() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("om 3 timer", t.format(1000 * 60 * 60 * 3));
    }

    @Test
    public void testDaysFromNow() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("om 3 dage", t.format(1000 * 60 * 60 * 24 * 3));
    }

    @Test
    public void testWeeksFromNow() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("om 3 uger", t.format(1000 * 60 * 60 * 24 * 7 * 3));
    }

    @Test
    public void testMonthsFromNow() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("om 3 måneder", t.format(2629743830L * 3L));
    }

    @Test
    public void testYearsFromNow() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("om 3 år", t.format(2629743830L * 12L * 3L));
    }

    @Test
    public void testDecadesFromNow() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("3 årtier fra nu", t.format(315569259747L * 3L));
    }

    @Test
    public void testCenturiesFromNow() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("3 århundreder fra nu", t.format(3155692597470L * 3L));
    }

    @Test
    public void testMomentsAgo() {
        PrettyTime t = new PrettyTime(6000, locale);
        assertEquals("et øjeblik siden", t.format(0));
    }

    @Test
    public void testMinutesAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 12), locale);
        assertEquals("12 minutter siden", t.format(0));
    }

    @Test
    public void testHoursAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 3), locale);
        assertEquals("3 timer siden", t.format(0));
    }

    @Test
    public void testDaysAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3), locale);
        assertEquals("3 dage siden", t.format(0));
    }

    @Test
    public void testWeeksAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7 * 3), locale);
        assertEquals("3 uger siden", t.format(0));
    }

    @Test
    public void testMonthsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 3L), locale);
        assertEquals("3 måneder siden", t.format(0));
    }

    @Test
    public void testYearsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 12L * 3L), locale);
        assertEquals("3 år siden", t.format(0));
    }

    @Test
    public void testDecadesAgo() {
        PrettyTime t = new PrettyTime((315569259747L * 3L), locale);
        assertEquals("3 årtier siden", t.format(0));
    }

    @Test
    public void testCenturiesAgo() {
        PrettyTime t = new PrettyTime((3155692597470L * 3L), locale);
        assertEquals("3 århundreder siden", t.format(0));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
