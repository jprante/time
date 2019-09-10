package org.xbib.time.schedule;

import static org.junit.Assert.assertTrue;
import com.google.common.base.Stopwatch;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class CompareSpeedToQuartzTest extends CompareBehaviorToQuartzTest {
    @Override
    protected void check(final Iterable<ZonedDateTime> times) throws ParseException {
        final Iterable<Date> dates = DateTimes.toDates(times);
        final CronExpression local = quartzLike.parse(string);
        final org.quartz.CronExpression quartz = new org.quartz.CronExpression(string);
        final int trials = 25;
        final Stopwatch clock = Stopwatch.createStarted();
        for (int i = 0; i < trials; i++) {
            for (ZonedDateTime time : times) {
                local.matches(time);
            }
        }
        final long localNano = clock.elapsed(TimeUnit.NANOSECONDS);
        clock.reset().start();
        for (int i = 0; i < trials; i++) {
            for (Date date : dates) {
                quartz.isSatisfiedBy(date);
            }
        }
        final long quartzNano = clock.elapsed(TimeUnit.NANOSECONDS);
        final boolean lessThanOrEqual = localNano <= quartzNano;
        System.out.printf(
                "%-80s %-60s local %8.2fms %6s Quartz %8.2fms\n",
                nameOfTestMethod(),
                string,
                localNano / 1000000d,
                (lessThanOrEqual ? "<=" : ">"),
                quartzNano / 1000000d
        );
        assertTrue(
                "We took longer for expression '" + string + "'; " + localNano + " > " + quartzNano,
                lessThanOrEqual
        );
    }

    private String nameOfTestMethod() {
        try {
            throw new Exception();
        } catch (Exception e) {
            String method = null;
            Iterator<StackTraceElement> trace = Arrays.asList(e.getStackTrace()).iterator();
            StackTraceElement element = trace.next();
            while (getClass().getName().equals(element.getClassName())) {
                element = trace.next();
            }
            String parentClassName = getClass().getSuperclass().getName();
            while (element.getClassName().equals(parentClassName)) {
                method = element.getMethodName();
                element = trace.next();
            }
            return method;
        }
    }
}
