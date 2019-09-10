package org.xbib.time.schedule;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DateTimes {

    static Iterable<Date> toDates(Iterable<ZonedDateTime> times) {
        return StreamSupport.stream(times.spliterator(), false)
                .map(input -> Date.from(input.toInstant())).collect(Collectors.toList());
    }

    static ZonedDateTime midnight() {
        return now().truncatedTo(ChronoUnit.DAYS);
    }

    static ZonedDateTime startOfHour() {
        return now().truncatedTo(ChronoUnit.HOURS);
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    static ZonedDateTime lastOfMonth(ZonedDateTime t, DayOfWeek dayOfWeek) {
        ZonedDateTime day = t.with(TemporalAdjusters.lastDayOfMonth()).with(dayOfWeek);
        if (day.getMonth() != t.getMonth()) {
            day = day.minusWeeks(1);
        }
        return day;
    }

    static ZonedDateTime nthOfMonth(ZonedDateTime t, DayOfWeek dayOfWeek, int desiredNumber) {
        Month month = t.getMonth();
        t = t.withDayOfMonth(1).with(dayOfWeek);
        if (t.getMonth() != month) {
            t = t.plusWeeks(1);
        }
        int number = 1;
        while (number < desiredNumber && t.getMonth() == month) {
            number++;
            t = t.plusWeeks(1);
        }
        return t;
    }

    static ZonedDateTime nearestWeekday(ZonedDateTime t) {
        if (t.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return t.minusDays(1);
        } else if (t.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return t.plusDays(1);
        }
        return t;
    }

    static ZonedDateTime startOfYear() {
        return midnight().withDayOfYear(1);
    }
}
