package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Formats the numeric value of a field, potentially with prefix/suffix.
 */
class FieldFormatter implements PeriodPrinter, PeriodParser {
    private final int iMinPrintedDigits;
    //private final int iPrintZeroSetting;
    private final int iMaxParsedDigits;
    private final boolean iRejectSignedValues;

    /**
     * The index of the field type, 0=year, etc.
     */
    private final ChronoUnit unit;
    /**
     * The array of the latest formatter added for each type.
     * This is shared between all the field formatters in a formatter.
     */
    //private final FieldFormatter[] iFieldFormatters;

    private final PeriodFieldAffix iPrefix;
    private final PeriodFieldAffix iSuffix;

    FieldFormatter(int minPrintedDigits,
                   int maxParsedDigits, boolean rejectSignedValues,
                   ChronoUnit chronoUnit,
                   PeriodFieldAffix prefix, PeriodFieldAffix suffix) {
        iMinPrintedDigits = minPrintedDigits;
        iMaxParsedDigits = maxParsedDigits;
        iRejectSignedValues = rejectSignedValues;
        this.unit = chronoUnit;
        iPrefix = prefix;
        iSuffix = suffix;
    }

    FieldFormatter(FieldFormatter field, PeriodFieldAffix periodFieldAffix) {
        PeriodFieldAffix suffix = periodFieldAffix;
        iMinPrintedDigits = field.iMinPrintedDigits;
        iMaxParsedDigits = field.iMaxParsedDigits;
        iRejectSignedValues = field.iRejectSignedValues;
        this.unit = field.unit;
        iPrefix = field.iPrefix;
        if (field.iSuffix != null) {
            suffix = new CompositeAffix(field.iSuffix, suffix);
        }
        iSuffix = suffix;
    }

    public void finish(FieldFormatter[] fieldFormatters) {
        Set<PeriodFieldAffix> prefixesToIgnore = new HashSet<>();
        Set<PeriodFieldAffix> suffixesToIgnore = new HashSet<>();
        for (FieldFormatter fieldFormatter : fieldFormatters) {
            if (fieldFormatter != null && !this.equals(fieldFormatter)) {
                prefixesToIgnore.add(fieldFormatter.iPrefix);
                suffixesToIgnore.add(fieldFormatter.iSuffix);
            }
        }
        // if we have a prefix then allow ignore behaviour
        if (iPrefix != null) {
            iPrefix.finish(prefixesToIgnore);
        }
        // if we have a suffix then allow ignore behaviour
        if (iSuffix != null) {
            iSuffix.finish(suffixesToIgnore);
        }
    }

    public int countFieldsToPrint(Period period, int stopAt, Locale locale) {
        if (stopAt <= 0) {
            return 0;
        }
        if (getFieldValue(period) != Long.MAX_VALUE) {
            return 1;
        }
        return 0;
    }

    public int calculatePrintedLength(Period period, Locale locale) {
        long valueLong = getFieldValue(period);
        if (valueLong == Long.MAX_VALUE) {
            return 0;
        }

        int sum = Math.max(FormatUtils.calculateDigitCount(valueLong), iMinPrintedDigits);
        int value = (int) valueLong;

        if (iPrefix != null) {
            sum += iPrefix.calculatePrintedLength(value);
        }
        if (iSuffix != null) {
            sum += iSuffix.calculatePrintedLength(value);
        }

        return sum;
    }

    public void printTo(StringBuilder buf, Period period, Locale locale) {
        long valueLong = getFieldValue(period);
        if (valueLong == Long.MAX_VALUE) {
            return;
        }
        int value = (int) valueLong;
        if (iPrefix != null) {
            iPrefix.printTo(buf, value);
        }
        int minDigits = iMinPrintedDigits;
        if (minDigits <= 1) {
            FormatUtils.appendUnpaddedInteger(buf, value);
        } else {
            FormatUtils.appendPaddedInteger(buf, value, minDigits);
        }
        if (iSuffix != null) {
            iSuffix.printTo(buf, value);
        }
    }

    public void printTo(Writer out, Period period, Locale locale) throws IOException {
        long valueLong = getFieldValue(period);
        if (valueLong == Long.MAX_VALUE) {
            return;
        }
        int value = (int) valueLong;
        if (iPrefix != null) {
            iPrefix.printTo(out, value);
        }
        int minDigits = iMinPrintedDigits;
        if (minDigits <= 1) {
            FormatUtils.writeUnpaddedInteger(out, value);
        } else {
            FormatUtils.writePaddedInteger(out, value, minDigits);
        }
        if (iSuffix != null) {
            iSuffix.printTo(out, value);
        }
    }

    public int parseInto(PeriodAmount period, String text, int pos, Locale locale) {
        int position = pos;
        if (position >= text.length()) {
            return ~position;
        }

        if (iPrefix != null) {
            position = iPrefix.parse(text, position);
            if (position < 0) {
                return position;
            }
        }
        int limit;
        limit = Math.min(iMaxParsedDigits, text.length() - position);
        int length = 0;
        boolean hasDigits = false;
        boolean negative;
        while (length < limit) {
            char c = text.charAt(position + length);
            if (length == 0 && (c == '-' || c == '+') && !iRejectSignedValues) {
                negative = c == '-';
                if (length + 1 >= limit ||
                        (c = text.charAt(position + length + 1)) < '0' || c > '9') {
                    break;
                }
                if (negative) {
                    length++;
                } else {
                    position++;
                }
                limit = Math.min(limit + 1, text.length() - position);
                continue;
            }
            if (c >= '0' && c <= '9') {
                hasDigits = true;
            } else {
                break;
            }
            length++;
        }

        if (!hasDigits) {
            return ~position;
        }
        setFieldValue(period, unit, parseInt(text, position, length));
        position += length;
        if (position >= 0 && iSuffix != null) {
            position = iSuffix.parse(text, position);
        }
        return position;
    }

    /**
     * @param text     text to parse
     * @param pos position in text
     * @param len   exact count of characters to parse
     * @return parsed int value
     */
    private int parseInt(String text, int pos, int len) {
        int position = pos;
        int length = len;
        if (length >= 10) {
            return Integer.parseInt(text.substring(position, position + length));
        }
        if (length <= 0) {
            return 0;
        }
        int value = text.charAt(position++);
        length--;
        boolean negative;
        if (value == '-') {
            if (--length < 0) {
                return 0;
            }
            negative = true;
            value = text.charAt(position++);
        } else {
            negative = false;
        }
        value -= '0';
        while (length-- > 0) {
            value = ((value << 3) + (value << 1)) + text.charAt(position++) - '0';
        }
        return negative ? -value : value;
    }

    /**
     * @return Long.MAX_VALUE if nothing to print, otherwise value
     */
    long getFieldValue(Period period) {
        long value;
        switch (unit) {
            default:
                return Long.MAX_VALUE;
            case YEARS:
                value = period.get(ChronoUnit.YEARS);
                break;
            case MONTHS:
                value = period.get(ChronoUnit.MONTHS);
                break;
            case WEEKS:
                value = period.get(ChronoUnit.WEEKS);
                break;
            case DAYS:
                value = period.get(ChronoUnit.DAYS);
                break;
            case HOURS:
                value = period.get(ChronoUnit.HOURS);
                break;
            case MINUTES:
                value = period.get(ChronoUnit.MINUTES);
                break;
            case SECONDS:
                value = period.get(ChronoUnit.SECONDS);
                break;
            case MILLIS:
                value = period.get(ChronoUnit.MILLIS);
                break;
        }

        return value;
    }

    void setFieldValue(PeriodAmount period, ChronoUnit field, long value) {
        switch (field) {
            default:
                break;
            case YEARS:
                period.set(ChronoUnit.YEARS, value);
                break;
            case MONTHS:
                period.set(ChronoUnit.MONTHS, value);
                break;
            case WEEKS:
                period.set(ChronoUnit.WEEKS, value);
                break;
            case DAYS:
                period.set(ChronoUnit.DAYS, value);
                break;
            case HOURS:
                period.set(ChronoUnit.HOURS, value);
                break;
            case MINUTES:
                period.set(ChronoUnit.MINUTES, value);
                break;
            case SECONDS:
                period.set(ChronoUnit.SECONDS, value);
                break;
            case MILLIS:
                period.set(ChronoUnit.MILLIS, value);
                break;
        }
    }

    ChronoUnit getFieldType() {
        return unit;
    }
}
