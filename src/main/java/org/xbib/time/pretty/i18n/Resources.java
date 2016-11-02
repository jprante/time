package org.xbib.time.pretty.i18n;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Resources extends MapResourceBundle {
    private static final Map<String, Object> map = new HashMap<>();

    static {
        map.put("CenturyPattern", "%n %u");
        map.put("CenturyFuturePrefix", "");
        map.put("CenturyFutureSuffix", " from now");
        map.put("CenturyPastPrefix", "");
        map.put("CenturyPastSuffix", " ago");
        map.put("CenturySingularName", "century");
        map.put("CenturyPluralName", "centuries");
        map.put("DayPattern", "%n %u");
        map.put("DayFuturePrefix", "");
        map.put("DayFutureSuffix", " from now");
        map.put("DayPastPrefix", "");
        map.put("DayPastSuffix", " ago");
        map.put("DaySingularName", "day");
        map.put("DayPluralName", "days");
        map.put("DecadePattern", "%n %u");
        map.put("DecadeFuturePrefix", "");
        map.put("DecadeFutureSuffix", " from now");
        map.put("DecadePastPrefix", "");
        map.put("DecadePastSuffix", " ago");
        map.put("DecadeSingularName", "decade");
        map.put("DecadePluralName", "decades");
        map.put("HourPattern", "%n %u");
        map.put("HourFuturePrefix", "");
        map.put("HourFutureSuffix", " from now");
        map.put("HourPastPrefix", "");
        map.put("HourPastSuffix", " ago");
        map.put("HourSingularName", "hour");
        map.put("HourPluralName", "hours");
        map.put("JustNowPattern", "%u");
        map.put("JustNowFuturePrefix", "");
        map.put("JustNowFutureSuffix", "moments from now");
        map.put("JustNowPastPrefix", "moments ago");
        map.put("JustNowPastSuffix", "");
        map.put("JustNowSingularName", "");
        map.put("JustNowPluralName", "");
        map.put("MillenniumPattern", "%n %u");
        map.put("MillenniumFuturePrefix", "");
        map.put("MillenniumFutureSuffix", " from now");
        map.put("MillenniumPastPrefix", "");
        map.put("MillenniumPastSuffix", " ago");
        map.put("MillenniumSingularName", "millennium");
        map.put("MillenniumPluralName", "millennia");
        map.put("MillisecondPattern", "%n %u");
        map.put("MillisecondFuturePrefix", "");
        map.put("MillisecondFutureSuffix", " from now");
        map.put("MillisecondPastPrefix", "");
        map.put("MillisecondPastSuffix", " ago");
        map.put("MillisecondSingularName", "millisecond");
        map.put("MillisecondPluralName", "milliseconds");
        map.put("MinutePattern", "%n %u");
        map.put("MinuteFuturePrefix", "");
        map.put("MinuteFutureSuffix", " from now");
        map.put("MinutePastPrefix", "");
        map.put("MinutePastSuffix", " ago");
        map.put("MinuteSingularName", "minute");
        map.put("MinutePluralName", "minutes");
        map.put("MonthPattern", "%n %u");
        map.put("MonthFuturePrefix", "");
        map.put("MonthFutureSuffix", " from now");
        map.put("MonthPastPrefix", "");
        map.put("MonthPastSuffix", " ago");
        map.put("MonthSingularName", "month");
        map.put("MonthPluralName", "months");
        map.put("SecondPattern", "%n %u");
        map.put("SecondFuturePrefix", "");
        map.put("SecondFutureSuffix", " from now");
        map.put("SecondPastPrefix", "");
        map.put("SecondPastSuffix", " ago");
        map.put("SecondSingularName", "second");
        map.put("SecondPluralName", "seconds");
        map.put("WeekPattern", "%n %u");
        map.put("WeekFuturePrefix", "");
        map.put("WeekFutureSuffix", " from now");
        map.put("WeekPastPrefix", "");
        map.put("WeekPastSuffix", " ago");
        map.put("WeekSingularName", "week");
        map.put("WeekPluralName", "weeks");
        map.put("YearPattern", "%n %u");
        map.put("YearFuturePrefix", "");
        map.put("YearFutureSuffix", " from now");
        map.put("YearPastPrefix", "");
        map.put("YearPastSuffix", " ago");
        map.put("YearSingularName", "year");
        map.put("YearPluralName", "years");
        map.put("AbstractTimeUnitPattern", "");
        map.put("AbstractTimeUnitFuturePrefix", "");
        map.put("AbstractTimeUnitFutureSuffix", "");
        map.put("AbstractTimeUnitPastPrefix", "");
        map.put("AbstractTimeUnitPastSuffix", "");
        map.put("AbstractTimeUnitSingularName", "");
        map.put("AbstractTimeUnitPluralName", "");
    }

    @Override
    public Map<String, Object> getContents() {
        return map;
    }

}
