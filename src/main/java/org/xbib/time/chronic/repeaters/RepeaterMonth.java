package org.xbib.time.chronic.repeaters;

import org.xbib.time.chronic.Span;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 *
 */
public class RepeaterMonth extends RepeaterUnit {

    private static final int MONTH_SECONDS = 2592000; // 30 * 24 * 60 * 60

    private ZonedDateTime currentMonthStart;

    private static ZonedDateTime ymd(ZonedDateTime zonedDateTime) {
        return ZonedDateTime.of(zonedDateTime.getYear(), zonedDateTime.getMonthValue(), zonedDateTime.getDayOfMonth(),
                0, 0, 0, 0, zonedDateTime.getZone());
    }

    @Override
    protected Span internalNextSpan(PointerType pointer) {
        int direction = (pointer == PointerType.FUTURE) ? 1 : -1;
        if (currentMonthStart == null) {
            currentMonthStart = ZonedDateTime.of(getNow().getYear(), getNow().getMonthValue(), 1, 0, 0, 0, 0,
                    getNow().getZone())
                    .plus(direction, ChronoUnit.MONTHS);
        } else {
            currentMonthStart = currentMonthStart.plus(direction, ChronoUnit.MONTHS);
        }

        return new Span(currentMonthStart, ChronoUnit.MONTHS, 1);
    }

    @Override
    public Span getOffset(Span span, int amount, PointerType pointer) {
        long l = amount * (pointer == PointerType.FUTURE ? 1L : -1L);
        return new Span(span.getBeginCalendar().plus(l, ChronoUnit.MONTHS), span.getEndCalendar().plus(l,
                ChronoUnit.MONTHS));
    }

    @Override
    protected Span internalThisSpan(PointerType pointer) {
        ZonedDateTime monthStart;
        ZonedDateTime monthEnd;
        if (pointer == PointerType.FUTURE) {
            monthStart = ymd(getNow()).plus(1, ChronoUnit.DAYS);
            monthEnd = ZonedDateTime.of(getNow().getYear(), getNow().getMonthValue(), 1, 0, 0, 0, 0, getNow().getZone())
                    .plus(1, ChronoUnit.MONTHS);
        } else if (pointer == PointerType.PAST) {
            monthStart = ZonedDateTime.of(getNow().getYear(), getNow().getMonthValue(), 1, 0, 0, 0, 0, getNow().getZone());
            monthEnd = ymd(getNow());
        } else if (pointer == PointerType.NONE) {
            monthStart = ZonedDateTime.of(getNow().getYear(), getNow().getMonthValue(), 1, 0, 0, 0, 0, getNow().getZone());
            monthEnd = ZonedDateTime.of(getNow().getYear(), getNow().getMonthValue(), 1, 0, 0, 0, 0, getNow().getZone())
                    .plus(1, ChronoUnit.MONTHS);
        } else {
            throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
        }
        return new Span(monthStart, monthEnd);
    }

    @Override
    public int getWidth() {
        return RepeaterMonth.MONTH_SECONDS;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ getWidth();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RepeaterMonth &&
                ((Repeater) other).getType().equals(getType()) &&
                ((Repeater) other).getNow().equals(getNow());
    }

    @Override
    public String toString() {
        return super.toString() + "-month";
    }
}
