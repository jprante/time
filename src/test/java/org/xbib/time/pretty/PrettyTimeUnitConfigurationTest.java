package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xbib.time.pretty.units.Hour;
import org.xbib.time.pretty.units.JustNow;
import org.xbib.time.pretty.units.Minute;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class PrettyTimeUnitConfigurationTest {

    private Locale defaultLocale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ROOT);
    }

    @Test
    public void testRightNow() {
        LocalDateTime ref = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        LocalDateTime then = LocalDateTime.ofInstant(Instant.ofEpochMilli(2), ZoneId.systemDefault());
        PrettyTime t = new PrettyTime(ref);
        TimeFormat format = t.removeUnit(JustNow.class);
        assertNotNull(format);
        assertEquals("2 milliseconds from now", t.format(then));
    }

    @Test
    public void testMinutesFromNow() {
        PrettyTime t = new PrettyTime(0);
        TimeFormat format = t.removeUnit(Minute.class);
        assertNotNull(format);
        assertEquals("720 seconds from now", t.format(1000 * 60 * 12));
    }

    @Test
    public void testHoursFromNow() {
        PrettyTime t = new PrettyTime(0);
        TimeFormat format = t.removeUnit(Hour.class);
        assertNotNull(format);
        assertEquals("180 minutes from now", t.format(1000 * 60 * 60 * 3));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
