package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;

/**
 * Implements an affix where the text varies by the amount of the field.
 * Only singular (1) and plural (not 1) are supported.
 */
class PluralAffix extends IgnorableAffix {
    private final String iSingularText;
    private final String iPluralText;

    PluralAffix(String singularText, String pluralText) {
        iSingularText = singularText;
        iPluralText = pluralText;
    }

    public int calculatePrintedLength(int value) {
        return (value == 1 ? iSingularText : iPluralText).length();
    }

    public void printTo(StringBuilder buf, int value) {
        buf.append(value == 1 ? iSingularText : iPluralText);
    }

    public void printTo(Writer out, int value) throws IOException {
        out.write(value == 1 ? iSingularText : iPluralText);
    }

    public int parse(String periodStr, int position) {
        String text1 = iPluralText;
        String text2 = iSingularText;
        if (text1.length() < text2.length()) {
            // Swap in order to match longer one first.
            String temp = text1;
            text1 = text2;
            text2 = temp;
        }
        if (periodStr.regionMatches(true, position, text1, 0, text1.length()) &&
                !matchesOtherAffix(text1.length(), periodStr, position)) {
            return position + text1.length();
        }
        if (periodStr.regionMatches(true, position, text2, 0, text2.length()) &&
                !matchesOtherAffix(text2.length(), periodStr, position)) {
            return position + text2.length();
        }
        return ~position;
    }

    public int scan(String periodStr, final int position) {
        String text1 = iPluralText;
        String text2 = iSingularText;

        if (text1.length() < text2.length()) {
            // Swap in order to match longer one first.
            String temp = text1;
            text1 = text2;
            text2 = temp;
        }

        int textLength1 = text1.length();
        int textLength2 = text2.length();

        int sourceLength = periodStr.length();
        for (int pos = position; pos < sourceLength; pos++) {
            if (periodStr.regionMatches(true, pos, text1, 0, textLength1) &&
                    !matchesOtherAffix(text1.length(), periodStr, pos)) {
                return pos;
            }
            if (periodStr.regionMatches(true, pos, text2, 0, textLength2) &&
                    !matchesOtherAffix(text2.length(), periodStr, pos)) {
                return pos;
            }
        }
        return ~position;
    }

    public String[] getAffixes() {
        return new String[]{iSingularText, iPluralText};
    }
}
