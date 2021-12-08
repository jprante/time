package org.xbib.time.chronic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.time.chronic.repeaters.RepeaterWeek;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class RepeaterWeekTest {

    private final static ZoneId ZONE_ID = ZoneId.of("GMT");

    private final ZonedDateTime now = construct(2006, 8, 16, 14, 0, 0);

    public static ZonedDateTime construct(int year, int month, int day) {
        return ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZONE_ID);
    }

    public static ZonedDateTime construct(int year, int month, int day, int hour, int minutes, int seconds) {
        return ZonedDateTime.of(year, month, day, hour, minutes, seconds, 0, ZONE_ID);
    }

    public static ZonedDateTime construct(int year, int month, int day, int hour) {
        return ZonedDateTime.of(year, month, day, hour, 0, 0, 0, ZONE_ID);
    }

    @Test
    public void testNextFuture() {
        RepeaterWeek weeks = new RepeaterWeek();
        weeks.setNow(now);

        Span nextWeek = weeks.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2006, 8, 20), nextWeek.getBeginCalendar());
        assertEquals(construct(2006, 8, 27), nextWeek.getEndCalendar());

        Span nextNextWeek = weeks.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2006, 8, 27), nextNextWeek.getBeginCalendar());
        assertEquals(construct(2006, 9, 3), nextNextWeek.getEndCalendar());
    }

    @Test
    public void testNextPast() {
        RepeaterWeek weeks = new RepeaterWeek();
        weeks.setNow(now);
        Span lastWeek = weeks.nextSpan(PointerType.PAST);
        assertEquals(construct(2006, 8, 6), lastWeek.getBeginCalendar());
        assertEquals(construct(2006, 8, 13), lastWeek.getEndCalendar());

        Span lastLastWeek = weeks.nextSpan(PointerType.PAST);
        assertEquals(construct(2006, 7, 30), lastLastWeek.getBeginCalendar());
        assertEquals(construct(2006, 8, 6), lastLastWeek.getEndCalendar());
    }

    @Test
    public void testThisFuture() {
        RepeaterWeek weeks = new RepeaterWeek();
        weeks.setNow(now);

        Span thisWeek = weeks.thisSpan(PointerType.FUTURE);
        assertEquals(construct(2006, 8, 16, 15), thisWeek.getBeginCalendar());
        assertEquals(construct(2006, 8, 20), thisWeek.getEndCalendar());
    }

    @Test
    public void testThisPast() {
        RepeaterWeek weeks = new RepeaterWeek();
        weeks.setNow(now);

        Span thisWeek = weeks.thisSpan(PointerType.PAST);
        assertEquals(construct(2006, 8, 13, 0), thisWeek.getBeginCalendar());
        assertEquals(construct(2006, 8, 16, 14), thisWeek.getEndCalendar());
    }

    @Test
    public void testOffset() {
        Span span = new Span(now, ChronoUnit.SECONDS, 1);

        Span offsetSpan = new RepeaterWeek().getOffset(span, 3, PointerType.FUTURE);

        assertEquals(construct(2006, 9, 6, 14), offsetSpan.getBeginCalendar());
        assertEquals(construct(2006, 9, 6, 14, 0, 1), offsetSpan.getEndCalendar());
    }
}
