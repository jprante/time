package org.xbib.time.pretty.units;

import org.xbib.time.pretty.TimeUnit;

import java.util.Comparator;

/**
 *
 */
public class TimeUnitComparator implements Comparator<TimeUnit> {

    public int compare(final TimeUnit left, final TimeUnit right) {
        if (left.getMillisPerUnit() < right.getMillisPerUnit()) {
            return -1;
        } else if (left.getMillisPerUnit() > right.getMillisPerUnit()) {
            return 1;
        }
        return 0;
    }
}
