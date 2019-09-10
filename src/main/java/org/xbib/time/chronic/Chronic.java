package org.xbib.time.chronic;

import org.xbib.time.chronic.handlers.Handler;
import org.xbib.time.chronic.numerizer.Numerizer;
import org.xbib.time.chronic.repeaters.Repeater;
import org.xbib.time.chronic.tags.Grabber;
import org.xbib.time.chronic.tags.Ordinal;
import org.xbib.time.chronic.tags.Pointer;
import org.xbib.time.chronic.tags.Scalar;
import org.xbib.time.chronic.tags.Separator;
import org.xbib.time.chronic.tags.TimeZone;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Chronic {

    private static final Logger logger = Logger.getLogger(Chronic.class.getName());

    private Chronic() {
    }

    public static Span parse(String text) throws ParseException {
        return Chronic.parse(text, new Options());
    }

    /**
     * Parses a string containing a natural language date or time. If the parser
     * can find a date or time, either a Time or Chronic::Span will be returned
     * (depending on the value of {@code :guess}). If no date or time can be found,
     * +nil+ will be returned.
     * <p>
     * Options are:
     * <p>
     * [{@code :context}]
     * {@code :past} or {@code :future} (defaults to {@code :future})
     * <p>
     * If your string represents a birthday, you can set {@code :context} to {@code :past}
     * and if an ambiguous string is given, it will assume it is in the
     * past. Specify {@code :future<} or omit to set a future context.
     * <p>
     * [{@code :now}]
     * Time (defaults to Time.now)
     * <p>
     * By setting {@code :now} to a Time, all computations will be based off
     * of that time instead of Time.now
     * <p>
     * [{@code :guess<}]
     * +true+ or +false+ (defaults to +true+)
     * <p>
     * By default, the parser will guess a single point in time for the
     * given date or time. If you'd rather have the entire time span returned,
     * set {@code :guess} to +false+ and a Chronic::Span will be returned.
     * <p>
     * [{@code :ambiguous_time_range}]
     * Integer or {@code :none} (defaults to {@code 6} (6am-6pm))
     * <p>
     * If an Integer is given, ambiguous times (like 5:00) will be
     * assumed to be within the range of that time in the AM to that time
     * in the PM. For example, if you set it to {@code 7}, then the parser will
     * look for the time between 7am and 7pm. In the case of 5:00, it would
     * assume that means 5:00pm. If {@code :none} is given, no assumption
     * will be made, and the first matching instance of that time will
     * be used.
     * @param text text
     * @param options options
     * @return span
     * @throws ParseException parse exception
     */
    @SuppressWarnings("unchecked")
    public static Span parse(String text, Options options) throws ParseException {
        String normalizedText = Chronic.preNormalize(text);
        List<Token> tokens = Chronic.baseTokenize(normalizedText);
        List<Class<?>> optionScannerClasses = new LinkedList<>();
        optionScannerClasses.add(Repeater.class);
        for (Class<?> optionScannerClass : optionScannerClasses) {
            try {
                tokens = (List<Token>) optionScannerClass.getMethod("scan", List.class, Options.class)
                        .invoke(null, tokens, options);
            } catch (Exception e) {
                logger.log(Level.FINE, e.getMessage(), e);
                throw new ParseException("failed to scan tokens", 0);
            }
        }
        List<Class<?>> scannerClasses = new LinkedList<>();
        scannerClasses.add(Grabber.class);
        scannerClasses.add(Pointer.class);
        scannerClasses.add(Scalar.class);
        scannerClasses.add(Ordinal.class);
        scannerClasses.add(Separator.class);
        scannerClasses.add(TimeZone.class);
        for (Class<?> scannerClass : scannerClasses) {
            try {
                tokens = (List<Token>) scannerClass.getMethod("scan", List.class, Options.class)
                        .invoke(null, tokens, options);
            } catch (Exception e) {
                logger.log(Level.FINE, e.getMessage(), e);
                throw new ParseException("failed to scan tokens", 0);
            }
        }
        List<Token> taggedTokens = new LinkedList<>();
        for (Token token : tokens) {
            if (token.isTagged()) {
                taggedTokens.add(token);
            }
        }
        tokens = taggedTokens;
        Span span = Handler.tokensToSpan(tokens, options);
        // guess a time within a span if required
        if (options.isGuess()) {
            span = guess(span);
        }
        return span;
    }

    /**
     * Clean up the specified input text by stripping unwanted characters,
     * converting idioms to their canonical form, converting number words
     * to numbers (three =&gt; 3), and converting ordinal words to numeric
     * ordinals (third =&gt; 3rd).
     * @param text text
     * @return string
     */
    protected static String preNormalize(String text) {
        String normalizedText = text.toLowerCase();
        normalizedText = Chronic.numericizeNumbers(normalizedText);
        normalizedText = normalizedText.replaceAll("['\"\\.]", "");
        normalizedText = normalizedText.replaceAll("([/\\-,@])", " $1 ");
        normalizedText = normalizedText.replaceAll("\\btoday\\b", "this day");
        normalizedText = normalizedText.replaceAll("\\btomm?orr?ow\\b", "next day");
        normalizedText = normalizedText.replaceAll("\\byesterday\\b", "last day");
        normalizedText = normalizedText.replaceAll("\\bnoon\\b", "12:00");
        normalizedText = normalizedText.replaceAll("\\bmidnight\\b", "24:00");
        normalizedText = normalizedText.replaceAll("\\bbefore now\\b", "past");
        normalizedText = normalizedText.replaceAll("\\bnow\\b", "this second");
        normalizedText = normalizedText.replaceAll("\\b(ago|before)\\b", "past");
        normalizedText = normalizedText.replaceAll("\\bthis past\\b", "last");
        normalizedText = normalizedText.replaceAll("\\bthis last\\b", "last");
        normalizedText = normalizedText.replaceAll("\\b(?:in|during) the (morning)\\b", "$1");
        normalizedText = normalizedText.replaceAll("\\b(?:in the|during the|at) (afternoon|evening|night)\\b", "$1");
        normalizedText = normalizedText.replaceAll("\\btonight\\b", "this night");
        normalizedText = normalizedText.replaceAll("(?=\\w)([ap]m|oclock)\\b", " $1");
        normalizedText = normalizedText.replaceAll("\\b(hence|after|from)\\b", "future");
        normalizedText = Chronic.numericizeOrdinals(normalizedText);
        return normalizedText;
    }

    /**
     * Convert number words to numbers (three =&gt; 3).
     * @param text text
     * @return string
     */
    protected static String numericizeNumbers(String text) {
        return Numerizer.numerize(text);
    }

    /**
     * Convert ordinal words to numeric ordinals (third =&gt; 3rd).
     * @param text text
     * @return string
     */
    protected static String numericizeOrdinals(String text) {
        return text;
    }

    /**
     * Split the text on spaces and convert each word into
     * a Token.
     * @param text text
     * @return list of tokens
     */
    protected static List<Token> baseTokenize(String text) {
        String[] words = text.split(" ");
        List<Token> tokens = new LinkedList<>();
        for (String word : words) {
            tokens.add(new Token(word));
        }
        return tokens;
    }

    /**
     * Guess a specific time within the given span.
     * @param span span
     * @return span
     */
    protected static Span guess(Span span) {
        if (span == null) {
            return null;
        }
        Long guessValue;
        if (span.getWidth() > 1) {
            guessValue = span.getBegin() + (span.getWidth() / 2);
        } else {
            guessValue = span.getBegin();
        }
        return new Span(guessValue, guessValue, span.getZoneId());
    }
}
