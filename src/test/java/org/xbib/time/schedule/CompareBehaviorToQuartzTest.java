package org.xbib.time.schedule;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.xbib.time.schedule.util.DateTimes.toDates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xbib.time.schedule.util.DateTimes;
import org.xbib.time.schedule.util.Times;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompareBehaviorToQuartzTest {

    static final CronExpression.Parser quartzLike = CronExpression.parser()
            .withSecondsField(true)
            .withOneBasedDayOfWeek(true)
            .allowBothDayFields(false);

    private static final String timeFormatString = "s m H d M E yyyy";

    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(timeFormatString);

    private final DateFormat dateFormat = new SimpleDateFormat(timeFormatString);

    protected String string;

    private Times expected;

    @BeforeEach
    public void before() {
        expected = new Times();
    }

    @Test
    public void complex() throws Exception {
        string = "0/5 14,18,3-39,52 * ? JAN,MAR,SEP MON-FRI 2002-2010";
        expected.seconds.with(0);
        expected.minutes.with(52).withRange(3, 39);
        expected.months.with(1, 3, 9);
        expected.daysOfWeek.withRange(1, 5);
        expected.years.withRange(2002, 2010);
        check();
    }

    @Test
    public void at_noon_every_day() throws Exception {
        string = "0 0 12 * * ?";
        expected.seconds.with(0);
        expected.minutes.with(0);
        expected.hours.with(12);
        check();
    }

    @Test
    public void at_10_15am_every_day1() throws Exception {
        string = "0 15 10 ? * *";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        check();
    }

    @Test
    public void at_10_15am_every_day2() throws Exception {
        string = "0 15 10 * * ?";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        check();
    }

    @Test
    public void at_10_15am_every_day3() throws Exception {
        string = "0 15 10 * * ? *";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        check();
    }


    @Test
    public void at_10_15am_every_day_in_2005() throws Exception {
        string = "0 15 10 * * ? 2005";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        expected.years.with(2005);
        check();
    }

    @Test
    public void every_minute_of_2pm() throws Exception {
        string = "0 * 14 * * ?";
        expected.seconds.with(0);
        expected.hours.with(14);
        check();
    }

    @Test
    public void every_5_minutes_of_2pm() throws Exception {
        string = "0 0/5 14 * * ?";
        expected.seconds.with(0);
        expected.minutes.withRange(0, 59, 5);
        expected.hours.with(14);
        check();
    }

    @Test
    public void every_5_minutes_of_2pm_and_6pm() throws Exception {
        string = "0 0/5 14,18 * * ?";
        expected.seconds.with(0);
        expected.minutes.withRange(0, 59, 5);
        expected.hours.with(14, 18);
        check();
    }

    @Test
    public void first_5_minutes_of_2pm() throws Exception {
        string = "0 0-5 14 * * ?";
        expected.seconds.with(0);
        expected.minutes.withRange(0, 5);
        expected.hours.with(14);
        check();
    }

    @Test
    public void at_2_10pm_and_2_44pm_every_wednesday_in_march() throws Exception {
        string = "0 10,44 14 ? 3 WED";
        expected.seconds.with(0);
        expected.minutes.with(10, 44);
        expected.hours.with(14);
        expected.months.with(3);
        expected.daysOfWeek.with(3);
        check();
    }

    @Test
    public void at_10_15am_every_weekday() throws Exception {
        string = "0 15 10 ? * MON-FRI";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        expected.daysOfWeek.withRange(1, 5);
        check();
    }

    @Test
    public void at_10_15am_on_the_15th_of_every_month() throws Exception {
        string = "0 15 10 15 * ?";
        expected.seconds.with(0);
        expected.minutes.with(15);
        expected.hours.with(10);
        expected.daysOfMonth.with(15);
        check();
    }

    @Test
    public void at_10_15am_on_the_last_day_of_every_month() throws Exception {
        string = "0 15 10 L * ?";
        List<ZonedDateTime> times = new ArrayList<>();
        ZonedDateTime t = ZonedDateTime.now().withDayOfYear(1).truncatedTo(ChronoUnit.DAYS).plusHours(10).plusMinutes(15);
        int year = t.getYear();
        while (t.getYear() == year) {
            times.add(t.with(TemporalAdjusters.lastDayOfMonth()));
            t = t.plusMonths(1);
        }
        check(times);
    }

    @Test
    public void at_10_15am_on_the_last_friday_of_every_month() throws Exception {
        string = "0 15 10 ? * 6L";
        List<ZonedDateTime> times = new ArrayList<>();
        ZonedDateTime t = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusHours(10).plusMinutes(15);
        int year = t.getYear();
        while (t.getYear() == year) {
            times.add(DateTimes.lastOfMonth(t, DayOfWeek.FRIDAY));
            t = t.plusMonths(1);
        }
        check(times);
    }

    @Test
    public void at_10_15am_on_the_last_friday_of_every_month_during_2002_through_2005() throws Exception {
        string = "0 15 10 ? * 6L 2002-2005";
        List<ZonedDateTime> times = new ArrayList<>();
        for (int year = 2002; year <= 2005; year++) {
            ZonedDateTime t = ZonedDateTime.now().withYear(year).truncatedTo(ChronoUnit.DAYS).plusHours(10).plusMinutes(15);
            while (t.getYear() == year) {
                times.add(DateTimes.lastOfMonth(t, DayOfWeek.FRIDAY));
                t = t.plusMonths(1);
            }
        }
        check(times);
    }


    @Test
    @Disabled
    // TODO let's see if we can make this more reliably faster than the respective quartz run
    public void at_10_15am_on_the_third_friday_of_every_month() throws Exception {
        string = "0 15 10 ? * 6#3";
        List<ZonedDateTime> times = new ArrayList<>();
        ZonedDateTime t = ZonedDateTime.now().withDayOfYear(1).truncatedTo(ChronoUnit.DAYS).plusHours(10).plusMinutes(15);
        int year = t.getYear();
        while (t.getYear() == year) {
            times.add(DateTimes.nthOfMonth(t, DayOfWeek.FRIDAY, 3));
            t = t.plusMonths(1);
        }
        check(times);
    }

    @Test
    public void at_noon_every_5_days_every_month_starting_on_the_first_day_of_the_month() throws Exception {
        string = "0 0 12 1/5 * ?";
        expected.seconds.with(0);
        expected.minutes.with(0);
        expected.hours.with(12);
        expected.daysOfMonth.withRange(1, 31, 5);
        check();
    }

    @Test
    public void november_11th_at_11_11am() throws Exception {
        string = "0 11 11 11 11 ?";
        expected.seconds.with(0);
        expected.minutes.with(11);
        expected.hours.with(11);
        expected.daysOfMonth.with(11);
        expected.months.with(11);
        check();
    }

    private void check() throws ParseException {
        check(expected.dateTimes());
    }

    protected void check(Iterable<ZonedDateTime> times) throws ParseException {
        checkLocalImplementation(times);
        checkQuartzImplementation(toDates(times));
    }

    private void checkQuartzImplementation(Iterable<Date> times) throws ParseException {
        org.quartz.CronExpression quartz = new org.quartz.CronExpression(string);
        for (Date time : times) {
            assertTrue(quartz.isSatisfiedBy(time), dateFormat.format(time).toUpperCase() + " doesn't match expression: " + string);
        }
    }

    private void checkLocalImplementation(Iterable<ZonedDateTime> times) {
        CronExpression expr = quartzLike.parse(string);
        for (ZonedDateTime time : times) {
            assertTrue(expr.matches(time), time.format(dateTimeFormat).toUpperCase() + " doesn't match expression: " + string);
        }
    }
}
