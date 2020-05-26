package org.xbib.time.schedule;

import org.junit.jupiter.api.Test;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NextExecutionTest {

    private static final Logger logger = Logger.getLogger(NextExecutionTest.class.getName());

    @Test
    public void nextSecond() {
        CronExpression expression = CronExpression.parser().withSecondsField(true)
                .parse("0-59 * * * * *");
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime next = expression.nextExecution(now, now.plusMinutes(1));
        logger.log(Level.INFO, now.toString());
        logger.log(Level.INFO, next.toString());
    }

    @Test
    public void nextHour() {
        CronExpression expression = CronExpression.hourly();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime next = expression.nextExecution(now, now.plusHours(2));
        logger.log(Level.INFO, now.toString());
        logger.log(Level.INFO, next.toString());
    }

    @Test
    public void nextDay() {
        CronExpression expression = CronExpression.daily();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime next = expression.nextExecution(now, now.plusDays(2));
        logger.log(Level.INFO, now.toString());
        logger.log(Level.INFO, next.toString());
    }

    @Test
    public void nextWeek() {
        CronExpression expression = CronExpression.weekly();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime next = expression.nextExecution(now, now.plusWeeks(2));
        logger.log(Level.INFO, now.toString());
        logger.log(Level.INFO, next.toString());
    }

    @Test
    public void nextMonth() {
        CronExpression expression = CronExpression.monthly();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime next = expression.nextExecution(now, now.plusMonths(2));
        logger.log(Level.INFO, now.toString());
        logger.log(Level.INFO, next.toString());
    }

    @Test
    public void nextFractionMinute() {
        CronExpression expression = CronExpression.parse("10-20/5 * * * * *");
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime next = expression.nextExecution(now, now.plusHours(2));
        logger.log(Level.INFO, now.toString());
        logger.log(Level.INFO, next.toString());
    }

    @Test
    public void nextMultipleNth() {
        CronExpression expression = CronExpression.parser()
                .withSecondsField(true)
                .parse("0/5 14,18,3-39,52 * ? JAN,MAR,SEP MON-FRI 2002-2010");
        ZonedDateTime begin = ZonedDateTime.of(2002, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        logger.log(Level.INFO, expression.toString());
        logger.log(Level.INFO, begin.toString());
        ZonedDateTime next = expression.nextExecution(begin, begin.plusYears(1));
        logger.log(Level.INFO, next.toString());
    }
}
