package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class PrettyTimeI18n_UA_Test {

    private Locale defaultLocale;

    private Locale locale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        locale = new Locale("ua");
        Locale.setDefault(locale);
    }

    @Test
    public void testPrettyTime() {
        PrettyTime p = new PrettyTime(locale);
        assertEquals("зараз", p.format(LocalDateTime.now()));
    }

    @Test
    public void testPrettyTimeCenturies() {
        PrettyTime p = new PrettyTime((3155692597470L * 3L), locale);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        assertEquals("3 століття тому", p.format(localDateTime));

        p = new PrettyTime((0), locale);
        localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(3155692597470L * 3L), ZoneId.systemDefault());
        assertEquals("через 3 століття", p.format(localDateTime));
    }

    @Test
    public void testCeilingInterval() {
        LocalDateTime then = LocalDateTime.of(2009, 5, 20, 0, 0);
        LocalDateTime ref = LocalDateTime.of(2009, 6, 17, 0, 0);
        PrettyTime t = new PrettyTime(ref, locale);
        assertEquals("1 місяць тому", t.format(then));
    }

    @Test
    public void testRightNow() {
        PrettyTime t = new PrettyTime(locale);
        assertEquals("зараз", t.format(LocalDateTime.now()));
    }

    @Test
    public void testRightNowVariance() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("зараз", t.format(600));
    }

    @Test
    public void testMinutesFromNow() {
        PrettyTime t = new PrettyTime(0, locale);
        assertEquals("через 12 хвилин", t.format((1000 * 60 * 12)));
    }

    @Test
    public void testHoursFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 години", t.format((1000 * 60 * 60 * 3)));
    }

    @Test
    public void testDaysFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 дні", t.format((1000 * 60 * 60 * 24 * 3)));
    }

    @Test
    public void testWeeksFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 тижні", t.format((1000 * 60 * 60 * 24 * 7 * 3)));
    }

    @Test
    public void testMonthsFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 місяці", t.format((2629743830L * 3L)));
    }

    @Test
    public void testYearsFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 роки", t.format((2629743830L * 12L * 3L)));
    }

    @Test
    public void testDecadesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 десятиліття", t.format((315569259747L * 3L)));
    }

    @Test
    public void testCenturiesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 століття", t.format((3155692597470L * 3L)));
    }

    /*
     * Past
     */
    @Test
    public void testMomentsAgo() {
        PrettyTime t = new PrettyTime((6000), locale);
        assertEquals("тільки що", t.format((0)));
    }

    @Test
    public void testMinutesAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 12), locale);
        assertEquals("12 хвилин тому", t.format((0)));
    }

    @Test
    public void test1HourAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60), locale);
        assertEquals("1 годину тому", t.format((0)));
    }

    @Test
    public void test3HoursAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 3), locale);
        assertEquals("3 години тому", t.format((0)));
    }

    @Test
    public void test6HoursAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 6), locale);
        assertEquals("6 годин тому", t.format((0)));
    }

    @Test
    public void testDaysAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3), locale);
        assertEquals("3 дні тому", t.format((0)));
    }

    @Test
    public void testWeeksAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7 * 3), locale);
        assertEquals("3 тижні тому", t.format((0)));
    }

    @Test
    public void testMonthsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 3L), locale);
        assertEquals("3 місяці тому", t.format((0)));
    }

    @Test
    public void testYearsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 12L * 3L), locale);
        assertEquals("3 роки тому", t.format((0)));
    }

    @Test
    public void test8YearsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 12L * 8L), locale);
        assertEquals("8 років тому", t.format((0)));
    }

    @Test
    public void testDecadesAgo() {
        PrettyTime t = new PrettyTime((315569259747L * 3L), locale);
        assertEquals("3 десятиліття тому", t.format((0)));
    }

    @Test
    public void test8DecadesAgo() {
        PrettyTime t = new PrettyTime((315569259747L * 8L), locale);
        assertEquals("8 десятиліть тому", t.format((0)));
    }

    @Test
    public void testCenturiesAgo() {
        PrettyTime t = new PrettyTime((3155692597470L * 3L), locale);
        assertEquals("3 століття тому", t.format((0)));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
