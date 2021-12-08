package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;

/**
 * Implements an affix where the text does not vary by the amount.
 */
public class SimpleAffix extends IgnorableAffix {

    private final String iText;

    public SimpleAffix(String text) {
        iText = text;
    }

    @Override
    public int calculatePrintedLength(int value) {
        return iText.length();
    }

    @Override
    public void printTo(StringBuilder buf, int value) {
        buf.append(iText);
    }

    @Override
    public void printTo(Writer out, int value) throws IOException {
        out.write(iText);
    }

    @Override
    public int parse(String periodStr, int position) {
        String text = iText;
        int textLength = text.length();
        if (periodStr.regionMatches(true, position, text, 0, textLength) &&
                !matchesOtherAffix(textLength, periodStr, position)) {
            return position + textLength;
        }
        return ~position;
    }

    @Override
    public int scan(String periodStr, final int position) {
        String text = iText;
        int textLength = text.length();
        int sourceLength = periodStr.length();
        search:
        for (int pos = position; pos < sourceLength; pos++) {
            if (periodStr.regionMatches(true, pos, text, 0, textLength) &&
                    !matchesOtherAffix(textLength, periodStr, pos)) {
                return pos;
            }
            // Only allow number characters to be skipped in search of suffix.
            switch (periodStr.charAt(pos)) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '.':
                case ',':
                case '+':
                case '-':
                    break;
                default:
                    break search;
            }
        }
        return ~position;
    }

    @Override
    public String[] getAffixes() {
        return new String[]{iText};
    }
}
