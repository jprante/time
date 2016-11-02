package org.xbib.time.chronic.tags;

import java.time.ZonedDateTime;

/**
 * Tokens are tagged with subclassed instances of this class when
 * they match specific criteria.
 * @param <T> type parameter
 */
public class Tag<T> {

    private T type;

    private ZonedDateTime now;

    public Tag(T type) {
        this.type = type;
    }

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public ZonedDateTime getNow() {
        return now;
    }

    public void setNow(ZonedDateTime s) {
        this.now = s;
    }

}
