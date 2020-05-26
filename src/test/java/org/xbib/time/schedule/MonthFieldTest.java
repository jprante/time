package org.xbib.time.schedule;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class MonthFieldTest {

    @Test
    public void keywords() {
        assertTrue(parse("JAN").contains(1));
        assertTrue(parse("FEB").contains(2));
        assertTrue(parse("MAR").contains(3));
        assertTrue(parse("APR").contains(4));
        assertTrue(parse("MAY").contains(5));
        assertTrue(parse("JUN").contains(6));
        assertTrue(parse("JUL").contains(7));
        assertTrue(parse("AUG").contains(8));
        assertTrue(parse("SEP").contains(9));
        assertTrue(parse("OCT").contains(10));
        assertTrue(parse("NOV").contains(11));
        assertTrue(parse("DEC").contains(12));
    }

    private DefaultField parse(String s) {
        return MonthField.parse(new Tokens(s));
    }
}
