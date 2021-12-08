package org.xbib.time.chronic.repeaters;

import org.xbib.time.chronic.Options;
import org.xbib.time.chronic.Span;
import org.xbib.time.chronic.Tick;
import org.xbib.time.chronic.Token;
import org.xbib.time.chronic.tags.PointerType;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
public class RepeaterTime extends Repeater<Tick> {

    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{1,2}(:?\\d{2})?([\\.:]?\\d{2})?$");

    private ZonedDateTime currentTime;

    public RepeaterTime(String time) {
        super(null);
        String t = time.replaceAll(":", "");
        Tick type;
        boolean ambiguous = time.contains(":") && Integer.parseInt(t.substring(0, 1)) != 0 &&
                Integer.parseInt(t.substring(0, 2)) <= 12;
        int length = t.length();
        if (length <= 2) {
            int hours = Integer.parseInt(t);
            int hoursInSeconds = hours * 60 * 60;
            if (hours == 12) {
                type = new Tick(0, true);
            } else {
                type = new Tick(hoursInSeconds, true);
            }
        } else if (length == 3) {
            int hoursInSeconds = Integer.parseInt(t.substring(0, 1)) * 60 * 60;
            int minutesInSeconds = Integer.parseInt(t.substring(1)) * 60;
            type = new Tick(hoursInSeconds + minutesInSeconds, true);
        } else if (length == 4) {
            int hours = Integer.parseInt(t.substring(0, 2));
            int hoursInSeconds = hours * 60 * 60;
            int minutesInSeconds = Integer.parseInt(t.substring(2)) * 60;
            if (hours == 12) {
                type = new Tick(minutesInSeconds, ambiguous);
            } else {
                type = new Tick(hoursInSeconds + minutesInSeconds, ambiguous);
            }
        } else if (length == 5) {
            int hoursInSeconds = Integer.parseInt(t.substring(0, 1)) * 60 * 60;
            int minutesInSeconds = Integer.parseInt(t.substring(1, 3)) * 60;
            int seconds = Integer.parseInt(t.substring(3));
            type = new Tick(hoursInSeconds + minutesInSeconds + seconds, true);
        } else if (length == 6) {
            int hours = Integer.parseInt(t.substring(0, 2));
            int hoursInSeconds = hours * 60 * 60;
            int minutesInSeconds = Integer.parseInt(t.substring(2, 4)) * 60;
            int seconds = Integer.parseInt(t.substring(4, 6));
            if (hours == 12) {
                type = new Tick(minutesInSeconds + seconds, ambiguous);
            } else {
                type = new Tick(hoursInSeconds + minutesInSeconds + seconds, ambiguous);
            }
        } else {
            throw new IllegalArgumentException("Time cannot exceed six digits");
        }
        setType(type);
    }

    public static RepeaterTime scan(Token token, List<Token> tokens, Options options) {
        if (RepeaterTime.TIME_PATTERN.matcher(token.getWord()).matches()) {
            return new RepeaterTime(token.getWord());
        }
        Integer intStrValue = integerValue(token.getWord());
        if (intStrValue != null) {
            return new RepeaterTime(intStrValue.toString());
        }
        return null;
    }

    private static Integer integerValue(String str) {
        if (str != null) {
            String s = str.toLowerCase();
            switch (s) {
                case "one":
                    return 1;
                case "two":
                    return 2;
                case "three":
                    return 3;
                case "four":
                    return 4;
                case "five":
                    return 5;
                case "six":
                    return 6;
                case "seven":
                    return 7;
                case "eight":
                    return 8;
                case "nine":
                    return 9;
                case "ten":
                    return 10;
                case "eleven":
                    return 11;
                case "twelve":
                    return 12;
            }
        }
        return null;
    }

    private static ZonedDateTime ymd(ZonedDateTime zonedDateTime) {
        return ZonedDateTime.of(zonedDateTime.getYear(), zonedDateTime.getMonthValue(), zonedDateTime.getDayOfMonth(),
                0, 0, 0, 0, zonedDateTime.getZone());
    }

    @Override
    protected Span internalNextSpan(PointerType pointer) {
        int halfDay = RepeaterDay.DAY_SECONDS / 2;
        int fullDay = RepeaterDay.DAY_SECONDS;

        ZonedDateTime now = getNow();
        Tick tick = getType();
        boolean first = false;
        if (currentTime == null) {
            first = true;
            ZonedDateTime midnight = ymd(now);
            ZonedDateTime yesterdayMidnight = midnight.minus(fullDay, ChronoUnit.SECONDS);
            ZonedDateTime tomorrowMidnight = midnight.plus(fullDay, ChronoUnit.SECONDS);
            boolean done = false;
            if (pointer == PointerType.FUTURE) {
                if (tick.isAmbiguous()) {
                    List<ZonedDateTime> futureDates = new LinkedList<>();
                    futureDates.add(midnight.plus(tick.intValue(), ChronoUnit.SECONDS));
                    futureDates.add(midnight.plus(halfDay + (long) tick.intValue(), ChronoUnit.SECONDS));
                    futureDates.add(tomorrowMidnight.plus(tick.intValue(), ChronoUnit.SECONDS));
                    for (ZonedDateTime futureDate : futureDates) {
                        if (futureDate.isAfter(now) || futureDate.equals(now)) {
                            currentTime = futureDate;
                            done = true;
                            break;
                        }
                    }
                } else {
                    List<ZonedDateTime> futureDates = new LinkedList<>();
                    futureDates.add(midnight.plus(tick.intValue(), ChronoUnit.SECONDS));
                    futureDates.add(tomorrowMidnight.plus(tick.intValue(), ChronoUnit.SECONDS));
                    for (ZonedDateTime futureDate : futureDates) {
                        if (futureDate.isAfter(now) || futureDate.equals(now)) {
                            currentTime = futureDate;
                            done = true;
                            break;
                        }
                    }
                }
            } else {
                List<ZonedDateTime> pastDates = new LinkedList<>();
                if (tick.isAmbiguous()) {
                    pastDates.add(midnight.plus(halfDay + (long) tick.intValue(), ChronoUnit.SECONDS));
                    pastDates.add(midnight.plus(tick.intValue(), ChronoUnit.SECONDS));
                    pastDates.add(yesterdayMidnight.plus(tick.intValue() * 2L, ChronoUnit.SECONDS));
                    for (ZonedDateTime pastDate : pastDates) {
                        if (pastDate.isBefore(now) || pastDate.equals(now)) {
                            currentTime = pastDate;
                            done = true;
                            break;
                        }
                    }
                } else {
                    pastDates.add(midnight.plus(tick.intValue(), ChronoUnit.SECONDS));
                    pastDates.add(yesterdayMidnight.plus(tick.intValue(), ChronoUnit.SECONDS));
                    for (ZonedDateTime pastDate : pastDates) {
                        if (pastDate.isBefore(now) || pastDate.equals(now)) {
                            currentTime = pastDate;
                            done = true;
                            break;
                        }
                    }
                }
            }
            if (!done && currentTime == null) {
                throw new IllegalStateException("Current time cannot be null at this point.");
            }
        }
        if (!first) {
            int increment = tick.isAmbiguous() ? halfDay : fullDay;
            long direction = pointer == PointerType.FUTURE ? 1L : -1L;
            currentTime = currentTime.plus(direction * increment, ChronoUnit.SECONDS);
        }
        return new Span(currentTime, currentTime.plus(getWidth(), ChronoUnit.SECONDS));
    }

    @Override
    protected Span internalThisSpan(PointerType pointer) {
        return nextSpan(pointer == PointerType.NONE ? PointerType.FUTURE : pointer);
    }

    @Override
    public Span getOffset(Span span, int amount, PointerType pointer) {
        throw new IllegalStateException("Not implemented.");
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ getWidth();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RepeaterTime &&
                ((Repeater) other).getType().equals(getType()) &&
                ((Repeater) other).getNow().equals(getNow());
    }

    @Override
    public String toString() {
        return super.toString() + "-time-" + getType();
    }
}
