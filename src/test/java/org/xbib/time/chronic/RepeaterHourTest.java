package org.xbib.time.chronic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.time.chronic.repeaters.RepeaterHour;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class RepeaterHourTest {

    private final static ZoneId ZONE_ID = ZoneId.of("GMT");
    private ZonedDateTime now = construct(2006, 8, 16, 14, 0, 0);

    public static ZonedDateTime construct(int year, int month, int day, int hour) {
        return ZonedDateTime.of(year, month, day, hour, 0, 0, 0, ZONE_ID);
    }

    public static ZonedDateTime construct(int year, int month, int day, int hour, int minutes) {
        return ZonedDateTime.of(year, month, day, hour, minutes, 0, 0, ZONE_ID);
    }

    public static ZonedDateTime construct(int year, int month, int day, int hour, int minutes, int seconds) {
        return ZonedDateTime.of(year, month, day, hour, minutes, seconds, 0, ZONE_ID);
    }

    @Test
    public void testNextFuture() {
        RepeaterHour hours = new RepeaterHour();
        hours.setNow(now);

        Span nextHour = hours.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2006, 8, 16, 15), nextHour.getBeginCalendar());
        assertEquals(construct(2006, 8, 16, 16), nextHour.getEndCalendar());

        Span nextNextHour = hours.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2006, 8, 16, 16), nextNextHour.getBeginCalendar());
        assertEquals(construct(2006, 8, 16, 17), nextNextHour.getEndCalendar());
    }

    @Test
    public void testNextPast() {
        RepeaterHour hours = new RepeaterHour();
        hours.setNow(now);
        Span lastHour = hours.nextSpan(PointerType.PAST);
        assertEquals(construct(2006, 8, 16, 13), lastHour.getBeginCalendar());
        assertEquals(construct(2006, 8, 16, 14), lastHour.getEndCalendar());

        Span lastLastHour = hours.nextSpan(PointerType.PAST);
        assertEquals(construct(2006, 8, 16, 12), lastLastHour.getBeginCalendar());
        assertEquals(construct(2006, 8, 16, 13), lastLastHour.getEndCalendar());
    }

    @Test
    public void testThis() {
        now = construct(2006, 8, 16, 14, 30);

        RepeaterHour hours = new RepeaterHour();
        hours.setNow(now);

        Span thisHour;
        thisHour = hours.thisSpan(PointerType.FUTURE);
        assertEquals(construct(2006, 8, 16, 14, 31), thisHour.getBeginCalendar());
        assertEquals(construct(2006, 8, 16, 15), thisHour.getEndCalendar());

        thisHour = hours.thisSpan(PointerType.PAST);
        assertEquals(construct(2006, 8, 16, 14), thisHour.getBeginCalendar());
        assertEquals(construct(2006, 8, 16, 14, 30), thisHour.getEndCalendar());
    }

    @Test
    public void testOffset() {
        Span span = new Span(now, ChronoUnit.SECONDS, 1);

        Span offsetSpan;
        offsetSpan = new RepeaterHour().getOffset(span, 3, PointerType.FUTURE);

        assertEquals(construct(2006, 8, 16, 17), offsetSpan.getBeginCalendar());
        assertEquals(construct(2006, 8, 16, 17, 0, 1), offsetSpan.getEndCalendar());

        offsetSpan = new RepeaterHour().getOffset(span, 24, PointerType.PAST);

        assertEquals(construct(2006, 8, 15, 14), offsetSpan.getBeginCalendar());
        assertEquals(construct(2006, 8, 15, 14, 0, 1), offsetSpan.getEndCalendar());
    }
}
