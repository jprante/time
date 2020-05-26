package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Locale;

public class PrettyTimeLocaleFallbackTest {

    private Locale defaultLocale;

    @BeforeEach
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("Foo", "Bar"));
    }

    @Test
    public void testCeilingInterval() {
        assertEquals(new Locale("Foo", "Bar"), Locale.getDefault());
        LocalDateTime then = LocalDateTime.of(2009, 5, 20, 0, 0);
        LocalDateTime ref = LocalDateTime.of(2009, 6, 17, 0, 0);
        PrettyTime t = new PrettyTime(ref);
        assertEquals("1 month ago", t.format(then));
    }

    @AfterEach
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }

}
