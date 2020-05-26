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

public class PrettyTimeI18n_hi_IN_Test {

    private Locale defaultLocale;

    private Locale locale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        locale = new Locale("hi", "IN");
        Locale.setDefault(locale);
    }

    @Test
    public void testLocaleISOCorrectness() {
        assertEquals("hi", this.locale.getLanguage());
        assertEquals("IN", this.locale.getCountry());
        assertEquals("हिन्दी", this.locale.getDisplayLanguage());
        assertEquals("भारत", this.locale.getDisplayCountry());
    }

    @Test
    public void testNow() {
        PrettyTime prettyTime = new PrettyTime(locale);
        assertEquals("अभी", prettyTime.format(LocalDateTime.now()));
    }

    @Test
    public void testCeilingInterval() {
        LocalDateTime then = LocalDateTime.of(2009, 5, 20, 0, 0);
        LocalDateTime ref = LocalDateTime.of(2009, 6, 17, 0, 0);
        PrettyTime t = new PrettyTime(ref);
        assertEquals("1 महीना पहले", t.format(then));
    }

    @Test
    public void testRightNow() {
        PrettyTime t = new PrettyTime();
        assertEquals("अभी", t.format(LocalDateTime.now()));
    }

    @Test
    public void testRightNowVariance() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("अभी", t.format((600)));
    }

    @Test
    public void testMinutesFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("12 मिनट बाद", t.format((1000 * 60 * 12)));
    }

    @Test
    public void testHoursFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 घंटे बाद", t.format((1000 * 60 * 60 * 3)));
    }

    @Test
    public void testDaysFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 दिन बाद",
                t.format((1000 * 60 * 60 * 24 * 3)));
    }

    @Test
    public void testWeeksFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 सप्ताह बाद",
                t.format((1000 * 60 * 60 * 24 * 7 * 3)));
    }

    @Test
    public void testMonthsFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 महीने बाद", t.format((2629743830L * 3L)));
        //assertEquals("अभी से 3 महीने बाद", t.format((1000 * 60 * 60 * 24 * 365 * 3L)));
    }

    @Test
    public void testYearsFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 वर्ष बाद",
                t.format((2629743830L * 12L * 3L)));
    }

    @Test
    public void testDecadesFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 दशक बाद",
                t.format((315569259747L * 3L)));
    }

    @Test
    public void testCenturiesFromNow() {
        PrettyTime t = new PrettyTime((0));
        assertEquals("3 सदियों बाद",
                t.format((3155692597470L * 3L)));
    }

    /*
     * Past
     */
    @Test
    public void testMomentsAgo() {
        PrettyTime t = new PrettyTime((6000));
        assertEquals("अभी", t.format((0)));
    }

    @Test
    public void testMinutesAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 12));
        assertEquals("12 मिनट पहले", t.format((0)));
    }

    @Test
    public void testHoursAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 3));
        assertEquals("3 घंटे पहले", t.format((0)));
    }

    @Test
    public void testDaysAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3));
        assertEquals("3 दिन पहले", t.format((0)));
    }

    @Test
    public void testWeeksAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7 * 3));
        assertEquals("3 सप्ताह पहले", t.format((0)));
    }

    @Test
    public void testMonthsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 3L));
        assertEquals("3 महीने पहले", t.format((0)));
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
        t.registerUnit(unit, new SimpleTimeFormat().setSingularName("खेल")
                .setPluralName("खेल").setPattern("%n %u")
                .setRoundingTolerance(20).setFutureSuffix("होंगे ")
                .setFuturePrefix("भविष्य में ")
                .setPastPrefix("पहले ")
                .setPastSuffix("थे"));

        assertEquals("भविष्य में 5 खेल होंगे", t.format((25000)));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(25000), ZoneId.systemDefault());
        t.setReference(localDateTime);
        assertEquals("पहले 5 खेल थे", t.format((0)));
    }

    @Test
    public void testYearsAgo() {
        PrettyTime t = new PrettyTime((2629743830L * 12L * 3L));
        assertEquals("3 वर्ष पहले", t.format((0)));
    }

    @Test
    public void testDecadesAgo() {
        PrettyTime t = new PrettyTime((315569259747L * 3L));
        assertEquals("3 दशक पहले", t.format((0)));
    }

    @Test
    public void testCenturiesAgo() {
        PrettyTime t = new PrettyTime((3155692597470L * 3L));
        assertEquals("3 सदियों पहले", t.format((0)));
    }

    @Test
    public void testWithinTwoHoursRounding() {
        PrettyTime t = new PrettyTime();
        LocalDateTime localDateTime = LocalDateTime.now().minusSeconds(6544);
        assertEquals("2 घंटे पहले", t.format(localDateTime));
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
        assertTrue(timeUnitQuantities.get(1).getQuantity() == -9 || timeUnitQuantities.get(1).getQuantity() == -10);
    }

    @Test
    public void testFormattingDurationListInThePast() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3 + 1000
                * 60 * 60 * 15 + 1000 * 60 * 38));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertEquals("3 दिन 15 घंटे 38 मिनट पहले", t.format(timeUnitQuantities));
    }

    @Test
    public void testFormattingDurationListInTheFuture() {
        PrettyTime t = new PrettyTime((0));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 60 * 15
                + 1000 * 60 * 38), ZoneId.systemDefault());
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertEquals("3 दिन 15 घंटे 38 मिनट बाद", t.format(timeUnitQuantities));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
