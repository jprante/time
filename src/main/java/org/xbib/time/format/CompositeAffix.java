package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/**
 * Builds a composite affix by merging two other affix implementations.
 */
class CompositeAffix extends IgnorableAffix {
    private final PeriodFieldAffix iLeft;
    private final PeriodFieldAffix iRight;
    private final String[] iLeftRightCombinations;

    CompositeAffix(PeriodFieldAffix left, PeriodFieldAffix right) {
        iLeft = left;
        iRight = right;

        // We need to construct all possible combinations of left and right.
        // We are doing it once in constructor so that getAffixes() is quicker.
        Set<String> result = new HashSet<String>();
        for (String leftText : iLeft.getAffixes()) {
            for (String rightText : iRight.getAffixes()) {
                result.add(leftText + rightText);
            }
        }
        iLeftRightCombinations = result.toArray(new String[result.size()]);
    }

    @Override
    public int calculatePrintedLength(int value) {
        return iLeft.calculatePrintedLength(value)
                + iRight.calculatePrintedLength(value);
    }

    @Override
    public void printTo(StringBuilder buf, int value) {
        iLeft.printTo(buf, value);
        iRight.printTo(buf, value);
    }

    @Override
    public void printTo(Writer out, int value) throws IOException {
        iLeft.printTo(out, value);
        iRight.printTo(out, value);
    }

    @Override
    public int parse(String periodStr, int position) {
        int pos = iLeft.parse(periodStr, position);
        if (pos >= 0) {
            pos = iRight.parse(periodStr, pos);
            if (pos >= 0 && matchesOtherAffix(parse(periodStr, pos) - pos, periodStr, position)) {
                return ~position;
            }
        }
        return pos;
    }

    @Override
    public int scan(String periodStr, final int position) {
        int leftPosition = iLeft.scan(periodStr, position);
        if (leftPosition >= 0) {
            int rightPosition = iRight.scan(periodStr, iLeft.parse(periodStr, leftPosition));
            if (!(rightPosition >= 0 && matchesOtherAffix(iRight.parse(periodStr, rightPosition) -
                    leftPosition, periodStr, position))) {
                if (leftPosition > 0) {
                    return leftPosition;
                } else {
                    return rightPosition;
                }
            }
        }
        return ~position;
    }

    @Override
    public String[] getAffixes() {
        return iLeftRightCombinations.clone();
    }
}
