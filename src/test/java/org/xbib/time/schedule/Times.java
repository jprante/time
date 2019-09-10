package org.xbib.time.schedule;

import com.google.common.collect.ImmutableSortedSet;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.NavigableSet;

public class Times {

    final Integers
            seconds,
            minutes,
            hours,
            months,
            daysOfWeek,
            years,
            daysOfMonth;

    Times() {
        seconds = new Integers();
        minutes = new Integers();
        hours = new Integers();
        months = new Integers();
        daysOfWeek = new Integers();
        years = new Integers();
        daysOfMonth = new Integers();
    }

    NavigableSet<ZonedDateTime> dateTimes() {
        if (seconds.isEmpty()) {
            seconds.withRange(0, 1);
        }
        if (minutes.isEmpty()) {
            minutes.withRange(0, 1);
        }
        if (hours.isEmpty()) {
            hours.withRange(0, 1);
        }
        if (months.isEmpty()) {
            months.withRange(1, 2);
        }
        if (years.isEmpty()) {
            int thisYear = ZonedDateTime.now().getYear();
            years.withRange(thisYear, thisYear + 1);
        }
        ImmutableSortedSet.Builder<ZonedDateTime> builder = ImmutableSortedSet.naturalOrder();
        for (int second : seconds) {
            for (int minute : minutes) {
                for (int hour : hours) {
                    for (int month : months) {
                        for (int year : years) {
                            ZonedDateTime base = ZonedDateTime.now()
                                    .truncatedTo(ChronoUnit.DAYS)
                                    .withSecond(second)
                                    .withMinute(minute)
                                    .withHour(hour)
                                    .withMonth(month)
                                    .withDayOfMonth(1)
                                    .withYear(year);
                            if (!daysOfWeek.isEmpty() && !daysOfMonth.isEmpty()) {
                                addDaysOfWeek(builder, base);
                                addDaysOfMonth(builder, base);
                            } else if (!daysOfWeek.isEmpty()) {
                                addDaysOfWeek(builder, base);
                            } else if (!daysOfMonth.isEmpty()) {
                                addDaysOfMonth(builder, base);
                            } else {
                                builder.add(base);
                            }
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    private void addDaysOfWeek(ImmutableSortedSet.Builder<ZonedDateTime> builder, ZonedDateTime base) {
        Month month = base.getMonth();
        Iterator<Integer> iterator = daysOfWeek.iterator();
        base = base.with(DayOfWeek.of(iterator.next()));
        if (base.getMonth() != month) {
            base = base.plusWeeks(1);
        }
        do {
            builder.add(base);
            base = base.plusWeeks(1);
        } while (base.getMonth() == month);
    }

    private void addDaysOfMonth(ImmutableSortedSet.Builder<ZonedDateTime> builder, ZonedDateTime base) {
        for (int day : daysOfMonth) {
            if (day <= base.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth()) {
                builder.add(base.withDayOfMonth(day));
            }
        }
    }
}
