package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * Implements an affix where the text varies by the amount of the field.
 * Different amounts are supported based on the provided parameters.
 */
class RegExAffix extends IgnorableAffix {

    private static final ConcurrentMap<String, Pattern> PATTERNS = new ConcurrentHashMap<>();

    private static final Comparator<String> LENGTH_DESC_COMPARATOR = (o1, o2) -> o2.length() - o1.length();

    private final String[] iSuffixes;
    private final Pattern[] iPatterns;

    // The parse method has to iterate over the suffixes from the longest one to the shortest one
    // Otherwise it might consume not enough characters.
    private final String[] iSuffixesSortedDescByLength;

    RegExAffix(String[] regExes, String[] texts) {
        iSuffixes = texts.clone();
        iPatterns = new Pattern[regExes.length];
        for (int i = 0; i < regExes.length; i++) {
            Pattern pattern = PATTERNS.get(regExes[i]);
            if (pattern == null) {
                pattern = Pattern.compile(regExes[i]);
                Pattern p = PATTERNS.putIfAbsent(regExes[i], pattern);
            }
            iPatterns[i] = pattern;
        }
        iSuffixesSortedDescByLength = iSuffixes.clone();
        Arrays.sort(iSuffixesSortedDescByLength, LENGTH_DESC_COMPARATOR);
    }

    private int selectSuffixIndex(int value) {
        String valueString = String.valueOf(value);
        for (int i = 0; i < iPatterns.length; i++) {
            if (iPatterns[i].matcher(valueString).matches()) {
                return i;
            }
        }
        return iPatterns.length - 1;
    }

    @Override
    public int calculatePrintedLength(int value) {
        return iSuffixes[selectSuffixIndex(value)].length();
    }

    @Override
    public void printTo(StringBuilder buf, int value) {
        buf.append(iSuffixes[selectSuffixIndex(value)]);
    }

    @Override
    public void printTo(Writer out, int value) throws IOException {
        out.write(iSuffixes[selectSuffixIndex(value)]);
    }

    @Override
    public int parse(String periodStr, int position) {
        for (String text : iSuffixesSortedDescByLength) {
            if (periodStr.regionMatches(true, position, text, 0, text.length()) &&
                    !matchesOtherAffix(text.length(), periodStr, position)) {
                return position + text.length();
            }
        }
        return ~position;
    }

    @Override
    public int scan(String periodStr, final int position) {
        int sourceLength = periodStr.length();
        for (int pos = position; pos < sourceLength; pos++) {
            for (String text : iSuffixesSortedDescByLength) {
                if (periodStr.regionMatches(true, pos, text, 0, text.length()) &&
                        !matchesOtherAffix(text.length(), periodStr, pos)) {
                    return pos;
                }
            }
        }
        return ~position;
    }

    @Override
    public String[] getAffixes() {
        return iSuffixes.clone();
    }
}
