package org.xbib.time.pretty.units;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TimeUnitComparatorTest {

    @Test
    public void testComparingOrder() {
        TimeUnitComparator comparator = new TimeUnitComparator();
        assertEquals(-1, comparator.compare(new Hour(), new Day()));
    }

}
