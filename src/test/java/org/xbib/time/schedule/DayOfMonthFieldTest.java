package org.xbib.time.schedule;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public class DayOfMonthFieldTest {

    @Test
    public void last() {
        DayOfMonthField field = parse("L");
        assertTrue(field.matches(ZonedDateTime.now().with(TemporalAdjusters.lastDayOfMonth())));
    }

    @Test
    public void nearestFriday() {
        ZonedDateTime saturday = ZonedDateTime.now().with(DayOfWeek.SATURDAY);
        ZonedDateTime friday = saturday.minusDays(1);
        DayOfMonthField field = parse(saturday.getDayOfMonth() + "W");
        assertTrue(field.matches(friday));
    }

    @Test
    public void nearestMonday() {
        ZonedDateTime sunday = ZonedDateTime.now().with(DayOfWeek.SUNDAY);
        ZonedDateTime monday = sunday.plusDays(1);
        DayOfMonthField field = parse(sunday.getDayOfMonth() + "W");
        assertTrue(field.matches(monday));
    }

    private DayOfMonthField parse(String s) {
        return DayOfMonthField.parse(new Tokens(s));
    }
}
