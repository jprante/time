package org.xbib.time.chronic.repeaters;

import org.xbib.time.chronic.Range;

/**
 *
 */
public class EnumRepeaterDayPortion extends RepeaterDayPortion<DayPortion> {

    private static final Range AM_RANGE = new Range(0, 12 * 60 * 60); // 12am-12pm

    private static final Range PM_RANGE = new Range(12 * 60 * 60, 24 * 60 * 60 - 1); // 12pm-12am

    private static final Range MORNING_RANGE = new Range(6 * 60 * 60, 12 * 60 * 60); // 6am-12pm

    private static final Range AFTERNOON_RANGE = new Range(13 * 60 * 60, 17 * 60 * 60); // 1pm-5pm

    private static final Range EVENING_RANGE = new Range(17 * 60 * 60, 20 * 60 * 60); // 5pm-8pm

    private static final Range NIGHT_RANGE = new Range(20 * 60 * 60, 24 * 60 * 60); // 8pm-12pm

    public EnumRepeaterDayPortion(DayPortion type) {
        super(type);
    }

    @Override
    protected Range createRange(DayPortion type) {
        Range range;
        if (type == DayPortion.AM) {
            range = EnumRepeaterDayPortion.AM_RANGE;
        } else if (type == DayPortion.PM) {
            range = EnumRepeaterDayPortion.PM_RANGE;
        } else if (type == DayPortion.MORNING) {
            range = EnumRepeaterDayPortion.MORNING_RANGE;
        } else if (type == DayPortion.AFTERNOON) {
            range = EnumRepeaterDayPortion.AFTERNOON_RANGE;
        } else if (type == DayPortion.EVENING) {
            range = EnumRepeaterDayPortion.EVENING_RANGE;
        } else if (type == DayPortion.NIGHT) {
            range = EnumRepeaterDayPortion.NIGHT_RANGE;
        } else {
            throw new IllegalArgumentException("Unknown day portion type " + type);
        }
        return range;
    }

    @Override
    protected long getWidth(Range range) {
        return range.getWidth();
    }
}
