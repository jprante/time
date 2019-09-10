package org.xbib.time.schedule;

import static org.junit.Assert.assertTrue;
import com.google.caliper.memory.ObjectGraphMeasurer;
import org.junit.Test;

public class CompareSizeToQuartzTest {

    private static final CronExpression.Parser quartzLike = CronExpression.parser()
            .withSecondsField(true)
            .withOneBasedDayOfWeek(true)
            .allowBothDayFields(false);

    @Test
    public void complex() throws Exception {
        check("0/5 14,18,3-39,52 * ? JAN,MAR,SEP MON-FRI 2002-2010");
    }

    @Test
    public void at_noon_every_day() throws Exception {
        check("0 0 12 * * ?");
    }

    @Test
    public void at_10_15am_every_day1() throws Exception {
        check("0 15 10 ? * *");
    }

    @Test
    public void at_10_15am_every_day2() throws Exception {
        check("0 15 10 * * ?");
    }

    @Test
    public void at_10_15am_every_day3() throws Exception {
        check("0 15 10 * * ? *");
    }

    @Test
    public void at_10_15am_every_day_in_2005() throws Exception {
        check("0 15 10 * * ? 2005");
    }

    @Test
    public void every_minute_of_2pm() throws Exception {
        check("0 * 14 * * ?");
    }

    @Test
    public void every_5_minutes_of_2pm() throws Exception {
        check("0 0/5 14 * * ?");
    }

    @Test
    public void every_5_minutes_of_2pm_and_6pm() throws Exception {
        check("0 0/5 14,18 * * ?");
    }

    @Test
    public void first_5_minutes_of_2pm() throws Exception {
        check("0 0-5 14 * * ?");
    }

    @Test
    public void at_2_10pm_and_2_44pm_every_wednesday_in_march() throws Exception {
        check("0 10,44 14 ? 3 WED");
    }

    @Test
    public void at_10_15am_every_weekday() throws Exception {
        check("0 15 10 ? * MON-FRI");
    }

    @Test
    public void at_10_15am_on_the_15th_of_every_month() throws Exception {
        check("0 15 10 15 * ?");
    }

    @Test
    public void at_10_15am_on_the_last_day_of_every_month() throws Exception {
        check("0 15 10 L * ?");
    }

    @Test
    public void at_10_15am_on_the_last_friday_of_every_month() throws Exception {
        check("0 15 10 ? * 6L");
    }

    @Test
    public void at_10_15am_on_the_last_friday_of_every_month_during_2002_through_2005() throws Exception {
        check("0 15 10 ? * 6L 2002-2005");
    }

    @Test
    public void at_10_15am_on_the_third_friday_of_every_month() throws Exception {
        check("0 15 10 ? * 6#3");
    }

    @Test
    public void at_noon_every_5_days_every_month_starting_on_the_first_day_of_the_month() throws Exception {
        check("0 0 12 1/5 * ?");
    }

    @Test
    public void november_11th_at_11_11am() throws Exception {
        check("0 11 11 11 11 ?");
    }

    private void check(String expression) throws Exception {
        CronExpression local = quartzLike.parse(expression);
        org.quartz.CronExpression quartz = new org.quartz.CronExpression(expression);
        long localSize = ObjectSizeCalculator.getObjectSize(local);
        long quartzSize = ObjectSizeCalculator.getObjectSize(quartz);
        assertTrue("We have more bytes", localSize < quartzSize);
        ObjectGraphMeasurer.Footprint localFoot = ObjectGraphMeasurer.measure(local);
        ObjectGraphMeasurer.Footprint quartzFoot = ObjectGraphMeasurer.measure(quartz);
        assertTrue("We have more references", localFoot.getAllReferences() < quartzFoot.getAllReferences());
        assertTrue("We have more non-null references", localFoot.getNonNullReferences() < quartzFoot.getNonNullReferences());
        //assertTrue("We have more null references", localFoot.getNullReferences() < quartzFoot.getNullReferences());
        assertTrue("We have more objects", localFoot.getObjects() < quartzFoot.getObjects());
        assertTrue("We have more primitives", localFoot.getPrimitives().size() < quartzFoot.getPrimitives().size());
    }
}
