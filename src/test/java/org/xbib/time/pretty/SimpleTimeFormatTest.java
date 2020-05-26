package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class SimpleTimeFormatTest {

    private Locale defaultLocale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ROOT);
    }

    @Test
    public void testRounding() {
        PrettyTime t = new PrettyTime(1000 * 60 * 60 * 3 + 1000 * 60 * 45);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        TimeUnitQuantity timeUnitQuantity = t.approximateDuration(localDateTime);
        assertEquals("4 hours ago", t.format(timeUnitQuantity));
        assertEquals("3 hours ago", t.formatUnrounded(timeUnitQuantity));
    }

    @Test
    public void testDecorating() {
        PrettyTime t = new PrettyTime();
        TimeFormat format = new SimpleTimeFormat().setFutureSuffix("from now").setPastSuffix("ago");

        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(1);
        TimeUnitQuantity timeUnitQuantity = t.approximateDuration(localDateTime);
        assertEquals("some time from now", format.decorate(timeUnitQuantity, "some time"));

        localDateTime = LocalDateTime.now().minusSeconds(10);
        timeUnitQuantity = t.approximateDuration(localDateTime);
        assertEquals("some time ago", format.decorate(timeUnitQuantity, "some time"));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
