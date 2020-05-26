package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

public class PrettyTimeTest {

    private Locale defaultLocale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ROOT);
    }

    @Test
    public void testCeilingInterval() {
        LocalDateTime then = LocalDateTime.of(2009, 5, 20, 0, 0);
        LocalDateTime ref = LocalDateTime.of(2009, 6, 17, 0, 0);
        PrettyTime t = new PrettyTime(ref);
        assertEquals("1 month ago", t.format(then));
    }

    @Test
    public void testRightNow() {
        PrettyTime t = new PrettyTime();
        assertEquals("moments from now", t.format(LocalDateTime.now()));
    }

    @Test
    public void testRightNowVariance() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("moments from now", t.format(600));
    }

    @Test
    public void testMinutesFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("12 minutes from now", t.format((1000 * 60 * 12)));
    }

    @Test
    public void testHoursFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 hours from now", t.format((1000 * 60 * 60 * 3)));
    }

    @Test
    public void testDaysFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 days from now", t.format((1000 * 60 * 60 * 24 * 3)));
    }

    @Test
    public void testWeeksFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 weeks from now", t.format((1000 * 60 * 60 * 24 * 7 * 3)));
    }

    @Test
    public void testMonthsFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 months from now", t.format((2629743830L * 3L)));
    }

    @Test
    public void testYearsFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 years from now", t.format((2629743830L * 12L * 3L)));
    }

    @Test
    public void testDecadesFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 decades from now", t.format((315569259747L * 3L)));
    }

    @Test
    public void testCenturiesFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 centuries from now", t.format((3155692597470L * 3L)));
    }

    @Test
    public void testMomentsAgo() {
        PrettyTime t = new PrettyTime((6000));
        assertEquals("moments ago", t.format((0)));
    }

    @Test
    public void testMinutesAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 12));
        assertEquals("12 minutes ago", t.format((0)));
    }

    @Test
    public void testHoursAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 3));
        assertEquals("3 hours ago", t.format((0)));
    }

    @Test
    public void testDaysAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3));
        assertEquals("3 days ago", t.format((0)));
    }

    @Test
    public void testWeeksAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7 * 3));
        assertEquals("3 weeks ago", t.format((0)));
    }

    @Test
    public void testMonthsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 3L));
        assertEquals("3 months ago", t.format((0)));
    }

    @Test
    public void testCustomFormat() {
        PrettyTime t = new PrettyTime((0));
        TimeUnit unit = new TimeUnit() {
            @Override
            public long getMaxQuantity() {
                return 0;
            }

            @Override
            public long getMillisPerUnit() {
                return 5000;
            }
        };
        t.clearUnits();
        t.registerUnit(unit, new SimpleTimeFormat()
                .setSingularName("tick").setPluralName("ticks")
                .setPattern("%n %u").setRoundingTolerance(20)
                .setFutureSuffix("... RUN!")
                .setFuturePrefix("self destruct in: ").setPastPrefix("self destruct was: ").setPastSuffix(
                        " ago..."));

        assertEquals("self destruct in: 5 ticks ... RUN!", t.format((25000)));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(25000), ZoneId.systemDefault());
        t.setReference(localDateTime);
        assertEquals("self destruct was: 5 ticks ago...", t.format((0)));
    }

    @Test
    public void testYearsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 12L * 3L));
        assertEquals("3 years ago", t.format((0)));
    }

    @Test
    public void testDecadesAgo() {
        PrettyTime t = new PrettyTime((315569259747L * 3L));
        assertEquals("3 decades ago", t.format((0)));
    }

    @Test
    public void testCenturiesAgo() {
        PrettyTime t = new PrettyTime((3155692597470L * 3L));
        assertEquals("3 centuries ago", t.format((0)));
    }

    @Test
    public void testWithinTwoHoursRounding() {
        PrettyTime t = new PrettyTime();
        LocalDateTime localDateTime = LocalDateTime.now().minusSeconds(6544);
        assertEquals("2 hours ago", t.format(localDateTime));
    }

    @Test
    public void testPreciseInTheFuture() {
        PrettyTime t = new PrettyTime();
        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(10 * 60 + 5 * 60 * 60);
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertTrue(timeUnitQuantities.size() >= 2);
        assertEquals(5, timeUnitQuantities.get(0).getQuantity());
        assertEquals(10, timeUnitQuantities.get(1).getQuantity());
    }

    @Test
    public void testPreciseInThePast() {
        PrettyTime t = new PrettyTime();
        LocalDateTime localDateTime = LocalDateTime.now().minusSeconds(10 * 60 + 5 * 60 * 60);
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertTrue(timeUnitQuantities.size() >= 2);
        assertEquals(-5, timeUnitQuantities.get(0).getQuantity());
        // TODO why -10 or -9?
        assertTrue(-10 == timeUnitQuantities.get(1).getQuantity() || -9 == timeUnitQuantities.get(1).getQuantity());
    }

    @Test
    public void testFormattingDurationListInThePast() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 60 * 15 + 1000 * 60 * 38));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertEquals("3 days 15 hours 38 minutes ago", t.format(timeUnitQuantities));
    }

    @Test
    public void testFormattingDurationListInTheFuture() {
        PrettyTime t = new PrettyTime((0));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 60 * 15
                + 1000 * 60 * 38), ZoneId.systemDefault());
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertEquals("3 days 15 hours 38 minutes from now", t.format(timeUnitQuantities));
    }

    @Test
    public void testSetLocale() {
        PrettyTime t = new PrettyTime((315569259747L * 3L));
        assertEquals("3 decades ago", t.format((0)));
        t.setLocale(Locale.GERMAN);
        assertEquals("vor 3 Jahrzehnten", t.format((0)));
    }

    @Test
    public void testFormatApproximateDuration() {
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(10);
        PrettyTime t = new PrettyTime();
        String result = t.formatApproximateDuration(localDateTime);
        assert result.equals("10 minutes");
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
