package org.xbib.time.schedule;

import java.util.NavigableSet;

public interface TimeField {

    boolean contains(int number);

    NavigableSet<Integer> getNumbers();

    boolean isFullRange();
}
