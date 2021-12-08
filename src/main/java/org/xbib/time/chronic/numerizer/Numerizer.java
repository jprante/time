package org.xbib.time.chronic.numerizer;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Numerizer {

    private static final Pattern DEHYPHENATOR = Pattern.compile(" +|(\\D)-(\\D)");

    private static final Pattern DEHALFER = Pattern.compile("a half", Pattern.CASE_INSENSITIVE);

    private static final Pattern DEHAALFER = Pattern.compile("(\\d+)(?: | and |-)*haAlf", Pattern.CASE_INSENSITIVE);

    private static final Pattern ANDITION_PATTERN = Pattern.compile("(\\d+)( | and )(\\d+)(?=\\W|$)");

    private static final DirectNum[] DIRECT_NUMS;

    private static final TenPrefix[] TEN_PREFIXES;

    private static final BigPrefix[] BIG_PREFIXES;

    static {
        List<DirectNum> directNums = new LinkedList<>();
        directNums.add(new DirectNum("eleven", "11"));
        directNums.add(new DirectNum("twelve", "12"));
        directNums.add(new DirectNum("thirteen", "13"));
        directNums.add(new DirectNum("fourteen", "14"));
        directNums.add(new DirectNum("fifteen", "15"));
        directNums.add(new DirectNum("sixteen", "16"));
        directNums.add(new DirectNum("seventeen", "17"));
        directNums.add(new DirectNum("eighteen", "18"));
        directNums.add(new DirectNum("nineteen", "19"));
        directNums.add(new DirectNum("ninteen", "19"));
        directNums.add(new DirectNum("zero", "0"));
        directNums.add(new DirectNum("one", "1"));
        directNums.add(new DirectNum("two", "2"));
        directNums.add(new DirectNum("three", "3"));
        directNums.add(new DirectNum("four(\\W|$)", "4$1"));
        directNums.add(new DirectNum("five", "5"));
        directNums.add(new DirectNum("six(\\W|$)", "6$1"));
        directNums.add(new DirectNum("seven(\\W|$)", "7$1"));
        directNums.add(new DirectNum("eight(\\W|$)", "8$1"));
        directNums.add(new DirectNum("nine(\\W|$)", "9$1"));
        directNums.add(new DirectNum("ten", "10"));
        directNums.add(new DirectNum("\\ba\\b", "1"));
        DIRECT_NUMS = directNums.toArray(new DirectNum[0]);

        List<TenPrefix> tenPrefixes = new LinkedList<>();
        tenPrefixes.add(new TenPrefix("twenty", 20));
        tenPrefixes.add(new TenPrefix("thirty", 30));
        tenPrefixes.add(new TenPrefix("fourty", 40));
        tenPrefixes.add(new TenPrefix("fifty", 50));
        tenPrefixes.add(new TenPrefix("sixty", 60));
        tenPrefixes.add(new TenPrefix("seventy", 70));
        tenPrefixes.add(new TenPrefix("eighty", 80));
        tenPrefixes.add(new TenPrefix("ninety", 90));
        tenPrefixes.add(new TenPrefix("ninty", 90));
        TEN_PREFIXES = tenPrefixes.toArray(new TenPrefix[0]);

        List<BigPrefix> bigPrefixes = new LinkedList<>();
        bigPrefixes.add(new BigPrefix("hundred", 100L));
        bigPrefixes.add(new BigPrefix("thousand", 1000L));
        bigPrefixes.add(new BigPrefix("million", 1000000L));
        bigPrefixes.add(new BigPrefix("billion", 1000000000L));
        bigPrefixes.add(new BigPrefix("trillion", 1000000000000L));
        BIG_PREFIXES = bigPrefixes.toArray(new BigPrefix[0]);
    }

    public static String numerize(String str) {
        String numerizedStr = str;
        numerizedStr = Numerizer.DEHYPHENATOR.matcher(numerizedStr).replaceAll("$1 $2");
        numerizedStr = Numerizer.DEHALFER.matcher(numerizedStr).replaceAll("haAlf");
        for (DirectNum dn : Numerizer.DIRECT_NUMS) {
            numerizedStr = dn.getName().matcher(numerizedStr).replaceAll(dn.getNumber());
        }
        for (Prefix tp : Numerizer.TEN_PREFIXES) {
            Matcher matcher = tp.getName().matcher(numerizedStr);
            if (matcher.find()) {
                StringBuilder matcherBuffer = new StringBuilder();
                do {
                    if (matcher.group(1) == null) {
                        matcher.appendReplacement(matcherBuffer, String.valueOf(tp.getNumber()));
                    } else {
                        matcher.appendReplacement(matcherBuffer, String.valueOf(tp.getNumber() +
                                Long.parseLong(matcher.group(1).trim())));
                    }
                } while (matcher.find());
                matcher.appendTail(matcherBuffer);
                numerizedStr = matcherBuffer.toString();
            }
        }
        for (Prefix bp : Numerizer.BIG_PREFIXES) {
            Matcher matcher = bp.getName().matcher(numerizedStr);
            if (matcher.find()) {
                StringBuilder matcherBuffer = new StringBuilder();
                do {
                    if (matcher.group(1) == null) {
                        matcher.appendReplacement(matcherBuffer, String.valueOf(bp.getNumber()));
                    } else {
                        matcher.appendReplacement(matcherBuffer, String.valueOf(bp.getNumber() *
                                Long.parseLong(matcher.group(1).trim())));
                    }
                } while (matcher.find());
                matcher.appendTail(matcherBuffer);
                numerizedStr = matcherBuffer.toString();
                numerizedStr = Numerizer.andition(numerizedStr);
            }
        }
        Matcher matcher = Numerizer.DEHAALFER.matcher(numerizedStr);
        if (matcher.find()) {
            StringBuilder matcherBuffer = new StringBuilder();
            do {
                matcher.appendReplacement(matcherBuffer,
                        String.valueOf(Float.parseFloat(matcher.group(1).trim()) + 0.5f));
            } while (matcher.find());
            matcher.appendTail(matcherBuffer);
            numerizedStr = matcherBuffer.toString();
        }
        return numerizedStr;
    }

    private static String andition(String str) {
        StringBuilder anditionStr = new StringBuilder(str);
        Matcher matcher = Numerizer.ANDITION_PATTERN.matcher(anditionStr);
        while (matcher.find()) {
            if (" and ".equalsIgnoreCase(matcher.group(2)) || matcher.group(1).length() > matcher.group(3).length()) {
                anditionStr.replace(matcher.start(), matcher.end(),
                        String.valueOf(Integer.parseInt(matcher.group(1).trim()) +
                                Integer.parseInt(matcher.group(3).trim())));
                matcher = Numerizer.ANDITION_PATTERN.matcher(anditionStr);
            }
        }
        return anditionStr.toString();
    }

    /**
     *
     */
    private static class DirectNum {

        private final Pattern name;

        private final String number;

        DirectNum(String name, String number) {
            this.name = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
            this.number = number;
        }

        public Pattern getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }

    /**
     *
     */
    static class Prefix {

        private final Pattern name;

        private final long number;

        Prefix(Pattern name, long number) {
            this.name = name;
            this.number = number;
        }

        public Pattern getName() {
            return name;
        }

        public long getNumber() {
            return number;
        }
    }

    private static class TenPrefix extends Prefix {
        TenPrefix(String name, long number) {
            super(Pattern.compile("(?:" + name + ")( *\\d(?=\\D|$))*", Pattern.CASE_INSENSITIVE), number);
        }
    }

    private static class BigPrefix extends Prefix {
        BigPrefix(String name, long number) {
            super(Pattern.compile("(\\d*) *" + name, Pattern.CASE_INSENSITIVE), number);
        }
    }
}
