package org.xbib.time.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.Period;

public class PeriodFormatterBuilderTest {

    @Test
    public void test() throws IOException {
        PeriodFormatter yearsAndMonths = new PeriodFormatterBuilder()
                .appendYears()
                .appendSuffix(" year", " years")
                .appendSeparator(" and ")
                .appendMonths()
                .appendSuffix(" month", " months")
                .toFormatter();
        assertEquals("3 years and 2 months", yearsAndMonths.print(Period.of(3, 2, 1)));
    }
}
