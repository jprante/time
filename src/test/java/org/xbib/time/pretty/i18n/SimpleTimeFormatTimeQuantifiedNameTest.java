package org.xbib.time.pretty.i18n;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xbib.time.pretty.PrettyTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class SimpleTimeFormatTimeQuantifiedNameTest {

    private Locale defaultLocale;

    private Locale locale;

    @Before
    public void setUp() throws Exception {
        defaultLocale = Locale.getDefault();
        locale = new Locale("yy");
        Locale.setDefault(locale);
    }

    @Test
    public void testFuturePluralName() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        Assert.assertEquals("2 days from now", p.format(1000 * 60 * 60 * 24 * 2));
    }

    @Test
    public void testPastPluralName() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24 * 2), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        Assert.assertEquals("2 days ago", p.format(0));
    }

    @Test
    public void testFutureSingularName() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        Assert.assertEquals("1 day from now", p.format(1000 * 60 * 60 * 24));
    }

    @Test
    public void testPastSingularName() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 24), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        Assert.assertEquals("1 day ago", p.format(0));
    }

    @Test
    public void testFuturePluralNameEmpty() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        Assert.assertEquals("2 hours from now", p.format(1000 * 60 * 60 * 2));
    }

    @Test
    public void testPastPluralNameMissing() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60 * 2), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        Assert.assertEquals("2 hours ago", p.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault())));
    }

    @Test
    public void testFutureSingularNameCopy() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        Assert.assertEquals("1 hour from now", p.format(1000 * 60 * 60));
    }

    @Test
    public void testPastSingularNameNull() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * 60 * 60), ZoneId.systemDefault());
        PrettyTime p = new PrettyTime(localDateTime);
        Assert.assertEquals("1 hour ago", p.format(0));
    }

    @After
    public void tearDown() throws Exception {
        Locale.setDefault(defaultLocale);
    }
}
