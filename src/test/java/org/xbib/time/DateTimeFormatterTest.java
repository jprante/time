package org.xbib.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeFormatterTest {

    @Test
    public void testLocalDate() {
        String pattern = "yyyyMMdd";
        String name1 = DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
        String name2 = DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.systemDefault())
                .withLocale(Locale.getDefault())
                .format(LocalDate.now());
        assertEquals(name1, name2);
    }
}
