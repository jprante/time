package org.xbib.time.schedule;

import java.util.NavigableSet;

public enum MatchAllField implements TimeField {

    instance;

    @Override
    public boolean contains(int number) {
        return true;
    }

    @Override
    public NavigableSet<Integer> getNumbers() {
        return null;
    }

    @Override
    public boolean isFullRange() {
        return true;
    }
}
