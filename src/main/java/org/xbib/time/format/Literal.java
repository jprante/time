package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;
import java.time.Period;
import java.util.Locale;

/**
 * Handles a simple literal piece of text.
 */
public class Literal implements PeriodPrinter, PeriodParser {

    public static final Literal EMPTY = new Literal("");

    private final String iText;

    public Literal(String text) {
        iText = text;
    }

    public int countFieldsToPrint(Period period, int stopAt, Locale locale) {
        return 0;
    }

    public int calculatePrintedLength(Period period, Locale locale) {
        return iText.length();
    }

    public void printTo(StringBuilder buf, Period period, Locale locale) {
        buf.append(iText);
    }

    public void printTo(Writer out, Period period, Locale locale) throws IOException {
        out.write(iText);
    }

    public int parseInto(PeriodAmount period, String periodStr,
                         int position, Locale locale) {
        if (periodStr.regionMatches(true, position, iText, 0, iText.length())) {
            return position + iText.length();
        }
        return ~position;
    }
}
