package org.xbib.time.pretty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xbib.time.pretty.units.JustNow;
import java.util.List;

public class PrettyTimeAPIManipulationTest {

    private List<TimeUnitQuantity> list = null;

    private final PrettyTime t = new PrettyTime();

    private final TimeUnitQuantity timeUnitQuantity = null;

    @Test
    public void testApiMisuse3() {
        t.clearUnits();
    }

    @Test
    public void testApiMisuse6() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> t.format(list));
    }

    @Test
    public void testApiMisuse8() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> t.formatUnrounded(timeUnitQuantity));
    }

    @Test
    public void testApiMisuse9() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> t.getFormat(null));
    }

    @Test
    public void testApiMisuse10() {
        t.getLocale();
    }

    @Test
    public void testApiMisuse12() {
        t.getUnits();
    }

    @Test
    public void testApiMisuse13() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> t.registerUnit(null, null));
    }

    @Test
    public void testApiMisuse15() {
        t.toString();
    }

    @Test
    public void testApiMisuse16() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> t.removeUnit((Class<TimeUnit>) null));
    }

    @Test
    public void testApiMisuse17() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> t.removeUnit((TimeUnit) null));
    }

    @Test
    public void testApiMisuse18() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> t.getUnit(null));
    }

    @Test
    public void testApiMisuse19() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> t.getUnit(null));
    }

    @Test
    public void testGetUnit() {
        JustNow unit = t.getUnit(JustNow.class);
        assertNotNull(unit);
    }

    @Test
    public void testChangeUnit() {
        JustNow unit = t.getUnit(JustNow.class);
        assertEquals(1000L * 60L * 5L, unit.getMaxQuantity());
        unit.setMaxQuantity(1);
        assertEquals(1, t.getUnit(JustNow.class).getMaxQuantity());
    }
}
