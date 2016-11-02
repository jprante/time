package org.xbib.time.chronic;

import org.xbib.time.chronic.tags.Pointer;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 *
 */
public class Options {
    private ZonedDateTime now;
    private ZoneId zoneId;
    private Pointer.PointerType context;
    private boolean guess;
    private int ambiguousTimeRange;
    private boolean compatibilityMode;

    public Options() {
        setContext(Pointer.PointerType.FUTURE);
        setNow(ZonedDateTime.now());
        setZoneId(ZoneId.of("GMT"));
        setGuess(true);
        setAmbiguousTimeRange(6);
    }

    public boolean isCompatibilityMode() {
        return compatibilityMode;
    }

    public Options setCompatibilityMode(boolean compatibilityMode) {
        this.compatibilityMode = compatibilityMode;
        return this;
    }

    public Pointer.PointerType getContext() {
        return context;
    }

    public Options setContext(Pointer.PointerType context) {
        this.context = context;
        return this;
    }

    public ZonedDateTime getNow() {
        return now;
    }

    public Options setNow(ZonedDateTime now) {
        this.now = now;
        return this;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public Options setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public boolean isGuess() {
        return guess;
    }

    public Options setGuess(boolean guess) {
        this.guess = guess;
        return this;
    }

    public int getAmbiguousTimeRange() {
        return ambiguousTimeRange;
    }

    public Options setAmbiguousTimeRange(int ambiguousTimeRange) {
        this.ambiguousTimeRange = ambiguousTimeRange;
        return this;
    }

}
