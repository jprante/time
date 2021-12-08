package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * Defines a formatted field's prefix or suffix text.
 * This can be used for fields such as 'n hours' or 'nH' or 'Hour:n'.
 */
public interface PeriodFieldAffix {

    int calculatePrintedLength(int value);

    void printTo(StringBuilder buf, int value);

    void printTo(Writer out, int value) throws IOException;

    /**
     * @param periodStr period string
     * @param position position
     * @return new position after parsing affix, or ~position of failure
     */
    int parse(String periodStr, int position);

    /**
     * @param periodStr period string
     * @param position position
     * @return position where affix starts, or original ~position if not found
     */
    int scan(String periodStr, int position);

    /**
     * @return a copy of array of affixes
     */
    String[] getAffixes();

    /**
     * This method should be called only once.
     * After first call consecutive calls to this methods will have no effect.
     * Causes this affix to ignore a match (parse and scan
     * methods) if there is an affix in the passed list that holds
     * affix text which satisfy both following conditions:
     * - the affix text is also a match
     * - the affix text is longer than the match from this object
     *
     * @param affixesToIgnore affixes to ignore
     */
    void finish(Set<PeriodFieldAffix> affixesToIgnore);
}
