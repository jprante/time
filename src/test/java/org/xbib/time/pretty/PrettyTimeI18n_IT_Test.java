package org.xbib.time.pretty;

import org.junit.Before;
import org.junit.Test;
import org.xbib.time.pretty.units.JustNow;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class PrettyTimeI18n_IT_Test {
    private Locale locale;

    @Before
    public void setUp() throws Exception {
        locale = new Locale("it");
    }

    @Test
    public void testRightNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra poco", t.format((6000)));
    }

    @Test
    public void testMomentsAgo() throws Exception {
        PrettyTime t = new PrettyTime((6000), locale);
        assertEquals("poco fa", t.format((0)));
    }

    @Test
    public void testMilliSecondsFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 13 millisecondi", t.format((13)));
    }

    @Test
    public void testMilliSecondsAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((13), locale);
        assertEquals("13 millisecondi fa", t.format((0)));
    }

    @Test
    public void testMilliSecondFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 1 millisecondo", t.format((1)));
    }

    @Test
    public void testMilliSecondAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((1), locale);
        assertEquals("1 millisecondo fa", t.format((0)));
    }

    @Test
    public void testSecondsFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 13 secondi", t.format((1000 * 13)));
    }

    @Test
    public void testSecondsAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((1000 * 13), locale);
        assertEquals("13 secondi fa", t.format((0)));
    }

    @Test
    public void testSecondFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 1 secondo", t.format(1000));
    }

    @Test
    public void testSecondAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((1000), locale);
        assertEquals("1 secondo fa", t.format((0)));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 13 minuti", t.format((1000 * 60 * 13)));
    }

    @Test
    public void testMinutesAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000 * 60 * 13), locale);
        assertEquals("13 minuti fa", t.format((0)));
    }

    @Test
    public void testMinuteFromNow() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 1 minuto", t.format((1000 * 60)));
    }

    @Test
    public void testMinuteAgo() throws Exception {
        PrettyTime t = newPrettyTimeWOJustNow((1000 * 60), locale);
        assertEquals("1 minuto fa", t.format((0)));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 ore", t.format((1000 * 60 * 60 * 3)));
    }

    @Test
    public void testHoursAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 3), locale);
        assertEquals("3 ore fa", t.format((0)));
    }

    @Test
    public void testHoursFromNowSingle() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 ora", t.format((1000 * 60 * 60)));
    }

    @Test
    public void testHoursAgoSingle() throws Exception {
        PrettyTime t = new PrettyTime((1000 * 60 * 60), locale);
        assertEquals("1 ora fa", t.format((0)));
    }

    @Test
    public void testDaysFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 giorni", t.format((1000 * 60 * 60 * 24 * 3)));
    }

    @Test
    public void testDaysAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3), locale);
        assertEquals("3 giorni fa", t.format((0)));
    }

    @Test
    public void testDaysFromNowSingle() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 giorno", t.format((1000 * 60 * 60 * 24)));
    }

    @Test
    public void testDaysAgoSingle() throws Exception {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24), locale);
        assertEquals("1 giorno fa", t.format((0)));
    }

    @Test
    public void testWeeksFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 settimane", t.format((1000 * 60 * 60 * 24 * 7 * 3)));
    }

    @Test
    public void testWeeksAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7 * 3), locale);
        assertEquals("3 settimane fa", t.format((0)));
    }

    @Test
    public void testWeeksFromNowSingle() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 settimana", t.format((1000 * 60 * 60 * 24 * 7)));
    }

    @Test
    public void testWeeksAgoSingle() throws Exception {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7), locale);
        assertEquals("1 settimana fa", t.format((0)));
    }

    @Test
    public void testMonthsFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 mesi", t.format((1000L * 60 * 60 * 24 * 30 * 3)));
    }

    @Test
    public void testMonthsAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 30 * 3), locale);
        assertEquals("3 mesi fa", t.format((0)));
    }

    @Test
    public void testMonthFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 mese", t.format((1000L * 60 * 60 * 24 * 30)));
    }

    @Test
    public void testMonthAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 30), locale);
        assertEquals("1 mese fa", t.format((0)));
    }

    @Test
    public void testYearsFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 anni", t.format((1000L * 60 * 60 * 24 * 365 * 3)));
    }

    @Test
    public void testYearsAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 3), locale);
        assertEquals("3 anni fa", t.format((0)));
    }

    @Test
    public void testYearFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 anno", t.format((1000L * 60 * 60 * 24 * 366)));
    }

    @Test
    public void testYearAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 366), locale);
        assertEquals("1 anno fa", t.format((0)));
    }

    @Test
    public void testDecadesFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 decenni", t.format((1000L * 60 * 60 * 24 * 365 * 10 * 3)));
    }

    @Test
    public void testDecadesAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 10 * 3), locale);
        assertEquals("3 decenni fa", t.format((0)));
    }

    @Test
    public void testDecadeFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 decennio", t.format((1000L * 60 * 60 * 24 * 365 * 11)));
    }

    @Test
    public void testDecadeAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 11), locale);
        assertEquals("1 decennio fa", t.format((0)));
    }

    @Test
    public void testCenturiesFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 secoli", t.format((1000L * 60 * 60 * 24 * 365 * 100 * 3)));
    }

    @Test
    public void testCenturiesAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 100 * 3), locale);
        assertEquals("3 secoli fa", t.format((0)));
    }

    @Test
    public void testCenturyFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 secolo", t.format((1000L * 60 * 60 * 24 * 365 * 101)));
    }

    @Test
    public void testCenturyAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 101), locale);
        assertEquals("1 secolo fa", t.format((0)));
    }

    @Test
    public void testMillenniaFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 millenni", t.format((1000L * 60 * 60 * 24 * 365 * 1000 * 3)));
    }

    @Test
    public void testMillenniaAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 1000 * 3), locale);
        assertEquals("3 millenni fa", t.format((0)));
    }

    @Test
    public void testMillenniumFromNow() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 millennio", t.format((1000L * 60 * 60 * 24 * 365 * 1001)));
    }

    @Test
    public void testMillenniumAgo() throws Exception {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 1001), locale);
        assertEquals("1 millennio fa", t.format((0)));
    }

    @Test
    public void testFormattingDurationListInThePast() throws Exception {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 60 * 15 + 1000 * 60 * 38), locale);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertEquals("3 giorni 15 ore 38 minuti fa", t.format(timeUnitQuantities));
    }

    @Test
    public void testFormattingDurationListInTheFuture() throws Exception {
        PrettyTime t = new PrettyTime((0), locale);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 60 * 15
                + 1000 * 60 * 38), ZoneId.systemDefault());
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertEquals("fra 3 giorni 15 ore 38 minuti", t.format(timeUnitQuantities));
    }

    private PrettyTime newPrettyTimeWOJustNow(long ref, Locale locale) {
        PrettyTime t = new PrettyTime(ref, locale);
        List<TimeUnit> units = t.getUnits();
        List<TimeFormat> formats = new ArrayList<>();
        for (TimeUnit timeUnit : units) {
            if (!(timeUnit instanceof JustNow)) {
                formats.add(t.getFormat(timeUnit));
            }
        }
        int index = 0;
        t.clearUnits();
        for (TimeUnit timeUnit : units) {
            if (!(timeUnit instanceof JustNow)) {
                t.registerUnit(timeUnit, formats.get(index));
                index++;
            }
        }
        return t;
    }
}
