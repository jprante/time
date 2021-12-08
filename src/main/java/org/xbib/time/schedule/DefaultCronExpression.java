package org.xbib.time.schedule;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.SortedSet;

public class DefaultCronExpression extends CronExpression {

    private final String string;

    private final TimeField second;

    private final TimeField minute;

    private final TimeField hour;

    private final TimeField month;

    private final TimeField year;

    private final DayOfWeekField dayOfWeek;

    private final DayOfMonthField dayOfMonth;

    public DefaultCronExpression(String string, boolean seconds, boolean oneBasedDayOfWeek, boolean allowBothDayFields) {
        this.string = string;
        if (string.isEmpty()) {
            throw new IllegalArgumentException("empty spec not allowed");
        }
        String s = string.toUpperCase();
        Tokens tokens = new Tokens(s);
        if (seconds) {
            second = DefaultField.parse(tokens, 0, 59);
        } else {
            second = MatchAllField.instance;
        }
        minute = DefaultField.parse(tokens, 0, 59);
        hour = DefaultField.parse(tokens, 0, 23);
        dayOfMonth = DayOfMonthField.parse(tokens);
        month = MonthField.parse(tokens);
        dayOfWeek = DayOfWeekField.parse(tokens, oneBasedDayOfWeek);
        if (tokens.hasNext()) {
            year = DefaultField.parse(tokens, 0, 0);
        } else {
            year = MatchAllField.instance;
        }
        if (!allowBothDayFields && !dayOfMonth.isUnspecified() && !dayOfWeek.isUnspecified()) {
            throw new IllegalArgumentException("Day of month and day of week may not both be specified");
        }
    }

    @Override
    public boolean matches(ZonedDateTime t) {
        return second.contains(t.getSecond()) &&
                minute.contains(t.getMinute()) &&
                hour.contains(t.getHour()) &&
                month.contains(t.getMonthValue()) &&
                year.contains(t.getYear()) &&
                dayOfWeek.matches(t) &&
                dayOfMonth.matches(t);
    }

    @Override
    public ZonedDateTime nextExecution(ZonedDateTime from,
                                       ZonedDateTime to) {
        ZonedDateTime next = second instanceof MatchAllField ?
                from.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES) : from.plusSeconds(1).truncatedTo(ChronoUnit.SECONDS);
        while (true) {
            if (next.isBefore(from) || next.isAfter(to)) {
                throw new IllegalStateException("out of range: " + from + " < " + next + " < " + to + " -> " + this);
            }
            SortedSet<Integer> set;
            if (!year.contains(next.getYear())) {
                if (!year.isFullRange()) {
                    set = year.getNumbers().tailSet(next.getYear());
                    if (set.isEmpty()) {
                        next = next.plusYears(1);
                        continue;
                    } else {
                        next = next.plusYears(set.first() - next.getYear());
                    }
                }
            }
            if (!month.contains(next.getMonthValue())) {
                if (!month.isFullRange()) {
                    set = month.getNumbers().tailSet(next.getMonthValue());
                    if (set.isEmpty()) {
                        next = next.plusMonths(1);
                        continue;
                    } else {
                        next = next.plusMonths(set.first() - next.getMonthValue());
                    }
                }
            }
            if (!dayOfMonth.isUnspecified()) {
                if (!dayOfMonth.contains(next.getDayOfMonth())) {
                    if (!dayOfMonth.isFullRange()) {
                        set = dayOfMonth.getNumbers().tailSet(next.getDayOfMonth());
                        if (set.isEmpty()) {
                            next = next.plusDays(1).truncatedTo(ChronoUnit.DAYS);
                            continue;
                        } else {
                            next = next.plusDays(set.first() - next.getDayOfMonth())
                                    .truncatedTo(ChronoUnit.DAYS);
                        }
                    }
                }
            }
            if (!dayOfWeek.isUnspecified()) {
                if (!dayOfWeek.contains(next.getDayOfWeek().getValue())) {
                    if (!dayOfWeek.isFullRange()) {
                        set = dayOfWeek.getNumbers().tailSet(next.getDayOfWeek().getValue());
                        if (set.isEmpty()) {
                            next = next.plusDays(1).truncatedTo(ChronoUnit.DAYS);
                            continue;
                        } else {
                            DayOfWeek dayOfWeek = DayOfWeek.of(set.first());
                            next = next.with(TemporalAdjusters.next(dayOfWeek));

                        }
                    }
                }
            }
            if (!hour.contains(next.getHour())) {
                if (!hour.isFullRange()) {
                    set = hour.getNumbers().tailSet(next.getHour());
                    if (set.isEmpty()) {
                        next = next.plusHours(1).truncatedTo(ChronoUnit.HOURS);
                        continue;
                    } else {
                        next = next.plusHours(set.first() - next.getHour())
                                .truncatedTo(ChronoUnit.HOURS);
                    }
                }
            }
            if (!minute.contains(next.getMinute())) {
                if (!minute.isFullRange()) {
                    set = minute.getNumbers().tailSet(next.getMinute());
                    if (set.isEmpty()) {
                        next = next.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
                        continue;
                    } else {
                        next = next.plusMinutes(set.first() - next.getMinute())
                                .truncatedTo(ChronoUnit.MINUTES);
                    }
                }
            }
            if (!(second instanceof MatchAllField)) {
                if (!second.contains(next.getSecond())) {
                    if (!second.isFullRange()) {
                        set = second.getNumbers().tailSet(next.getSecond());
                        if (set.isEmpty()) {
                            next = next.plusSeconds(1).truncatedTo(ChronoUnit.SECONDS);
                            continue;
                        } else {
                            next = next.plusSeconds(set.first() - next.getSecond())
                                    .truncatedTo(ChronoUnit.SECONDS);
                        }
                    }
                }
            }
            break;
        }
        return next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultCronExpression that = (DefaultCronExpression) o;
        if (!Objects.equals(dayOfMonth, that.dayOfMonth)) {
            return false;
        }
        if (!Objects.equals(dayOfWeek, that.dayOfWeek)) {
            return false;
        }
        if (!Objects.equals(hour, that.hour)) {
            return false;
        }
        if (!Objects.equals(minute, that.minute)) {
            return false;
        }
        if (!Objects.equals(month, that.month)) {
            return false;
        }
        if (!Objects.equals(second, that.second)) {
            return false;
        }
        if (!string.equals(that.string)) {
            return false;
        }
        return Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        int result = string.hashCode();
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (minute != null ? minute.hashCode() : 0);
        result = 31 * result + (hour != null ? hour.hashCode() : 0);
        result = 31 * result + (month != null ? month.hashCode() : 0);
        result = 31 * result + (year != null ? year.hashCode() : 0);
        result = 31 * result + (dayOfWeek != null ? dayOfWeek.hashCode() : 0);
        result = 31 * result + (dayOfMonth != null ? dayOfMonth.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CronExpression[" + string + " -> seconds=" + second +
                ",mins=" + minute +
                ",hrs=" + hour +
                ",dayOfMonths=" + dayOfMonth +
                ",dayOfWeek=" + dayOfWeek +
                ",months=" + month +
                ",yrs=" + year +
                "]";
    }
}
