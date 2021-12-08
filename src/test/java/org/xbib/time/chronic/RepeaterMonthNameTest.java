package org.xbib.time.chronic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.time.chronic.repeaters.MonthName;
import org.xbib.time.chronic.repeaters.RepeaterMonthName;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class RepeaterMonthNameTest {

    private final static ZoneId ZONE_ID = ZoneId.of("GMT");

    private final ZonedDateTime now = construct(2006, 8, 16, 14, 0, 0);

    public static ZonedDateTime construct(int year, int month) {
        return ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZONE_ID);
    }

    public static ZonedDateTime construct(int year, int month, int day, int hour, int minutes, int seconds) {
        return ZonedDateTime.of(year, month, day, hour, minutes, seconds, 0, ZONE_ID);
    }

    @Test
    public void testNext() {
        RepeaterMonthName mays = new RepeaterMonthName(MonthName.MAY);
        mays.setNow(now);

        Span nextMay = mays.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2007, 5), nextMay.getBeginCalendar());
        assertEquals(construct(2007, 6), nextMay.getEndCalendar());

        Span nextNextMay = mays.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2008, 5), nextNextMay.getBeginCalendar());
        assertEquals(construct(2008, 6), nextNextMay.getEndCalendar());

        RepeaterMonthName decembers = new RepeaterMonthName(MonthName.DECEMBER);
        decembers.setNow(now);

        Span nextDecember = decembers.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2006, 12), nextDecember.getBeginCalendar());
        assertEquals(construct(2007, 1), nextDecember.getEndCalendar());

        mays = new RepeaterMonthName(MonthName.MAY);
        mays.setNow(now);

        assertEquals(construct(2006, 5), mays.nextSpan(PointerType.PAST).getBeginCalendar());
        assertEquals(construct(2005, 5), mays.nextSpan(PointerType.PAST).getBeginCalendar());
    }


    @Test
    public void testThis() {
        RepeaterMonthName octobers = new RepeaterMonthName(MonthName.MAY);
        octobers.setNow(now);

        Span nextMay = octobers.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2007, 5), nextMay.getBeginCalendar());
        assertEquals(construct(2007, 6), nextMay.getEndCalendar());

        Span nextNextMay = octobers.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2008, 5), nextNextMay.getBeginCalendar());
        assertEquals(construct(2008, 6), nextNextMay.getEndCalendar());
    }
}
