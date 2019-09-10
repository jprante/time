package org.xbib.time.schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.xbib.time.schedule.DateTimes.nthOfMonth;
import org.junit.Test;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WhatQuartzDoesNotSupport {
    @Test
    public void multipleNthDayOfWeek() {
        try {
            org.quartz.CronExpression quartz = new org.quartz.CronExpression("0 0 0 ? * 6#3,4#1,3#2");
            List<ZonedDateTime> times = new ArrayList<>();
            ZonedDateTime t = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).withDayOfYear(1);
            int year = t.getYear();
            while (t.getYear() == year) {
                times.add(nthOfMonth(t, DayOfWeek.FRIDAY, 3));
                times.add(nthOfMonth(t, DayOfWeek.TUESDAY, 2));
                t = t.plusMonths(1);
            }
            for (ZonedDateTime time : times) {
                boolean satisfied = quartz.isSatisfiedBy(Date.from(time.toInstant()));
                if (time.getDayOfWeek() == DayOfWeek.TUESDAY) {
                    // Earlier versions of Quartz only picked up the last one
                    assertTrue(satisfied);
                } else {
                    assertFalse(satisfied);
                }
            }
        } catch (ParseException e) {
            assertEquals("Support for specifying multiple \"nth\" days is not implemented.", e.getMessage());
        }
    }

    @Test
    public void multipleLastDayOfWeek() throws Exception {
        try {
            new org.quartz.CronExpression("0 0 0 ? * 6L,4L,3L");
            fail("Expected exception");
        } catch (ParseException e) {
            assertEquals("Support for specifying 'L' with other days of the week is not implemented", e.getMessage());
        }
    }
}
