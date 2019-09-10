package org.xbib.time.schedule;

import com.google.common.collect.ForwardingSet;
import java.util.HashSet;
import java.util.Set;

public class Integers extends ForwardingSet<Integer> {
    private final Set<Integer> delegate;

    public Integers(int... integers) {
        delegate = new HashSet<>();
        with(integers);
    }

    @Override
    protected Set<Integer> delegate() {
        return delegate;
    }

    public Integers with(int... integers) {
        for (int integer : integers) {
            add(integer);
        }
        return this;
    }

    public Integers withRange(int start, int end) {
        for (int i = start; i <= end; i++) {
            add(i);
        }
        return this;
    }

    public Integers withRange(int start, int end, int mod) {
        for (int i = start; i <= end; i++) {
            if ((i - start) % mod == 0) {
                add(i);
            }
        }
        return this;
    }
}
