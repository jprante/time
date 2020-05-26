package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class PrettyTimeI18n_SV_Test {

    private Locale defaultLocale;

    private Locale locale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        locale = new Locale("sv");
        Locale.setDefault(locale);
    }

    @Test
    public void testPrettyTime() {
        PrettyTime p = new PrettyTime(locale);
        assertEquals("om en stund", p.format(LocalDateTime.now()));
    }

    @Test
    public void testPrettyTimeCenturies() {
        PrettyTime p = new PrettyTime((3155692597470L * 3L), locale);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        assertEquals("3 århundraden sedan", p.format(localDateTime));

        p = new PrettyTime((0), locale);
        localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(3155692597470L * 3L), ZoneId.systemDefault());
        assertEquals("om 3 århundraden", p.format(localDateTime));
    }

    @Test
    public void testCeilingInterval() {
        LocalDateTime then = LocalDateTime.of(2009, 5, 20, 0, 0);
        LocalDateTime ref = LocalDateTime.of(2009, 6, 17, 0, 0);

        PrettyTime t = new PrettyTime(ref, locale);
        assertEquals("1 månad sedan", t.format(then));
    }

    @Test
    public void testRightNow() {
        PrettyTime t = new PrettyTime(locale);
        assertEquals("om en stund", t.format(LocalDateTime.now()));
    }

    @Test
    public void testRightNowVariance() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("om en stund", t.format((600)));
    }

    @Test
    public void testMinutesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("om 12 minuter", t.format((1000 * 60 * 12)));
    }

    @Test
    public void testHoursFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("om 3 timmar", t.format((1000 * 60 * 60 * 3)));
    }

    @Test
    public void testDaysFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("om 3 dagar", t.format((1000 * 60 * 60 * 24 * 3)));
    }

    @Test
    public void testWeeksFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("om 3 veckor", t.format((1000 * 60 * 60 * 24 * 7 * 3)));
    }

    @Test
    public void testMonthsFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("om 3 månader", t.format((2629743830L * 3L)));
    }

    @Test
    public void testYearsFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("om 3 år", t.format((2629743830L * 12L * 3L)));
    }

    @Test
    public void testDecadesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("om 3 årtionden", t.format((315569259747L * 3L)));
    }

    @Test
    public void testCenturiesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("om 3 århundraden", t.format((3155692597470L * 3L)));
    }

    /*
     * Past
     */
    @Test
    public void testMomentsAgo() {
        PrettyTime t = new PrettyTime((6000), locale);
        assertEquals("en stund sedan", t.format((0)));
    }

    @Test
    public void testMinutesAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 12), locale);
        assertEquals("12 minuter sedan", t.format((0)));
    }

    @Test
    public void testHoursAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 3), locale);
        assertEquals("3 timmar sedan", t.format((0)));
    }

    @Test
    public void testDaysAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3), locale);
        assertEquals("3 dagar sedan", t.format((0)));
    }

    @Test
    public void testWeeksAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7 * 3), locale);
        assertEquals("3 veckor sedan", t.format((0)));
    }

    @Test
    public void testMonthsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 3L), locale);
        assertEquals("3 månader sedan", t.format((0)));
    }

    @Test
    public void testYearsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 12L * 3L), locale);
        assertEquals("3 år sedan", t.format((0)));
    }

    @Test
    public void testDecadesAgo() {
        PrettyTime t = new PrettyTime((315569259747L * 3L), locale);
        assertEquals("3 årtionden sedan", t.format((0)));
    }

    @Test
    public void testCenturiesAgo() {
        PrettyTime t = new PrettyTime((3155692597470L * 3L), locale);
        assertEquals("3 århundraden sedan", t.format((0)));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
