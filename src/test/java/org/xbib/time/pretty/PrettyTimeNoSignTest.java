package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class PrettyTimeNoSignTest {

    @Test
    public void testNoSuffixes() {
        LocalDateTime then = LocalDateTime.of(2009, 8, 20, 0, 0);
        LocalDateTime ref = LocalDateTime.of(2009, 5, 17, 0, 0);
        PrettyTime p = new PrettyTime(ref, Locale.ENGLISH);

        List<TimeUnit> units = p.getUnits();
        for (TimeUnit unit : units) {
            TimeFormat fmt = p.getFormat(unit);
            if (fmt instanceof SimpleTimeFormat) {
                ((SimpleTimeFormat) fmt).setFuturePrefix("").setFutureSuffix("").setPastPrefix("").setPastSuffix("");
            }
        }
        assertEquals("3 months", p.format(then));
    }
}
