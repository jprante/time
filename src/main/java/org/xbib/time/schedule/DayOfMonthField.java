package org.xbib.time.schedule;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public class DayOfMonthField extends DefaultField {

    private final boolean lastDay;

    private final boolean nearestWeekday;

    private final boolean unspecified;

    private DayOfMonthField(Builder b) {
        super(b);
        this.lastDay = b.lastDay;
        this.nearestWeekday = b.nearestWeekday;
        this.unspecified = b.unspecified;
    }

    boolean isUnspecified() {
        return unspecified;
    }

    public boolean matches(ZonedDateTime time) {
        if (unspecified) {
            return true;
        }
        final int dayOfMonth = time.getDayOfMonth();
        if (lastDay) {
            return dayOfMonth == time.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        } else if (nearestWeekday) {
            DayOfWeek dayOfWeek = time.getDayOfWeek();
            if ((dayOfWeek == DayOfWeek.MONDAY && contains(time.minusDays(1).getDayOfMonth()))
                    || (dayOfWeek == DayOfWeek.FRIDAY && contains(time.plusDays(1).getDayOfMonth()))) {
                return true;
            }
        }
        return contains(dayOfMonth);
    }

    public static DayOfMonthField parse(Tokens s) {
        return new Builder().parse(s).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DayOfMonthField that = (DayOfMonthField) o;
        return lastDay == that.lastDay && nearestWeekday == that.nearestWeekday;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (lastDay ? 1 : 0);
        result = 31 * result + (nearestWeekday ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return isFullRange() ? "*" : getNumbers().toString();
    }

    public static class Builder extends DefaultField.Builder {

        private boolean lastDay;

        private boolean nearestWeekday;

        private boolean unspecified;

        Builder() {
            super(1, 31);
        }

        @Override
        public DayOfMonthField build() {
            return new DayOfMonthField(this);
        }

        @Override
        protected Builder parse(Tokens tokens) {
            super.parse(tokens);
            return this;
        }

        @Override
        protected boolean parseValue(Tokens tokens, Token token, int first, int last) {
            if (token == Token.MATCH_ONE) {
                unspecified = true;
                return false;
            } else if (token == Token.LAST) {
                lastDay = true;
                return false;
            } else {
                return super.parseValue(tokens, token, first, last);
            }
        }

        @Override
        protected boolean parseNumber(Tokens tokens, Token token, int first, int last) {
            if (token == Token.WEEKDAY) {
                add(first);
                nearestWeekday = true;
                return false;
            } else {
                return super.parseNumber(tokens, token, first, last);
            }
        }
    }
}
