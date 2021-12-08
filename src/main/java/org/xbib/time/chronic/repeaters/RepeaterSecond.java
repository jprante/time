package org.xbib.time.chronic.repeaters;

import org.xbib.time.chronic.Span;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 *
 */
public class RepeaterSecond extends RepeaterUnit {

    private static final int SECOND_SECONDS = 1;

    private ZonedDateTime secondStart;

    @Override
    protected Span internalNextSpan(PointerType pointer) {
        int direction = pointer == PointerType.FUTURE ? 1 : -1;
        if (secondStart == null) {
            secondStart = getNow().plus(direction, ChronoUnit.SECONDS);
        } else {
            secondStart = secondStart.plus(direction, ChronoUnit.SECONDS);
        }
        return new Span(secondStart, ChronoUnit.SECONDS, 1);
    }

    @Override
    protected Span internalThisSpan(PointerType pointer) {
        return new Span(getNow(), ChronoUnit.SECONDS, 1);
    }

    @Override
    public Span getOffset(Span span, int amount, PointerType pointer) {
        long direction = pointer == PointerType.FUTURE ? 1L : -1L;
        return span.add(direction * amount * RepeaterSecond.SECOND_SECONDS);
    }

    @Override
    public int getWidth() {
        return RepeaterSecond.SECOND_SECONDS;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ getWidth();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RepeaterSecond &&
                ((Repeater) other).getType().equals(getType()) &&
                ((Repeater) other).getNow().equals(getNow());
    }

    @Override
    public String toString() {
        return super.toString() + "-second";
    }
}
