package org.xbib.time.format;

import java.util.HashSet;
import java.util.Set;

/**
 * An affix that can be ignored.
 */
public abstract class IgnorableAffix implements PeriodFieldAffix {

    private volatile String[] iOtherAffixes;

    public void finish(Set<PeriodFieldAffix> periodFieldAffixesToIgnore) {
        if (iOtherAffixes == null) {
            // Calculate the shortest affix in this instance.
            int shortestAffixLength = Integer.MAX_VALUE;
            String shortestAffix = null;
            for (String affix : getAffixes()) {
                if (affix.length() < shortestAffixLength) {
                    shortestAffixLength = affix.length();
                    shortestAffix = affix;
                }
            }
            // Pick only affixes that are longer than the shortest affix in this instance.
            // This will reduce the number of parse operations and thus speed up the PeriodFormatter.
            // also need to pick affixes that differ only in case (but not those that are identical)
            Set<String> affixesToIgnore = new HashSet<String>();
            for (PeriodFieldAffix periodFieldAffixToIgnore : periodFieldAffixesToIgnore) {
                if (periodFieldAffixToIgnore != null) {
                    for (String affixToIgnore : periodFieldAffixToIgnore.getAffixes()) {
                        if (affixToIgnore.length() > shortestAffixLength ||
                                (affixToIgnore.equalsIgnoreCase(shortestAffix) &&
                                        !affixToIgnore.equals(shortestAffix))) {
                            affixesToIgnore.add(affixToIgnore);
                        }
                    }
                }
            }
            iOtherAffixes = affixesToIgnore.toArray(new String[affixesToIgnore.size()]);
        }
    }

    /**
     * Checks if there is a match among the other affixes (stored internally)
     * that is longer than the passed value (textLength).
     *
     * @param textLength the length of the match
     * @param periodStr  the Period string that will be parsed
     * @param position   the position in the Period string at which the parsing should be started.
     * @return true if the other affixes (stored internally) contain a match
     * that is longer than the textLength parameter, false otherwise
     */
    protected boolean matchesOtherAffix(int textLength, String periodStr, int position) {
        if (iOtherAffixes != null) {
            // ignore case when affix length differs
            // match case when affix length is same
            for (String affixToIgnore : iOtherAffixes) {
                int textToIgnoreLength = affixToIgnore.length();
                if ((textLength < textToIgnoreLength &&
                        periodStr.regionMatches(true, position, affixToIgnore, 0, textToIgnoreLength)) ||
                        (textLength == textToIgnoreLength &&
                                periodStr.regionMatches(false, position, affixToIgnore, 0, textToIgnoreLength))) {
                    return true;
                }
            }
        }
        return false;
    }
}
