package org.xbib.time.schedule;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CronExpression {

    public static CronExpression yearly() {
        return parse(YEARLY);
    }

    public static CronExpression monthly() {
        return parse(MONTHLY);
    }

    public static CronExpression weekly() {
        return parse(WEEKLY);
    }

    public static CronExpression daily() {
        return parse(DAILY);
    }

    public static CronExpression hourly() {
        return parse(HOURLY);
    }

    public static boolean isValid(String s) {
        return isValid(s, DEFAULT_ONE_BASED_DAY_OF_WEEK, DEFAULT_SECONDS, DEFAULT_ALLOW_BOTH_DAYS);
    }

    public static CronExpression parse(String s) {
        return parse(s, DEFAULT_ONE_BASED_DAY_OF_WEEK, DEFAULT_SECONDS, DEFAULT_ALLOW_BOTH_DAYS);
    }

    public static Parser parser() {
        return new Parser();
    }

    public abstract boolean matches(ZonedDateTime t);

    public abstract ZonedDateTime nextExecution(ZonedDateTime from, ZonedDateTime to);

    private static final Pattern ALIAS_PATTERN = Pattern.compile("[a-z]+");

    private static final boolean DEFAULT_ONE_BASED_DAY_OF_WEEK = false;

    private static final boolean DEFAULT_SECONDS = false;

    private static final boolean DEFAULT_ALLOW_BOTH_DAYS = true;

    private static final String YEARLY = "0 0 1 1 *",
            MONTHLY = "0 0 1 * *",
            WEEKLY = "0 0 * * 7",
            DAILY = "0 0 * * *",
            HOURLY = "0 * * * *";

    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("yearly", YEARLY),
            Map.entry("annually", YEARLY),
            Map.entry("monthly", MONTHLY),
            Map.entry("weekly", WEEKLY),
            Map.entry("daily", DAILY),
            Map.entry("midnight", DAILY),
            Map.entry("hourly", HOURLY));

    private static boolean isValid(String s, boolean oneBasedDayOfWeek, boolean seconds, boolean allowBothDays) {
        boolean valid;
        try {
            parse(s, oneBasedDayOfWeek, seconds, allowBothDays);
            valid = true;
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

    private static CronExpression parse(String s, boolean oneBasedDayOfWeek, boolean seconds, boolean allowBothDays) {
        Objects.requireNonNull(s);
        if (s.charAt(0) == '@') {
            Matcher aliasMatcher = ALIAS_PATTERN.matcher(s);
            if (aliasMatcher.find(1)) {
                String alias = aliasMatcher.group();
                if (ALIASES.containsKey(alias)) {
                    return new DefaultCronExpression(ALIASES.get(alias),
                            DEFAULT_ONE_BASED_DAY_OF_WEEK, DEFAULT_SECONDS, DEFAULT_ALLOW_BOTH_DAYS);
                } else if ("reboot".equals(alias)) {
                    return new RebootCronExpression();
                }
            }
        }
        return new DefaultCronExpression(s, seconds, oneBasedDayOfWeek, allowBothDays);
    }

    public static class Parser {

        private boolean oneBasedDayOfWeek;

        private boolean seconds;

        private boolean allowBothDays;

        private Parser() {
            this.oneBasedDayOfWeek = DEFAULT_ONE_BASED_DAY_OF_WEEK;
            this.seconds = DEFAULT_SECONDS;
            this.allowBothDays = DEFAULT_ALLOW_BOTH_DAYS;
        }

        public boolean isValid(String s) {
            return CronExpression.isValid(s, oneBasedDayOfWeek, seconds, allowBothDays);
        }

        public CronExpression parse(String s) {
            return CronExpression.parse(s, oneBasedDayOfWeek, seconds, allowBothDays);
        }

        public Parser withOneBasedDayOfWeek(boolean oneBasedDayOfWeek) {
            this.oneBasedDayOfWeek = oneBasedDayOfWeek;
            return this;
        }

        public Parser withSecondsField(boolean secondsField) {
            this.seconds = secondsField;
            return this;
        }

        public Parser allowBothDayFields(boolean allowBothDayFields) {
            this.allowBothDays = allowBothDayFields;
            return this;
        }
    }
}
