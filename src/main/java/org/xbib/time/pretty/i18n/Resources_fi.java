package org.xbib.time.pretty.i18n;

import org.xbib.time.pretty.SimpleTimeFormat;
import org.xbib.time.pretty.TimeFormat;
import org.xbib.time.pretty.TimeFormatProvider;
import org.xbib.time.pretty.TimeUnit;
import org.xbib.time.pretty.TimeUnitQuantity;
import org.xbib.time.pretty.units.Day;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public class Resources_fi extends ListResourceBundle implements TimeFormatProvider {

    private static Object[][] contents = new Object[][]{
            {"JustNowPattern", "%u"},
            {"JustNowPastSingularName", "hetki"},
            {"JustNowFutureSingularName", "hetken"},
            {"JustNowPastSuffix", "sitten"},
            {"JustNowFutureSuffix", "päästä"},
            {"MillisecondPattern", "%u"},
            {"MillisecondPluralPattern", "%n %u"},
            {"MillisecondPastSingularName", "millisekunti"},
            {"MillisecondPastPluralName", "millisekuntia"},
            {"MillisecondFutureSingularName", "millisekunnin"},
            {"MillisecondPastSuffix", "sitten"},
            {"MillisecondFutureSuffix", "päästä"},
            {"SecondPattern", "%u"},
            {"SecondPluralPattern", "%n %u"},
            {"SecondPastSingularName", "sekunti"},
            {"SecondPastPluralName", "sekuntia"},
            {"SecondFutureSingularName", "sekunnin"},
            {"SecondPastSuffix", "sitten"},
            {"SecondFutureSuffix", "päästä"},
            {"MinutePattern", "%u"},
            {"MinutePluralPattern", "%n %u"},
            {"MinutePastSingularName", "minuutti"},
            {"MinutePastPluralName", "minuuttia"},
            {"MinuteFutureSingularName", "minuutin"},
            {"MinutePastSuffix", "sitten"},
            {"MinuteFutureSuffix", "päästä"},
            {"HourPattern", "%u"},
            {"HourPluralPattern", "%n %u"},
            {"HourPastSingularName", "tunti"},
            {"HourPastPluralName", "tuntia"},
            {"HourFutureSingularName", "tunnin"},
            {"HourPastSuffix", "sitten"},
            {"HourFutureSuffix", "päästä"},
            {"DayPattern", "%u"},
            {"DayPluralPattern", "%n %u"},
            {"DayPastSingularName", "eilen"},
            {"DayPastPluralName", "päivää"},
            {"DayFutureSingularName", "huomenna"},
            {"DayFuturePluralName", "päivän"},
            {"DayPastSuffix", "sitten"},
            {"DayFutureSuffix", "päästä"},
            {"WeekPattern", "%u"},
            {"WeekPluralPattern", "%n %u"},
            {"WeekPastSingularName", "viikko"},
            {"WeekPastPluralName", "viikkoa"},
            {"WeekFutureSingularName", "viikon"},
            {"WeekFuturePluralName", "viikon"},
            {"WeekPastSuffix", "sitten"},
            {"WeekFutureSuffix", "päästä"},
            {"MonthPattern", "%u"},
            {"MonthPluralPattern", "%n %u"},
            {"MonthPastSingularName", "kuukausi"},
            {"MonthPastPluralName", "kuukautta"},
            {"MonthFutureSingularName", "kuukauden"},
            {"MonthPastSuffix", "sitten"},
            {"MonthFutureSuffix", "päästä"},
            {"YearPattern", "%u"},
            {"YearPluralPattern", "%n %u"},
            {"YearPastSingularName", "vuosi"},
            {"YearPastPluralName", "vuotta"},
            {"YearFutureSingularName", "vuoden"},
            {"YearPastSuffix", "sitten"},
            {"YearFutureSuffix", "päästä"},
            {"DecadePattern", "%u"},
            {"DecadePluralPattern", "%n %u"},
            {"DecadePastSingularName", "vuosikymmen"},
            {"DecadePastPluralName", "vuosikymmentä"},
            {"DecadeFutureSingularName", "vuosikymmenen"},
            {"DecadePastSuffix", "sitten"},
            {"DecadeFutureSuffix", "päästä"},
            {"CenturyPattern", "%u"},
            {"CenturyPluralPattern", "%n %u"},
            {"CenturyPastSingularName", "vuosisata"},
            {"CenturyPastPluralName", "vuosisataa"},
            {"CenturyFutureSingularName", "vuosisadan"},
            {"CenturyPastSuffix", "sitten"},
            {"CenturyFutureSuffix", "päästä"},
            {"MillenniumPattern", "%u"},
            {"MillenniumPluralPattern", "%n %u"},
            {"MillenniumPastSingularName", "vuosituhat"},
            {"MillenniumPastPluralName", "vuosituhatta"},
            {"MillenniumFutureSingularName", "vuosituhannen"},
            {"MillenniumPastSuffix", "sitten"},
            {"MillenniumFutureSuffix", "päästä"},
    };
    private volatile ConcurrentMap<TimeUnit, TimeFormat> formatMap = new ConcurrentHashMap<>();

    public Resources_fi() {
    }

    @Override
    public TimeFormat getFormatFor(TimeUnit t) {
        if (!formatMap.containsKey(t)) {
            formatMap.putIfAbsent(t, new FiTimeFormat(this, t));
        }
        return formatMap.get(t);
    }

    @Override
    protected Object[][] getContents() {
        return contents;
    }


    private static class FiTimeFormat extends SimpleTimeFormat {
        private final ResourceBundle bundle;
        private String pastName = "";
        private String futureName = "";
        private String pastPluralName = "";
        private String futurePluralName = "";
        private String pluralPattern = "";


        public FiTimeFormat(final ResourceBundle rb, final TimeUnit unit) {
            super();
            this.bundle = rb;

            if (bundle.containsKey(getUnitName(unit) + "PastSingularName")) {
                this.setPastName(bundle.getString(getUnitName(unit) + "PastSingularName"))
                        .setFutureName(bundle.getString(getUnitName(unit) + "FutureSingularName"))
                        .setPastPluralName(bundle.getString(getUnitName(unit) + "PastSingularName"))
                        .setFuturePluralName(bundle.getString(getUnitName(unit) + "FutureSingularName"))
                        .setPluralPattern(bundle.getString(getUnitName(unit) + "Pattern"));

                if (bundle.containsKey(getUnitName(unit) + "PastPluralName")) {
                    this.setPastPluralName(bundle.getString(getUnitName(unit) + "PastPluralName"));
                }

                if (bundle.containsKey(getUnitName(unit) + "FuturePluralName")) {
                    this.setFuturePluralName(bundle.getString(getUnitName(unit) + "FuturePluralName"));
                }

                if (bundle.containsKey(getUnitName(unit) + "PluralPattern")) {
                    this.setPluralPattern(bundle.getString(getUnitName(unit) + "PluralPattern"));
                }

                this.setPattern(bundle.getString(getUnitName(unit) + "Pattern"))
                        .setPastSuffix(bundle.getString(getUnitName(unit) + "PastSuffix"))
                        .setFutureSuffix(bundle.getString(getUnitName(unit) + "FutureSuffix"))
                        .setFuturePrefix("")
                        .setPastPrefix("")
                        .setSingularName("")
                        .setPluralName("");
            }
        }

        public String getPastName() {
            return pastName;
        }

        public FiTimeFormat setPastName(String pastName) {
            this.pastName = pastName;
            return this;
        }

        public String getFutureName() {
            return futureName;
        }

        public FiTimeFormat setFutureName(String futureName) {
            this.futureName = futureName;
            return this;
        }

        public String getPastPluralName() {
            return pastPluralName;
        }

        public FiTimeFormat setPastPluralName(String pastName) {
            this.pastPluralName = pastName;
            return this;
        }

        public String getFuturePluralName() {
            return futurePluralName;
        }

        public FiTimeFormat setFuturePluralName(String futureName) {
            this.futurePluralName = futureName;
            return this;
        }

        public String getPluralPattern() {
            return pluralPattern;
        }

        public FiTimeFormat setPluralPattern(String pattern) {
            this.pluralPattern = pattern;
            return this;
        }

        @Override
        protected String getGramaticallyCorrectName(TimeUnitQuantity d, boolean round) {
            String result = d.isInPast() ? getPastName() : getFutureName();
            if ((Math.abs(getQuantity(d, round)) == 0) || (Math.abs(getQuantity(d, round)) > 1)) {
                result = d.isInPast() ? getPastPluralName() : getFuturePluralName();
            }
            return result;
        }

        @Override
        protected String getPattern(long quantity) {
            if (Math.abs(quantity) == 1) {
                return getPattern();
            }
            return getPluralPattern();

        }

        @Override
        public String decorate(TimeUnitQuantity timeUnitQuantity, String time) {
            String result;
            if (timeUnitQuantity.getUnit() instanceof Day && Math.abs(timeUnitQuantity.getQuantity()) == 1) {
                result = time;
            } else {
                result = super.decorate(timeUnitQuantity, time);
            }
            return result;
        }

        private String getUnitName(TimeUnit unit) {
            return unit.getClass().getSimpleName();
        }

    }
}
