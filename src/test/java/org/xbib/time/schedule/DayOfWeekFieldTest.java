package org.xbib.time.schedule;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class DayOfWeekFieldTest {

    @Test
    public void keywords() {
        assertTrue(parse("MON", false).contains(1));
        assertTrue(parse("TUE", false).contains(2));
        assertTrue(parse("WED", false).contains(3));
        assertTrue(parse("THU", false).contains(4));
        assertTrue(parse("FRI", false).contains(5));
        assertTrue(parse("SAT", false).contains(6));
        assertTrue(parse("SUN", false).contains(7));
    }

    @Test
    public void oneBased() {
        assertTrue(parse("1", true).contains(0));
        assertTrue(parse("2", true).contains(1));
    }

    private DayOfWeekField parse(String s, boolean oneBased) {
        return DayOfWeekField.parse(new Tokens(s), oneBased);
    }
}
