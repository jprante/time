package org.xbib.time.schedule;

public class MonthField extends DefaultField {

    private MonthField(Builder b) {
        super(b);
    }

    public static MonthField parse(Tokens s) {
        return new Builder().parse(s).build();
    }

    public static class Builder extends DefaultField.Builder {

        protected static final Keywords KEYWORDS = new Keywords();

        static {
            KEYWORDS.put("JAN", 1);
            KEYWORDS.put("FEB", 2);
            KEYWORDS.put("MAR", 3);
            KEYWORDS.put("APR", 4);
            KEYWORDS.put("MAY", 5);
            KEYWORDS.put("JUN", 6);
            KEYWORDS.put("JUL", 7);
            KEYWORDS.put("AUG", 8);
            KEYWORDS.put("SEP", 9);
            KEYWORDS.put("OCT", 10);
            KEYWORDS.put("NOV", 11);
            KEYWORDS.put("DEC", 12);
        }

        Builder() {
            super(1, 12);
        }

        @Override
        protected Builder parse(Tokens tokens) {
            tokens.keywords(KEYWORDS);
            super.parse(tokens);
            tokens.reset();
            return this;
        }

        @Override
        public MonthField build() {
            return new MonthField(this);
        }
    }
}
