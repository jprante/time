package org.xbib.time.chronic.repeaters;

import org.xbib.time.chronic.Span;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 *
 */
public class RepeaterHour extends RepeaterUnit {

    public static final int HOUR_SECONDS = 3600;

    private ZonedDateTime currentDayStart;

    private static ZonedDateTime ymdh(ZonedDateTime zonedDateTime) {
        return ZonedDateTime.of(zonedDateTime.getYear(), zonedDateTime.getMonthValue(), zonedDateTime.getDayOfMonth(),
                zonedDateTime.getHour(), 0, 0, 0, zonedDateTime.getZone());
    }

    private static ZonedDateTime ymdhm(ZonedDateTime zonedDateTime) {
        return ZonedDateTime.of(zonedDateTime.getYear(), zonedDateTime.getMonthValue(), zonedDateTime.getDayOfMonth(),
                zonedDateTime.getHour(), zonedDateTime.getMinute(), 0, 0, zonedDateTime.getZone());
    }

    @Override
    protected Span internalNextSpan(PointerType pointer) {
        if (currentDayStart == null) {
            if (pointer == PointerType.FUTURE) {
                currentDayStart = ymdh(getNow()).plus(1, ChronoUnit.HOURS);
            } else if (pointer == PointerType.PAST) {
                currentDayStart = ymdh(getNow()).minus(1, ChronoUnit.HOURS);
            } else {
                throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
            }
        } else {
            int direction = (pointer == PointerType.FUTURE) ? 1 : -1;
            currentDayStart = currentDayStart.plus(direction, ChronoUnit.HOURS);
        }
        return new Span(currentDayStart, ChronoUnit.HOURS, 1);
    }

    @Override
    protected Span internalThisSpan(PointerType pointer) {
        ZonedDateTime hourStart;
        ZonedDateTime hourEnd;
        if (pointer == PointerType.FUTURE) {
            hourStart = ymdhm(getNow()).plus(1, ChronoUnit.MINUTES);
            hourEnd = ymdh(getNow()).plus(1, ChronoUnit.HOURS);
        } else if (pointer == PointerType.PAST) {
            hourStart = ymdh(getNow());
            hourEnd = ymdhm(getNow());
        } else if (pointer == PointerType.NONE) {
            hourStart = ymdh(getNow());
            hourEnd = hourStart.plus(1, ChronoUnit.HOURS);
        } else {
            throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
        }
        return new Span(hourStart, hourEnd);
    }

    @Override
    public Span getOffset(Span span, int amount, PointerType pointer) {
        long direction = pointer == PointerType.FUTURE ? 1L : -1L;
        return span.add(direction * amount * RepeaterHour.HOUR_SECONDS);
    }

    @Override
    public int getWidth() {
        return RepeaterHour.HOUR_SECONDS;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ getWidth();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RepeaterHour &&
                ((Repeater) other).getType().equals(getType()) &&
                ((Repeater) other).getNow().equals(getNow());
    }

    @Override
    public String toString() {
        return super.toString() + "-hour";
    }
}
