package org.xbib.time.pretty.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xbib.time.pretty.PrettyTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class SimpleTimeFormatTimeQuantifiedNameTest {

    private Locale defaultLocale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale locale = new Locale("yy");
        Locale.setDefault(locale);
    }

    @Test
    public void testFuturePluralName() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        assertEquals("2 days from now", p.format(1000 * 60 * 60 * 24 * 2));
    }

    @Test
    public void testPastPluralName() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 2), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        assertEquals("2 days ago", p.format(0));
    }

    @Test
    public void testFutureSingularName() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        assertEquals("1 day from now", p.format(1000 * 60 * 60 * 24));
    }

    @Test
    public void testPastSingularName() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        assertEquals("1 day ago", p.format(0));
    }

    @Test
    public void testFuturePluralNameEmpty() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        assertEquals("2 hours from now", p.format(1000 * 60 * 60 * 2));
    }

    @Test
    public void testPastPluralNameMissing() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 2), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        assertEquals("2 hours ago", p.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault())));
    }

    @Test
    public void testFutureSingularNameCopy() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        assertEquals("1 hour from now", p.format(1000 * 60 * 60));
    }

    @Test
    public void testPastSingularNameNull() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        assertEquals("1 hour ago", p.format(0));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }
}
