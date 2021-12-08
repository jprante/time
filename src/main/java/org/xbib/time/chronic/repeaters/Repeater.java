package org.xbib.time.chronic.repeaters;

import org.xbib.time.chronic.Options;
import org.xbib.time.chronic.Span;
import org.xbib.time.chronic.Token;
import org.xbib.time.chronic.tags.PointerType;
import org.xbib.time.chronic.tags.Tag;

import java.util.List;

/**
 * Repeater.
 * @param <T> type parameter
 */
public abstract class Repeater<T> extends Tag<T> implements Comparable<Repeater<?>> {

    public Repeater(T type) {
        super(type);
    }

    public static List<Token> scan(List<Token> tokens) {
        return Repeater.scan(tokens, new Options());
    }

    public static List<Token> scan(List<Token> tokens, Options options) {
        for (Token token : tokens) {
            Tag<?> t;
            t = RepeaterMonthName.scan(token);
            if (t != null) {
                token.tag(t);
            }
            t = RepeaterDayName.scan(token);
            if (t != null) {
                token.tag(t);
            }
            t = RepeaterDayPortion.scan(token);
            if (t != null) {
                token.tag(t);
            }
            t = RepeaterTime.scan(token, tokens, options);
            if (t != null) {
                token.tag(t);
            }
            t = RepeaterUnit.scan(token);
            if (t != null) {
                token.tag(t);
            }
        }
        return tokens;
    }

    @Override
    public int compareTo(Repeater<?> other) {
        return Integer.compare(getWidth(), other.getWidth());
    }

    /**
     * Returns the width (in seconds or months) of this repeatable.
     * @return width
     */
    public abstract int getWidth();

    /**
     * Returns the next occurance of this repeatable.
     * @param pointer pointer
     * @return span
     */
    public Span nextSpan(PointerType pointer) {
        if (getNow() == null) {
            throw new IllegalStateException("Start point must be set before calling #next");
        }
        return internalNextSpan(pointer);
    }

    protected abstract Span internalNextSpan(PointerType pointer);

    public Span thisSpan(PointerType pointer) {
        if (getNow() == null) {
            throw new IllegalStateException("Start point must be set before calling #this");
        }
        return internalThisSpan(pointer);
    }

    protected abstract Span internalThisSpan(PointerType pointer);

    public abstract Span getOffset(Span span, int amount, PointerType pointer);

    @Override
    public int hashCode() {
        return getType().hashCode() ^ getNow().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Repeater &&
                ((Repeater) other).getType().equals(getType()) &&
                ((Repeater) other).getNow().equals(getNow());
    }

    @Override
    public String toString() {
        return "repeater";
    }
}
