package org.xbib.time.pretty;

import org.junit.Test;
import org.xbib.time.pretty.units.JustNow;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PrettyTimeAPIManipulationTest {

    List<TimeUnitQuantity> list = null;
    PrettyTime t = new PrettyTime();
    private TimeUnitQuantity timeUnitQuantity = null;

    @Test
    public void testApiMisuse3() throws Exception {
        t.clearUnits();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApiMisuse6() throws Exception {
        t.format(list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApiMisuse8() throws Exception {
        t.formatUnrounded(timeUnitQuantity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApiMisuse9() throws Exception {
        t.getFormat(null);
    }

    @Test
    public void testApiMisuse10() throws Exception {
        t.getLocale();
    }

    @Test
    public void testApiMisuse12() throws Exception {
        t.getUnits();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApiMisuse13() throws Exception {
        t.registerUnit(null, null);
    }

    @Test
    public void testApiMisuse15() throws Exception {
        t.toString();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApiMisuse16() throws Exception {
        t.removeUnit((Class<TimeUnit>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApiMisuse17() throws Exception {
        t.removeUnit((TimeUnit) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApiMisuse18() throws Exception {
        t.getUnit(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApiMisuse19() throws Exception {
        t.getUnit(null);
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
