package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Handles a separator, that splits the fields into multiple parts.
 * For example, the 'T' in the ISO8601 standard.
 */
public class Separator implements PeriodPrinter, PeriodParser {

    private final String iText;

    private final String iFinalText;

    private final String[] iParsedForms;

    private final boolean iUseBefore;

    private final boolean iUseAfter;

    private final PeriodPrinter iBeforePrinter;

    private final PeriodParser iBeforeParser;

    protected volatile PeriodPrinter iAfterPrinter;

    protected volatile PeriodParser iAfterParser;

    public Separator(String text, String finalText, String[] variants,
                     PeriodPrinter beforePrinter, PeriodParser beforeParser,
                     boolean useBefore, boolean useAfter) {
        iText = text;
        iFinalText = finalText;
        if ((finalText == null || text.equals(finalText)) &&
                (variants == null || variants.length == 0)) {

            iParsedForms = new String[]{text};
        } else {
            TreeSet<String> parsedSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            parsedSet.add(text);
            parsedSet.add(finalText);
            if (variants != null) {
                for (int i = variants.length; --i >= 0; ) {
                    parsedSet.add(variants[i]);
                }
            }
            ArrayList<String> parsedList = new ArrayList<String>(parsedSet);
            Collections.reverse(parsedList);
            iParsedForms = parsedList.toArray(new String[0]);
        }
        iBeforePrinter = beforePrinter;
        iBeforeParser = beforeParser;
        iUseBefore = useBefore;
        iUseAfter = useAfter;
    }

    public int countFieldsToPrint(Period period, int stopAt, Locale locale) {
        int sum = iBeforePrinter.countFieldsToPrint(period, stopAt, locale);
        if (sum < stopAt) {
            sum += iAfterPrinter.countFieldsToPrint(period, stopAt, locale);
        }
        return sum;
    }

    public int calculatePrintedLength(Period period, Locale locale) {
        PeriodPrinter before = iBeforePrinter;
        PeriodPrinter after = iAfterPrinter;
        int sum = before.calculatePrintedLength(period, locale)
                + after.calculatePrintedLength(period, locale);
        if (iUseBefore) {
            if (before.countFieldsToPrint(period, 1, locale) > 0) {
                if (iUseAfter) {
                    int afterCount = after.countFieldsToPrint(period, 2, locale);
                    if (afterCount > 0) {
                        sum += (afterCount > 1 ? iText : iFinalText).length();
                    }
                } else {
                    sum += iText.length();
                }
            }
        } else if (iUseAfter && after.countFieldsToPrint(period, 1, locale) > 0) {
            sum += iText.length();
        }
        return sum;
    }

    public void printTo(StringBuilder buf, Period period, Locale locale) throws IOException {
        PeriodPrinter before = iBeforePrinter;
        PeriodPrinter after = iAfterPrinter;
        before.printTo(buf, period, locale);
        if (iUseBefore) {
            if (before.countFieldsToPrint(period, 1, locale) > 0) {
                if (iUseAfter) {
                    int afterCount = after.countFieldsToPrint(period, 2, locale);
                    if (afterCount > 0) {
                        buf.append(afterCount > 1 ? iText : iFinalText);
                    }
                } else {
                    buf.append(iText);
                }
            }
        } else if (iUseAfter && after.countFieldsToPrint(period, 1, locale) > 0) {
            buf.append(iText);
        }
        after.printTo(buf, period, locale);
    }

    public void printTo(Writer out, Period period, Locale locale) throws IOException {
        PeriodPrinter before = iBeforePrinter;
        PeriodPrinter after = iAfterPrinter;
        before.printTo(out, period, locale);
        if (iUseBefore) {
            if (before.countFieldsToPrint(period, 1, locale) > 0) {
                if (iUseAfter) {
                    int afterCount = after.countFieldsToPrint(period, 2, locale);
                    if (afterCount > 0) {
                        out.write(afterCount > 1 ? iText : iFinalText);
                    }
                } else {
                    out.write(iText);
                }
            }
        } else if (iUseAfter && after.countFieldsToPrint(period, 1, locale) > 0) {
            out.write(iText);
        }
        after.printTo(out, period, locale);
    }

    public int parseInto(PeriodAmount period, String periodStr, int pos, Locale locale) {
        int position = pos;
        int oldPos = position;
        position = iBeforeParser.parseInto(period, periodStr, position, locale);
        if (position < 0) {
            return position;
        }
        boolean found = false;
        int parsedFormLength = -1;
        if (position > oldPos) {
            for (String parsedForm : iParsedForms) {
                if ((parsedForm == null || parsedForm.length() == 0) ||
                        periodStr.regionMatches(true, position, parsedForm, 0, parsedForm.length())) {
                    parsedFormLength = (parsedForm == null ? 0 : parsedForm.length());
                    position += parsedFormLength;
                    found = true;
                    break;
                }
            }
        }
        oldPos = position;
        position = iAfterParser.parseInto(period, periodStr, position, locale);
        if (position < 0) {
            return position;
        }
        if (found && position == oldPos && parsedFormLength > 0) {
            // Separator should not have been supplied.
            return ~oldPos;
        }
        if (position > oldPos && !found && !iUseBefore) {
            // Separator was required.
            return ~oldPos;
        }
        return position;
    }

    protected Separator finish(PeriodPrinter afterPrinter, PeriodParser afterParser) {
        iAfterPrinter = afterPrinter;
        iAfterParser = afterParser;
        return this;
    }
}
