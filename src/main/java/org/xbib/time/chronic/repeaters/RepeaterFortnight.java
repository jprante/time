package org.xbib.time.chronic.repeaters;

import org.xbib.time.chronic.Span;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 *
 */
public class RepeaterFortnight extends RepeaterUnit {

    public static final int FORTNIGHT_SECONDS = 1209600; // (14 * 24 * 60 * 60)

    private ZonedDateTime currentFortnightStart;

    private static ZonedDateTime ymdh(ZonedDateTime zonedDateTime) {
        return ZonedDateTime.of(zonedDateTime.getYear(), zonedDateTime.getMonthValue(), zonedDateTime.getDayOfMonth(),
                zonedDateTime.getHour(), 0, 0, 0, zonedDateTime.getZone());
    }

    @Override
    protected Span internalNextSpan(PointerType pointer) {
        if (currentFortnightStart == null) {
            if (pointer == PointerType.FUTURE) {
                RepeaterDayName sundayRepeater = new RepeaterDayName(DayName.SUNDAY);
                sundayRepeater.setNow(getNow());
                Span nextSundaySpan = sundayRepeater.nextSpan(PointerType.FUTURE);
                currentFortnightStart = nextSundaySpan.getBeginCalendar();
            } else if (pointer == PointerType.PAST) {
                RepeaterDayName sundayRepeater = new RepeaterDayName(DayName.SUNDAY);
                sundayRepeater.setNow(getNow().plus(RepeaterDay.DAY_SECONDS, ChronoUnit.SECONDS));
                sundayRepeater.nextSpan(PointerType.PAST);
                sundayRepeater.nextSpan(PointerType.PAST);
                Span lastSundaySpan = sundayRepeater.nextSpan(PointerType.PAST);
                currentFortnightStart = lastSundaySpan.getBeginCalendar();
            } else {
                throw new IllegalArgumentException("Unable to handle pointer " + pointer);
            }
        } else {
            long direction = (pointer == PointerType.FUTURE) ? 1L : -1L;
            currentFortnightStart = currentFortnightStart.plus(direction * RepeaterFortnight.FORTNIGHT_SECONDS,
                    ChronoUnit.SECONDS);
        }

        return new Span(currentFortnightStart, ChronoUnit.SECONDS, RepeaterFortnight.FORTNIGHT_SECONDS);
    }

    @Override
    protected Span internalThisSpan(PointerType pointerType) {
        PointerType pointer = pointerType;
        if (pointer == null) {
            pointer = PointerType.FUTURE;
        }
        Span span;
        if (pointer == PointerType.FUTURE) {
            ZonedDateTime thisFortnightStart = ymdh(getNow()).plus(RepeaterHour.HOUR_SECONDS, ChronoUnit.SECONDS);
            RepeaterDayName sundayRepeater = new RepeaterDayName(DayName.SUNDAY);
            sundayRepeater.setNow(getNow());
            sundayRepeater.thisSpan(PointerType.FUTURE);
            Span thisSundaySpan = sundayRepeater.thisSpan(PointerType.FUTURE);
            ZonedDateTime thisFortnightEnd = thisSundaySpan.getBeginCalendar();
            span = new Span(thisFortnightStart, thisFortnightEnd);
        } else if (pointer == PointerType.PAST) {
            ZonedDateTime thisFortnightEnd = ymdh(getNow());
            RepeaterDayName sundayRepeater = new RepeaterDayName(DayName.SUNDAY);
            sundayRepeater.setNow(getNow());
            Span lastSundaySpan = sundayRepeater.nextSpan(PointerType.PAST);
            ZonedDateTime thisFortnightStart = lastSundaySpan.getBeginCalendar();
            span = new Span(thisFortnightStart, thisFortnightEnd);
        } else {
            throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
        }

        return span;
    }

    @Override
    public Span getOffset(Span span, int amount, PointerType pointer) {
        long direction = (pointer == PointerType.FUTURE) ? 1L : -1L;
        return span.add(direction * amount * RepeaterFortnight.FORTNIGHT_SECONDS);
    }

    @Override
    public int getWidth() {
        return RepeaterFortnight.FORTNIGHT_SECONDS;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ getWidth();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RepeaterFortnight &&
                ((Repeater) other).getType().equals(getType()) &&
                ((Repeater) other).getNow().equals(getNow());
    }

    @Override
    public String toString() {
        return super.toString() + "-fortnight";
    }
}
