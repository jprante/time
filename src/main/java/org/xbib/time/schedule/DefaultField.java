package org.xbib.time.schedule;

import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

public class DefaultField implements TimeField {

    private final boolean fullRange;

    private final NavigableSet<Integer> numbers;

    DefaultField(Builder b) {
        fullRange = b.fullRange;
        numbers = fullRange ? null : b.numbers;
    }

    public static DefaultField parse(Tokens s, int min, int max) {
        return new Builder(min, max).parse(s).build();
    }

    @Override
    public boolean contains(int number) {
        return fullRange || numbers.contains(number);
    }

    @Override
    public NavigableSet<Integer> getNumbers() {
        return numbers;
    }

    @Override
    public boolean isFullRange() {
        return fullRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultField that = (DefaultField) o;
        if (fullRange != that.fullRange) {
            return false;
        }
        return Objects.equals(numbers, that.numbers);
    }

    @Override
    public int hashCode() {
        int result = (fullRange ? 1 : 0);
        result = 31 * result + (numbers != null ? numbers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return isFullRange() ? "*" : getNumbers().toString();
    }

    public static class Builder {

        private final NavigableSet<Integer> numbers;

        private final int min;

        private final int max;

        private boolean fullRange;

        Builder(int min, int max) {
            this.min = min;
            this.max = max;
            numbers = new TreeSet<>();
        }

        protected Builder parse(Tokens tokens) {
            Token token;
            while (!endOfField(token = tokens.next())) {
                if (parseValue(tokens, token, min, max)) {
                    break;
                }
            }
            return this;
        }

        protected boolean parseValue(Tokens tokens, Token token, int first, int last) {
            if (token == Token.NUMBER) {
                return parseNumber(tokens, tokens.next(), tokens.number(), last);
            } else if (token == Token.MATCH_ALL) {
                token = tokens.next();
                if (token == Token.SKIP) {
                    rangeSkip(first, last, nextNumber(tokens));
                } else if (token == Token.VALUE_SEPARATOR) {
                    range(first, last);
                } else if (endOfField(token)) {
                    range(first, last);
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns true if the end of this field has been reached.
         * @param tokens tokens
         * @param token token
         * @param first first
         * @param last last
         * @return true if end reached
         */
        protected boolean parseNumber(Tokens tokens, Token token, int first, int last) {
            Token t = token;
            int l = last;
            if (t == Token.SKIP) {
                rangeSkip(first, l, nextNumber(tokens));
            } else if (t == Token.RANGE) {
                l = nextNumber(tokens);
                t = tokens.next();
                if (t == Token.SKIP) {
                    rangeSkip(first, l, nextNumber(tokens));
                } else if (t == Token.VALUE_SEPARATOR) {
                    range(first, l);
                } else if (endOfField(t)) {
                    range(first, l);
                    return true;
                }
            } else if (t == Token.VALUE_SEPARATOR) {
                add(first);
            } else if (endOfField(t)) {
                add(first);
                return true;
            }
            return false;
        }

        int nextNumber(Tokens tokens) {
            if (tokens.next() == Token.NUMBER) {
                return tokens.number();
            }
            throw new IllegalStateException("Expected number");
        }

        private boolean endOfField(Token token) {
            return token == Token.FIELD_SEPARATOR || token == Token.END_OF_INPUT;
        }

        void rangeSkip(int first, int last, int skip) {
            for (int i = first; i <= last; i++) {
                if ((i - min) % skip == 0) {
                    add(i);
                }
            }
        }

        protected void range(int first, int last) {
            if (first == min && last == max) {
                fullRange = true;
            } else {
                for (int i = first; i <= last; i++) {
                    add(i);
                }
            }
        }

        protected void add(int value) {
            numbers.add(value);
        }

        public DefaultField build() {
            return new DefaultField(this);
        }
    }
}
