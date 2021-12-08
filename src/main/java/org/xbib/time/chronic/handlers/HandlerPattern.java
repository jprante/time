package org.xbib.time.chronic.handlers;

/**
 *
 */
public class HandlerPattern {

    private final boolean optional;

    public HandlerPattern(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }
}
