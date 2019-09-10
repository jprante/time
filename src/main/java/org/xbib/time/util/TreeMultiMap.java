package org.xbib.time.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * A {@link TreeMap} based multi map. The keys ore ordered by a comparator.
 * @param <K> te key type
 * @param <V> the value type
 */
public class TreeMultiMap<K, V> extends AbstractMultiMap<K, V> {

    private final Comparator<K> comparator;

    public TreeMultiMap(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    @Override
    Map<K, Collection<V>> newMap() {
        return new TreeMap<>(comparator);
    }

    @Override
    Collection<V> newValues() {
        return new LinkedHashSet<>();
    }
}
