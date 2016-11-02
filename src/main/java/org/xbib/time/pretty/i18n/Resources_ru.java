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
public class Resources_ru extends ListResourceBundle implements TimeFormatProvider {
    private static final Object[][] OBJECTS = new Object[0][0];

    private static final int tolerance = 50;

    // see http://translate.sourceforge.net/wiki/l10n/pluralforms
    private static final int russianPluralForms = 3;

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
                        return "сейчас";
                    }
                    if (timeUnitQuantity.isInPast()) {
                        return "только что";
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
            return new TimeFormatAided("век", "века", "веков");
        } else if (t instanceof Day) {
            return new TimeFormatAided("день", "дня", "дней");
        } else if (t instanceof Decade) {
            return new TimeFormatAided("десятилетие", "десятилетия", "десятилетий");
        } else if (t instanceof Hour) {
            return new TimeFormatAided("час", "часа", "часов");
        } else if (t instanceof Millennium) {
            return new TimeFormatAided("тысячелетие", "тысячелетия", "тысячелетий");
        } else if (t instanceof Millisecond) {
            return new TimeFormatAided("миллисекунду", "миллисекунды", "миллисекунд");
        } else if (t instanceof Minute) {
            return new TimeFormatAided("минуту", "минуты", "минут");
        } else if (t instanceof Month) {
            return new TimeFormatAided("месяц", "месяца", "месяцев");
        } else if (t instanceof Second) {
            return new TimeFormatAided("секунду", "секунды", "секунд");
        } else if (t instanceof Week) {
            return new TimeFormatAided("неделю", "недели", "недель");
        } else if (t instanceof Year) {
            return new TimeFormatAided("год", "года", "лет");
        }
        return null; // error
    }

    private static class TimeFormatAided implements TimeFormat {
        private final String[] pluarls;

        public TimeFormatAided(String... plurals) {
            if (plurals.length != russianPluralForms) {
                throw new IllegalArgumentException("Wrong plural forms number for russian language!");
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
                result.append(" назад");
            }

            return result.toString();
        }
    }
}
