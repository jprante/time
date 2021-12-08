package org.xbib.time.chronic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xbib.time.chronic.repeaters.DayName;
import org.xbib.time.chronic.repeaters.RepeaterDayName;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class RepeaterDayNameTest {

    private final static ZoneId ZONE_ID = ZoneId.of("GMT");

    private final ZonedDateTime now = construct(2006, 8, 16, 14, 0, 0);

    public static ZonedDateTime construct(int year, int month, int day) {
        return ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZONE_ID);
    }

    public static ZonedDateTime construct(int year, int month, int day, int hour, int minutes, int seconds) {
        return ZonedDateTime.of(year, month, day, hour, minutes, seconds, 0, ZONE_ID);
    }

    @Test
    public void testMatch() {
        Token token = new Token("saturday");
        RepeaterDayName repeater = RepeaterDayName.scan(token);
        assertEquals(DayName.SATURDAY, repeater.getType());

        token = new Token("sunday");
        repeater = RepeaterDayName.scan(token);
        assertEquals(DayName.SUNDAY, repeater.getType());
    }

    @Test
    public void testNextFuture() {
        Span span;

        RepeaterDayName mondays = new RepeaterDayName(DayName.MONDAY);
        mondays.setNow(now);
        span = mondays.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2006, 8, 21), span.getBeginCalendar());
        assertEquals(construct(2006, 8, 22), span.getEndCalendar());

        span = mondays.nextSpan(PointerType.FUTURE);
        assertEquals(construct(2006, 8, 28), span.getBeginCalendar());
        assertEquals(construct(2006, 8, 29), span.getEndCalendar());
    }

    @Test
    public void testNextPast() {
        Span span;

        RepeaterDayName mondays = new RepeaterDayName(DayName.MONDAY);
        mondays.setNow(now);
        span = mondays.nextSpan(PointerType.PAST);
        assertEquals(construct(2006, 8, 14), span.getBeginCalendar());
        assertEquals(construct(2006, 8, 15), span.getEndCalendar());

        span = mondays.nextSpan(PointerType.PAST);
        assertEquals(construct(2006, 8, 7), span.getBeginCalendar());
        assertEquals(construct(2006, 8, 8), span.getEndCalendar());
    }
}
