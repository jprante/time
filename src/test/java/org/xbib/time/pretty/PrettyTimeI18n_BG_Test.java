package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

public class PrettyTimeI18n_BG_Test {

    private Locale defaultLocale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale locale = new Locale("bg");
        Locale.setDefault(locale);
    }

    @Test
    public void testCenturiesFromNow() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("след 3 века", t.format(3155692597470L * 3L));
    }

    @Test
    public void testCenturiesAgo() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(3155692597470L * 3L), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 3 века", t.format(0));
    }

    @Test
    public void testCenturySingular() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(3155692597470L), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 1 век", t.format(0));
    }

    @Test
    public void testDaysFromNow() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("след 3 дни", t.format(1000 * 60 * 60 * 24 * 3));
    }

    @Test
    public void testDaysAgo() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 3), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 3 дни", t.format(0));
    }

    @Test
    public void testDaySingular() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 1 ден", t.format(0));
    }

    @Test
    public void testDecadesAgo() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(315569259747L * 3L), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 3 десетилетия", t.format(0));
    }

    @Test
    public void testDecadesFromNow() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("след 3 десетилетия", t.format(315569259747L * 3L));
    }

    @Test
    public void testDecadeSingular() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("след 1 десетилетие", t.format(315569259747L));
    }

    @Test
    public void testHoursFromNow() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("след 3 часа", t.format(1000 * 60 * 60 * 3));
    }

    @Test
    public void testHoursAgo() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 3), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 3 часа", t.format(0));
    }

    @Test
    public void testHourSingular() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 1 час", t.format(0));
    }

    @Test
    public void testRightNow() {
        PrettyTime t = new PrettyTime();
        assertEquals("в момента", t.format(LocalDateTime.now()));
    }

    @Test
    public void testMomentsAgo() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(6000), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("току що", t.format(0));
    }

    @Test
    public void testMinutesFromNow() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("след 12 минути", t.format(1000 * 60 * 12));
    }

    @Test
    public void testMinutesAgo() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 12), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 12 минути", t.format(0));
    }

    @Test
    public void testMonthsFromNow() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("след 3 месеца", t.format((2629743830L * 3L)));
    }

    @Test
    public void testMonthsAgo() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(2629743830L * 3L), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 3 месеца", t.format(0));
    }

    @Test
    public void testMonthSingular() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(2629743830L), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 1 месец", t.format(0));
    }

    @Test
    public void testWeeksFromNow() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("след 3 седмици", t.format(1000 * 60 * 60 * 24 * 7 * 3));
    }

    @Test
    public void testWeeksAgo() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 7 * 3), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 3 седмици", t.format(0));
    }

    @Test
    public void testWeekSingular() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 7), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 1 седмица", t.format(0));
    }

    @Test
    public void testYearsFromNow() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("след 3 години", t.format(2629743830L * 12L * 3L));
    }

    @Test
    public void testYearsAgo() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(2629743830L * 12L * 3L), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 3 години", t.format(0));
    }

    @Test
    public void testYearSingular() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(2629743830L * 12L), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(localDateTime);
        assertEquals("преди 1 година", t.format(0));
    }

    @Test
    public void testFormattingDurationListInThePast() {
        PrettyTime t = new PrettyTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 60 * 15 + 1000 * 60 * 38), ZoneId.systemDefault()));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertEquals("преди 3 дни 15 часа 38 минути", t.format(timeUnitQuantities));
    }

    @Test
    public void testFormattingDurationListInTheFuture() {
        PrettyTime t = new PrettyTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault()));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 60 * 15
                + 1000 * 60 * 38), ZoneId.systemDefault());
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertEquals("след 3 дни 15 часа 38 минути", t.format(timeUnitQuantities));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
