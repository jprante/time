package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xbib.time.pretty.units.JustNow;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrettyTimeI18n_IT_Test {

    private Locale defaultLocale;

    private Locale locale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        locale = new Locale("it");
        Locale.setDefault(locale);
    }

    @Test
    public void testRightNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra poco", t.format((6000)));
    }

    @Test
    public void testMomentsAgo() {
        PrettyTime t = new PrettyTime((6000), locale);
        assertEquals("poco fa", t.format((0)));
    }

    @Test
    public void testMilliSecondsFromNow() {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 13 millisecondi", t.format((13)));
    }

    @Test
    public void testMilliSecondsAgo() {
        PrettyTime t = newPrettyTimeWOJustNow((13), locale);
        assertEquals("13 millisecondi fa", t.format((0)));
    }

    @Test
    public void testMilliSecondFromNow() {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 1 millisecondo", t.format((1)));
    }

    @Test
    public void testMilliSecondAgo() {
        PrettyTime t = newPrettyTimeWOJustNow((1), locale);
        assertEquals("1 millisecondo fa", t.format((0)));
    }

    @Test
    public void testSecondsFromNow() {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 13 secondi", t.format((1000 * 13)));
    }

    @Test
    public void testSecondsAgo() {
        PrettyTime t = newPrettyTimeWOJustNow((1000 * 13), locale);
        assertEquals("13 secondi fa", t.format((0)));
    }

    @Test
    public void testSecondFromNow() {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 1 secondo", t.format(1000));
    }

    @Test
    public void testSecondAgo() {
        PrettyTime t = newPrettyTimeWOJustNow((1000), locale);
        assertEquals("1 secondo fa", t.format((0)));
    }

    @Test
    public void testMinutesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 13 minuti", t.format((1000 * 60 * 13)));
    }

    @Test
    public void testMinutesAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 13), locale);
        assertEquals("13 minuti fa", t.format((0)));
    }

    @Test
    public void testMinuteFromNow() {
        PrettyTime t = newPrettyTimeWOJustNow((0), locale);
        assertEquals("fra 1 minuto", t.format((1000 * 60)));
    }

    @Test
    public void testMinuteAgo() {
        PrettyTime t = newPrettyTimeWOJustNow((1000 * 60), locale);
        assertEquals("1 minuto fa", t.format((0)));
    }

    @Test
    public void testHoursFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 ore", t.format((1000 * 60 * 60 * 3)));
    }

    @Test
    public void testHoursAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 3), locale);
        assertEquals("3 ore fa", t.format((0)));
    }

    @Test
    public void testHoursFromNowSingle() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 ora", t.format((1000 * 60 * 60)));
    }

    @Test
    public void testHoursAgoSingle() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60), locale);
        assertEquals("1 ora fa", t.format((0)));
    }

    @Test
    public void testDaysFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 giorni", t.format((1000 * 60 * 60 * 24 * 3)));
    }

    @Test
    public void testDaysAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3), locale);
        assertEquals("3 giorni fa", t.format((0)));
    }

    @Test
    public void testDaysFromNowSingle() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 giorno", t.format((1000 * 60 * 60 * 24)));
    }

    @Test
    public void testDaysAgoSingle() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24), locale);
        assertEquals("1 giorno fa", t.format((0)));
    }

    @Test
    public void testWeeksFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 settimane", t.format((1000 * 60 * 60 * 24 * 7 * 3)));
    }

    @Test
    public void testWeeksAgo() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7 * 3), locale);
        assertEquals("3 settimane fa", t.format((0)));
    }

    @Test
    public void testWeeksFromNowSingle() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 settimana", t.format((1000 * 60 * 60 * 24 * 7)));
    }

    @Test
    public void testWeeksAgoSingle() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 7), locale);
        assertEquals("1 settimana fa", t.format((0)));
    }

    @Test
    public void testMonthsFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 mesi", t.format((1000L * 60 * 60 * 24 * 30 * 3)));
    }

    @Test
    public void testMonthsAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 30 * 3), locale);
        assertEquals("3 mesi fa", t.format((0)));
    }

    @Test
    public void testMonthFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 mese", t.format((1000L * 60 * 60 * 24 * 30)));
    }

    @Test
    public void testMonthAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 30), locale);
        assertEquals("1 mese fa", t.format((0)));
    }

    @Test
    public void testYearsFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 anni", t.format((1000L * 60 * 60 * 24 * 365 * 3)));
    }

    @Test
    public void testYearsAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 3), locale);
        assertEquals("3 anni fa", t.format((0)));
    }

    @Test
    public void testYearFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 anno", t.format((1000L * 60 * 60 * 24 * 366)));
    }

    @Test
    public void testYearAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 366), locale);
        assertEquals("1 anno fa", t.format((0)));
    }

    @Test
    public void testDecadesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 decenni", t.format((1000L * 60 * 60 * 24 * 365 * 10 * 3)));
    }

    @Test
    public void testDecadesAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 10 * 3), locale);
        assertEquals("3 decenni fa", t.format((0)));
    }

    @Test
    public void testDecadeFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 decennio", t.format((1000L * 60 * 60 * 24 * 365 * 11)));
    }

    @Test
    public void testDecadeAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 11), locale);
        assertEquals("1 decennio fa", t.format((0)));
    }

    @Test
    public void testCenturiesFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 secoli", t.format((1000L * 60 * 60 * 24 * 365 * 100 * 3)));
    }

    @Test
    public void testCenturiesAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 100 * 3), locale);
        assertEquals("3 secoli fa", t.format((0)));
    }

    @Test
    public void testCenturyFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 secolo", t.format((1000L * 60 * 60 * 24 * 365 * 101)));
    }

    @Test
    public void testCenturyAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 101), locale);
        assertEquals("1 secolo fa", t.format((0)));
    }

    @Test
    public void testMillenniaFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 3 millenni", t.format((1000L * 60 * 60 * 24 * 365 * 1000 * 3)));
    }

    @Test
    public void testMillenniaAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 1000 * 3), locale);
        assertEquals("3 millenni fa", t.format((0)));
    }

    @Test
    public void testMillenniumFromNow() {
        PrettyTime t = new PrettyTime((0), locale);
        assertEquals("fra 1 millennio", t.format((1000L * 60 * 60 * 24 * 365 * 1001)));
    }

    @Test
    public void testMillenniumAgo() {
        PrettyTime t = new PrettyTime((1000L * 60 * 60 * 24 * 365 * 1001), locale);
        assertEquals("1 millennio fa", t.format((0)));
    }

    @Test
    public void testFormattingDurationListInThePast() {
        PrettyTime t = new PrettyTime((1000 * 60 * 60 * 24 * 3 + 1000 * 60 * 60 * 15 + 1000 * 60 * 38), locale);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        List<TimeUnitQuantity> timeUnitQuantities = t.calculatePreciseDuration(localDateTime);
        assertEquals("3 giorni 15 ore 38 minuti fa", t.format(timeUnitQuantities));
    }

    @Test
    public void testFormattingDurationListInTheFuture() {
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

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
