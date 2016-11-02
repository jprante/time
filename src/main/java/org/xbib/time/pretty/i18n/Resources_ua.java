package org.xbib.time.pretty.i18n;

import org.xbib.time.pretty.TimeFormat;
import org.xbib.time.pretty.TimeFormatProvider;
import org.xbib.time.pretty.TimeUnit;
import org.xbib.time.pretty.TimeUnitQuantity;
import org.xbib.time.pretty.units.Century;
import org.xbib.time.pretty.units.Day;
import org.xbib.time.pretty.units.Decade;
import org.xbib.time.pretty.units.Hour;
import org.xbib.time.pretty.units.JustNow;
import org.xbib.time.pretty.units.Millennium;
import org.xbib.time.pretty.units.Millisecond;
import org.xbib.time.pretty.units.Minute;
import org.xbib.time.pretty.units.Month;
import org.xbib.time.pretty.units.Second;
import org.xbib.time.pretty.units.Week;
import org.xbib.time.pretty.units.Year;

import java.util.ListResourceBundle;

/**
 *
 */
public class Resources_ua extends ListResourceBundle implements TimeFormatProvider {
    private static final Object[][] OBJECTS = new Object[0][0];

    private static final int tolerance = 50;

    private static final int slavicPluralForms = 3;

    @Override
    public Object[][] getContents() {
        return OBJECTS;
    }

    @Override
    public TimeFormat getFormatFor(TimeUnit t) {
        if (t instanceof JustNow) {
            return new TimeFormat() {
                @Override
                public String format(TimeUnitQuantity timeUnitQuantity) {
                    return performFormat(timeUnitQuantity);
                }

                @Override
                public String formatUnrounded(TimeUnitQuantity timeUnitQuantity) {
                    return performFormat(timeUnitQuantity);
                }

                private String performFormat(TimeUnitQuantity timeUnitQuantity) {
                    if (timeUnitQuantity.isInFuture()) {
                        return "зараз";
                    }
                    if (timeUnitQuantity.isInPast()) {
                        return "тільки що";
                    }
                    return null;
                }

                @Override
                public String decorate(TimeUnitQuantity timeUnitQuantity, String time) {
                    return time;
                }

                @Override
                public String decorateUnrounded(TimeUnitQuantity timeUnitQuantity, String time) {
                    return time;
                }
            };
        } else if (t instanceof Century) {
            return new TimeFormatAided("століття", "століття", "столітть");
        } else if (t instanceof Day) {
            return new TimeFormatAided("день", "дні", "днів");
        } else if (t instanceof Decade) {
            return new TimeFormatAided("десятиліття", "десятиліття", "десятиліть");
        } else if (t instanceof Hour) {
            return new TimeFormatAided("годину", "години", "годин");
        } else if (t instanceof Millennium) {
            return new TimeFormatAided("тисячоліття", "тисячоліття", "тисячоліть");
        } else if (t instanceof Millisecond) {
            return new TimeFormatAided("мілісекунду", "мілісекунди", "мілісекунд");
        } else if (t instanceof Minute) {
            return new TimeFormatAided("хвилину", "хвилини", "хвилин");
        } else if (t instanceof Month) {
            return new TimeFormatAided("місяць", "місяці", "місяців");
        } else if (t instanceof Second) {
            return new TimeFormatAided("секунду", "секунди", "секунд");
        } else if (t instanceof Week) {
            return new TimeFormatAided("тиждень", "тижні", "тижнів");
        } else if (t instanceof Year) {
            return new TimeFormatAided("рік", "роки", "років");
        }
        return null;
    }

    private static class TimeFormatAided implements TimeFormat {
        private final String[] pluarls;

        public TimeFormatAided(String... plurals) {
            if (plurals.length != slavicPluralForms) {
                throw new IllegalArgumentException("Wrong plural forms number for slavic language!");
            }
            this.pluarls = plurals;
        }

        @Override
        public String format(TimeUnitQuantity timeUnitQuantity) {
            long quantity = timeUnitQuantity.getQuantityRounded(tolerance);
            return String.valueOf(quantity);
        }

        @Override
        public String formatUnrounded(TimeUnitQuantity timeUnitQuantity) {
            long quantity = timeUnitQuantity.getQuantity();
            return String.valueOf(quantity);
        }

        @Override
        public String decorate(TimeUnitQuantity timeUnitQuantity, String time) {
            return performDecoration(
                    timeUnitQuantity.isInPast(),
                    timeUnitQuantity.isInFuture(),
                    timeUnitQuantity.getQuantityRounded(tolerance),
                    time
            );
        }

        @Override
        public String decorateUnrounded(TimeUnitQuantity timeUnitQuantity, String time) {
            return performDecoration(
                    timeUnitQuantity.isInPast(),
                    timeUnitQuantity.isInFuture(),
                    timeUnitQuantity.getQuantity(),
                    time
            );
        }

        private String performDecoration(boolean past, boolean future, long n, String time) {
            // a bit cryptic, yet well-tested
            // consider http://translate.sourceforge.net/wiki/l10n/pluralforms
            int pluralIdx = (n % 10 == 1 && n % 100 != 11 ? 0 : n % 10 >= 2 && n % 10 <= 4 &&
                    (n % 100 < 10 || n % 100 >= 20) ? 1 : 2);

            StringBuilder result = new StringBuilder();

            if (future) {
                result.append("через ");
            }

            result.append(time);
            result.append(' ');
            result.append(pluarls[pluralIdx]);

            if (past) {
                result.append(" тому");
            }

            return result.toString();
        }
    }
}
