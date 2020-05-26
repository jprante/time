package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class PrettyTimeI18n_RU_Test {

    private Locale defaultLocale;

    private Locale locale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        locale = new Locale("ru");
        Locale.setDefault(locale);
    }

    @Test
    public void testPrettyTime() {
        PrettyTime p = new PrettyTime(locale);
        assertEquals("сейчас", p.format(LocalDateTime.now()));
    }

    @Test
    public void testPrettyTimeCenturies() {
        PrettyTime p = new PrettyTime((3155692597470L * 3L), locale);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        assertEquals("3 века назад", p.format(localDateTime));

        p = new PrettyTime((0), locale);
        localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(3155692597470L * 3L), ZoneId.systemDefault());
        assertEquals("через 3 века", p.format(localDateTime));
    }

    @Test
    public void testCeilingInterval() {
        LocalDateTime then = LocalDateTime.of(2009, 5, 20, 0, 0);
        LocalDateTime ref = LocalDateTime.of(2009, 6, 17, 0, 0);
        PrettyTime t = new PrettyTime(ref, locale);
        assertEquals("1 месяц назад", t.format(then));
    }

    @Test
    public void testRightNow() {
        PrettyTime t = new PrettyTime(locale);
        assertEquals("сейчас", t.format(LocalDateTime.now()));
    }

    @Test
    public void testRightNowVariance() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("сейчас", t.format((600)));
    }

    @Test
    public void testMinutesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 12 минут", t.format((1000 * 60 * 12)));
    }

    @Test
    public void testHoursFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 часа", t.format((1000 * 60 * 60 * 3)));
    }

    @Test
    public void testDaysFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 дня", t.format((1000 * 60 * 60 * 24 * 3)));
    }

    @Test
    public void testWeeksFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 недели", t.format((1000 * 60 * 60 * 24 * 7 * 3)));
    }

    @Test
    public void testMonthsFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 месяца", t.format((2629743830L * 3L)));
    }

    @Test
    public void testYearsFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 года", t.format((2629743830L * 12L * 3L)));
    }

    @Test
    public void testDecadesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 десятилетия", t.format((315569259747L * 3L)));
    }

    @Test
    public void testCenturiesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("через 3 века", t.format((3155692597470L * 3L)));
    }

    /*
     * Past
     */
    @Test
    public void testMomentsAgo() {
        PrettyTime t = new PrettyTime((6000), locale);
        assertEquals("только что", t.format((0)));
    }

    @Test
    public void testMinutesAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 12), locale);
        assertEquals("12 минут назад", t.format((0)));
    }

    @Test
    public void testHoursAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 3), locale);
        assertEquals("3 часа назад", t.format((0)));
    }

    @Test
    public void testDaysAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3), locale);
        assertEquals("3 дня назад", t.format((0)));
    }

    @Test
    public void testWeeksAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7 * 3), locale);
        assertEquals("3 недели назад", t.format((0)));
    }

    @Test
    public void testMonthsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 3L), locale);
        assertEquals("3 месяца назад", t.format((0)));
    }

    @Test
    public void testYearsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 12L * 3L), locale);
        assertEquals("3 года назад", t.format((0)));
    }

    @Test
    public void testDecadesAgo() {
        PrettyTime t = new PrettyTime((315569259747L * 3L), locale);
        assertEquals("3 десятилетия назад", t.format((0)));
    }

    @Test
    public void testCenturiesAgo() {
        PrettyTime t = new PrettyTime((3155692597470L * 3L), locale);
        assertEquals("3 века назад", t.format((0)));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
