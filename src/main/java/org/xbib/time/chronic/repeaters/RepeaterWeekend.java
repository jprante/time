package org.xbib.time.chronic.repeaters;

import org.xbib.time.chronic.Span;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 *
 */
public class RepeaterWeekend extends RepeaterUnit {

    private static final long WEEKEND_SECONDS = 172800L; // (2 * 24 * 60 * 60);

    private ZonedDateTime currentWeekStart;

    @Override
    protected Span internalNextSpan(PointerType pointer) {
        if (currentWeekStart != null) {
            long direction = pointer == PointerType.FUTURE ? 1L : -1L;
            currentWeekStart = currentWeekStart.plus(direction * RepeaterWeek.WEEK_SECONDS, ChronoUnit.SECONDS);
            ZonedDateTime c = currentWeekStart.plus(RepeaterWeekend.WEEKEND_SECONDS, ChronoUnit.SECONDS);
            return new Span(currentWeekStart, c);
        }
        ZonedDateTime c;
        switch (pointer) {
            case PAST: {
                RepeaterDayName saturdayRepeater = new RepeaterDayName(DayName.SATURDAY);
                saturdayRepeater.setNow(getNow().plus(RepeaterDay.DAY_SECONDS, ChronoUnit.SECONDS));
                Span lastSaturdaySpan = saturdayRepeater.nextSpan(PointerType.PAST);
                currentWeekStart = lastSaturdaySpan.getBeginCalendar();
                c = currentWeekStart.plus(RepeaterWeekend.WEEKEND_SECONDS, ChronoUnit.SECONDS);
                return new Span(currentWeekStart, c);
            }
            case FUTURE: {
                RepeaterDayName saturdayRepeater = new RepeaterDayName(DayName.SATURDAY);
                saturdayRepeater.setNow(getNow());
                Span nextSaturdaySpan = saturdayRepeater.nextSpan(PointerType.FUTURE);
                currentWeekStart = nextSaturdaySpan.getBeginCalendar();
                c = currentWeekStart.plus(RepeaterWeekend.WEEKEND_SECONDS, ChronoUnit.SECONDS);
                return new Span(currentWeekStart, c);
            }
            default:
                break;
        }
        throw new IllegalArgumentException("pointer type not expected");
    }

    @Override
    protected Span internalThisSpan(PointerType pointer) {
        Span thisSpan;
        if (pointer == PointerType.FUTURE || pointer == PointerType.NONE) {
            RepeaterDayName saturdayRepeater = new RepeaterDayName(DayName.SATURDAY);
            saturdayRepeater.setNow(getNow());
            Span thisSaturdaySpan = saturdayRepeater.nextSpan(PointerType.FUTURE);
            thisSpan = new Span(thisSaturdaySpan.getBeginCalendar(), thisSaturdaySpan.getBeginCalendar()
                    .plus(RepeaterWeekend.WEEKEND_SECONDS, ChronoUnit.SECONDS));
        } else if (pointer == PointerType.PAST) {
            RepeaterDayName saturdayRepeater = new RepeaterDayName(DayName.SATURDAY);
            saturdayRepeater.setNow(getNow());
            Span lastSaturdaySpan = saturdayRepeater.nextSpan(PointerType.PAST);
            thisSpan = new Span(lastSaturdaySpan.getBeginCalendar(), lastSaturdaySpan.getBeginCalendar()
                    .plus(RepeaterWeekend.WEEKEND_SECONDS, ChronoUnit.SECONDS));
        } else {
            throw new IllegalArgumentException("Unable to handle pointer " + pointer + ".");
        }
        return thisSpan;
    }

    @Override
    public Span getOffset(Span span, int amount, PointerType pointer) {
        long direction = pointer == PointerType.FUTURE ? 1L : -1L;
        RepeaterWeekend weekend = new RepeaterWeekend();
        weekend.setNow(span.getBeginCalendar());
        ZonedDateTime start = weekend.nextSpan(pointer).getBeginCalendar().plus((amount - 1) *
                direction * RepeaterWeek.WEEK_SECONDS, ChronoUnit.SECONDS);
        return new Span(start, start.plus(span.getWidth(), ChronoUnit.SECONDS));
    }

    @Override
    public int getWidth() {
        return (int) RepeaterWeekend.WEEKEND_SECONDS;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ getWidth();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RepeaterWeekend &&
                ((Repeater) other).getType().equals(getType()) &&
                ((Repeater) other).getNow().equals(getNow());
    }

    @Override
    public String toString() {
        return super.toString() + "-weekend";
    }
}
