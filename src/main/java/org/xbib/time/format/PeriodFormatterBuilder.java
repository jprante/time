package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;


/**
 * Factory that creates complex instances of PeriodFormatter via method calls.
 * <p>
 * Period formatting is performed by the {@link PeriodFormatter} class.
 * Three classes provide factory methods to create formatters, and this is one.
 * The others are {@link PeriodFormat} and {@link ISOPeriodFormat}.
 * <p>
 * PeriodFormatterBuilder is used for constructing formatters which are then
 * used to print or parse. The formatters are built by appending specific fields
 * or other formatters to an instance of this builder.
 * <p>
 * For example, a formatter that prints years and months, like "15 years and 8 months",
 * can be constructed as follows:
 * <pre>
 * PeriodFormatter yearsAndMonths = new PeriodFormatterBuilder()
 *     .appendYears()
 *     .appendSuffix(" year", " years")
 *     .appendSeparator(" and ")
 *     .appendMonths()
 *     .appendSuffix(" month", " months")
 *     .toFormatter();
 * </pre>
 * <p>
 * PeriodFormatterBuilder itself is mutable and not thread-safe, but the
 * formatters that it builds are thread-safe and immutable.
 */
public class PeriodFormatterBuilder {

    private static final ConcurrentMap<String, Pattern> PATTERNS = new ConcurrentHashMap<>();

    private int iMinPrintedDigits;
    private int iMaxParsedDigits;
    private boolean iRejectSignedValues;

    private PeriodFieldAffix iPrefix;

    // List of Printers and Parsers used to build a final formatter.
    private List<Object> iElementPairs;
    /**
     * Set to true if the formatter is not a printer.
     */
    private boolean iNotPrinter;
    /**
     * Set to true if the formatter is not a parser.
     */
    private boolean iNotParser;

    // Last PeriodFormatter appended of each field type.
    //private FieldFormatter[] iFieldFormatters;

    public PeriodFormatterBuilder() {
        clear();
    }

    private static PeriodFormatter toFormatter(List<Object> elementPairs, boolean notPrinter, boolean notParser) {
        if (notPrinter && notParser) {
            throw new IllegalStateException("Builder has created neither a printer nor a parser");
        }
        int size = elementPairs.size();
        if (size >= 2 && elementPairs.get(0) instanceof Separator) {
            Separator sep = (Separator) elementPairs.get(0);
            if (sep.iAfterParser == null && sep.iAfterPrinter == null) {
                PeriodFormatter f = toFormatter(elementPairs.subList(2, size), notPrinter, notParser);
                sep = sep.finish(f.getPrinter(), f.getParser());
                return new PeriodFormatter(sep, sep);
            }
        }
        Object[] comp = createComposite(elementPairs);
        if (notPrinter) {
            return new PeriodFormatter(null, (PeriodParser) comp[1]);
        } else if (notParser) {
            return new PeriodFormatter((PeriodPrinter) comp[0], null);
        } else {
            return new PeriodFormatter((PeriodPrinter) comp[0], (PeriodParser) comp[1]);
        }
    }

    private static Object[] createComposite(List<Object> elementPairs) {
        switch (elementPairs.size()) {
            case 0:
                return new Object[]{Literal.EMPTY, Literal.EMPTY};
            case 1:
                return new Object[]{elementPairs.get(0), elementPairs.get(1)};
            default:
                Composite comp = new Composite(elementPairs);
                return new Object[]{comp, comp};
        }
    }

    /**
     * Constructs a PeriodFormatter using all the appended elements.
     * <p>
     * This is the main method used by applications at the end of the build
     * process to create a usable formatter.
     * <p>
     * Once this method has been called, the builder is in an invalid state.
     * <p>
     * The returned formatter may not support both printing and parsing.
     * The methods {@link PeriodFormatter#isPrinter()} and
     * {@link PeriodFormatter#isParser()} will help you determine the state
     * of the formatter.
     *
     * @return the newly created formatter
     * @throws IllegalStateException if the builder can produce neither a printer nor a parser
     */
    public PeriodFormatter toFormatter() {
        PeriodFormatter formatter = toFormatter(iElementPairs, iNotPrinter, iNotParser);
        return formatter;
    }

    /**
     * Internal method to create a PeriodPrinter instance using all the
     * appended elements.
     * <p>
     * Most applications will not use this method.
     * If you want a printer in an application, call {@link #toFormatter()}
     * and just use the printing API.
     * <p>
     * Subsequent changes to this builder do not affect the returned printer.
     *
     * @return the newly created printer, null if builder cannot create a printer
     */
    public PeriodPrinter toPrinter() {
        if (iNotPrinter) {
            return null;
        }
        return toFormatter().getPrinter();
    }

    /**
     * Internal method to create a PeriodParser instance using all the
     * appended elements.
     * <p>
     * Most applications will not use this method.
     * If you want a printer in an application, call {@link #toFormatter()}
     * and just use the printing API.
     * <p>
     * Subsequent changes to this builder do not affect the returned parser.
     *
     * @return the newly created parser, null if builder cannot create a parser
     */
    public PeriodParser toParser() {
        if (iNotParser) {
            return null;
        }
        return toFormatter().getParser();
    }

    /**
     * Clears out all the appended elements, allowing this builder to be reused.
     */
    public void clear() {
        iMinPrintedDigits = 1;
        //iPrintZeroSetting = PRINT_ZERO_RARELY_LAST;
        iMaxParsedDigits = 10;
        iRejectSignedValues = false;
        iPrefix = null;
        if (iElementPairs == null) {
            iElementPairs = new ArrayList<Object>();
        } else {
            iElementPairs.clear();
        }
        iNotPrinter = false;
        iNotParser = false;
    }

    /**
     * Appends another formatter.
     * @param formatter formatter
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder append(PeriodFormatter formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("No formatter supplied");
        }
        clearPrefix();
        append0(formatter.getPrinter(), formatter.getParser());
        return this;
    }

    /**
     * Appends a printer parser pair.
     * <p>
     * Either the printer or the parser may be null, in which case the builder will
     * be unable to produce a parser or printer repectively.
     *
     * @param printer appends a printer to the builder, null if printing is not supported
     * @param parser  appends a parser to the builder, null if parsing is not supported
     * @return this PeriodFormatterBuilder
     * @throws IllegalArgumentException if both the printer and parser are null
     */
    public PeriodFormatterBuilder append(PeriodPrinter printer, PeriodParser parser) {
        if (printer == null && parser == null) {
            throw new IllegalArgumentException("No printer or parser supplied");
        }
        clearPrefix();
        append0(printer, parser);
        return this;
    }

    /**
     * Instructs the printer to emit specific text, and the parser to expect it.
     * The parser is case-insensitive.
     * @param text text
     * @return this PeriodFormatterBuilder
     * @throws IllegalArgumentException if text is null
     */
    public PeriodFormatterBuilder appendLiteral(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Literal must not be null");
        }
        clearPrefix();
        Literal literal = new Literal(text);
        append0(literal, literal);
        return this;
    }

    /**
     * Set the minimum digits printed for the next and following appended
     * fields. By default, the minimum digits printed is one. If the field value
     * is zero, it is not printed unless a printZero rule is applied.
     * @param minDigits min digits
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder minimumPrintedDigits(int minDigits) {
        iMinPrintedDigits = minDigits;
        return this;
    }

    /**
     * Set the maximum digits parsed for the next and following appended
     * fields. By default, the maximum digits parsed is ten.
     * @param maxDigits max digits
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder maximumParsedDigits(int maxDigits) {
        iMaxParsedDigits = maxDigits;
        return this;
    }

    /**
     * Reject signed values when parsing the next and following appended fields.
     * @param v flag
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder rejectSignedValues(boolean v) {
        iRejectSignedValues = v;
        return this;
    }

    /**
     * Append a field prefix which applies only to the next appended field. If
     * the field is not printed, neither is the prefix.
     *
     * @param text text to print before field only if field is printed
     * @return this PeriodFormatterBuilder
     * @see #appendSuffix
     */
    public PeriodFormatterBuilder appendPrefix(String text) {
        if (text == null) {
            throw new IllegalArgumentException();
        }
        return appendPrefix(new SimpleAffix(text));
    }

    /**
     * Append a field prefix which applies only to the next appended field. If
     * the field is not printed, neither is the prefix.
     * <p>
     * During parsing, the singular and plural versions are accepted whether
     * or not the actual value matches plurality.
     *
     * @param singularText text to print if field value is one
     * @param pluralText   text to print if field value is not one
     * @return this PeriodFormatterBuilder
     * @see #appendSuffix
     */
    public PeriodFormatterBuilder appendPrefix(String singularText,
                                               String pluralText) {
        if (singularText == null || pluralText == null) {
            throw new IllegalArgumentException();
        }
        return appendPrefix(new PluralAffix(singularText, pluralText));
    }

    /**
     * Append a field prefix which applies only to the next appended field.
     * If the field is not printed, neither is the prefix.
     * <p>
     * The value is converted to String. During parsing, the prefix is selected based
     * on the match with the regular expression. The index of the first regular
     * expression that matches value converted to String nominates the prefix. If
     * none of the regular expressions match the value converted to String then the
     * last prefix is selected.
     * <p>
     * An example usage for English might look like this:
     * <pre>
     * appendPrefix(new String[] { &quot;&circ;1$&quot;, &quot;.*&quot; },
     * new String[] { &quot; year&quot;, &quot; years&quot; })
     * </pre>
     * <p>
     * Please note that for languages with simple mapping (singular and plural prefix
     * only - like the one above) the {@link #appendPrefix(String, String)} method
     * will produce in a slightly faster formatter and that
     * {@link #appendPrefix(String[], String[])} method should be only used when the
     * mapping between values and prefixes is more complicated than the difference between
     * singular and plural.
     *
     * @param regularExpressions an array of regular expressions, at least one
     *                           element, length has to match the length of prefixes parameter
     * @param prefixes           an array of prefixes, at least one element, length has to
     *                           match the length of regularExpressions parameter
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if no field exists to append to
     * @see #appendPrefix
     * @since 2.5
     */
    public PeriodFormatterBuilder appendPrefix(String[] regularExpressions, String[] prefixes) {
        if (regularExpressions == null || prefixes == null ||
                regularExpressions.length < 1 || regularExpressions.length != prefixes.length) {
            throw new IllegalArgumentException();
        }
        return appendPrefix(new RegExAffix(regularExpressions, prefixes));
    }

    /**
     * Append a field prefix which applies only to the next appended field. If
     * the field is not printed, neither is the prefix.
     *
     * @param prefix custom prefix
     * @return this PeriodFormatterBuilder
     * @see #appendSuffix
     */
    private PeriodFormatterBuilder appendPrefix(PeriodFieldAffix prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        if (iPrefix != null) {
            prefix = new CompositeAffix(iPrefix, prefix);
        }
        iPrefix = prefix;
        return this;
    }

    /**
     * Instruct the printer to emit an integer years field, if supported.
     * <p>
     * The number of printed and parsed digits can be controlled using
     * {@link #minimumPrintedDigits(int)} and {@link #maximumParsedDigits(int)}.
     *
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder appendYears() {
        appendField(ChronoUnit.YEARS);
        return this;
    }

    /**
     * Instruct the printer to emit an integer months field, if supported.
     * <p>
     * The number of printed and parsed digits can be controlled using
     * {@link #minimumPrintedDigits(int)} and {@link #maximumParsedDigits(int)}.
     *
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder appendMonths() {
        appendField(ChronoUnit.MONTHS);
        return this;
    }

    /**
     * Instruct the printer to emit an integer weeks field, if supported.
     * <p>
     * The number of printed and parsed digits can be controlled using
     * {@link #minimumPrintedDigits(int)} and {@link #maximumParsedDigits(int)}.
     *
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder appendWeeks() {
        appendField(ChronoUnit.WEEKS);
        return this;
    }

    /**
     * Instruct the printer to emit an integer days field, if supported.
     * <p>
     * The number of printed and parsed digits can be controlled using
     * {@link #minimumPrintedDigits(int)} and {@link #maximumParsedDigits(int)}.
     *
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder appendDays() {
        appendField(ChronoUnit.DAYS);
        return this;
    }

    /**
     * Instruct the printer to emit an integer hours field, if supported.
     * <p>
     * The number of printed and parsed digits can be controlled using
     * {@link #minimumPrintedDigits(int)} and {@link #maximumParsedDigits(int)}.
     *
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder appendHours() {
        appendField(ChronoUnit.HOURS);
        return this;
    }

    /**
     * Instruct the printer to emit an integer minutes field, if supported.
     * <p>
     * The number of printed and parsed digits can be controlled using
     * {@link #minimumPrintedDigits(int)} and {@link #maximumParsedDigits(int)}.
     *
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder appendMinutes() {
        appendField(ChronoUnit.MINUTES);
        return this;
    }

    /**
     * Instruct the printer to emit an integer seconds field, if supported.
     * <p>
     * The number of printed and parsed digits can be controlled using
     * {@link #minimumPrintedDigits(int)} and {@link #maximumParsedDigits(int)}.
     *
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder appendSeconds() {
        appendField(ChronoUnit.SECONDS);
        return this;
    }

    /**
     * Instruct the printer to emit an integer millis field, if supported.
     * <p>
     * The number of printed and parsed digits can be controlled using
     * {@link #minimumPrintedDigits(int)} and {@link #maximumParsedDigits(int)}.
     *
     * @return this PeriodFormatterBuilder
     */
    public PeriodFormatterBuilder appendMillis() {
        appendField(ChronoUnit.MILLIS);
        return this;
    }

    private void appendField(ChronoUnit unit) {
        appendField(unit, iMinPrintedDigits);
    }

    private void appendField(ChronoUnit unit, int minPrinted) {
        FieldFormatter field = new FieldFormatter(minPrinted,
                iMaxParsedDigits, iRejectSignedValues, unit, iPrefix, null);
        append0(field, field);
        iPrefix = null;
    }

    /**
     * Append a field suffix which applies only to the last appended field. If
     * the field is not printed, neither is the suffix.
     *
     * @param text text to print after field only if field is printed
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if no field exists to append to
     * @see #appendPrefix
     */
    public PeriodFormatterBuilder appendSuffix(String text) {
        if (text == null) {
            throw new IllegalArgumentException();
        }
        return appendSuffix(new SimpleAffix(text));
    }

    /**
     * Append a field suffix which applies only to the last appended field. If
     * the field is not printed, neither is the suffix.
     * <p>
     * During parsing, the singular and plural versions are accepted whether or
     * not the actual value matches plurality.
     *
     * @param singularText text to print if field value is one
     * @param pluralText   text to print if field value is not one
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if no field exists to append to
     * @see #appendPrefix
     */
    public PeriodFormatterBuilder appendSuffix(String singularText,
                                               String pluralText) {
        if (singularText == null || pluralText == null) {
            throw new IllegalArgumentException();
        }
        return appendSuffix(new PluralAffix(singularText, pluralText));
    }

    /**
     * Append a field suffix which applies only to the last appended field.
     * If the field is not printed, neither is the suffix.
     * <p>
     * The value is converted to String. During parsing, the suffix is selected based
     * on the match with the regular expression. The index of the first regular
     * expression that matches value converted to String nominates the suffix. If
     * none of the regular expressions match the value converted to String then the
     * last suffix is selected.
     * <p>
     * An example usage for English might look like this:
     * <pre>
     * appendSuffix(new String[] { &quot;&circ;1$&quot;, &quot;.*&quot; },
     * new String[] { &quot; year&quot;, &quot; years&quot; })
     * </pre>
     * Please note that for languages with simple mapping (singular and plural suffix
     * only - like the one above) the {@link #appendSuffix(String, String)} method
     * will result in a slightly faster formatter and that
     * {@link #appendSuffix(String[], String[])} method should be only used when the
     * mapping between values and prefixes is more complicated than the difference between
     * singular and plural.
     *
     * @param regularExpressions an array of regular expressions, at least one
     *                           element, length has to match the length of suffixes parameter
     * @param suffixes           an array of suffixes, at least one element, length has to
     *                           match the length of regularExpressions parameter
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if no field exists to append to
     * @see #appendPrefix
     * @since 2.5
     */
    public PeriodFormatterBuilder appendSuffix(String[] regularExpressions, String[] suffixes) {
        if (regularExpressions == null || suffixes == null ||
                regularExpressions.length < 1 || regularExpressions.length != suffixes.length) {
            throw new IllegalArgumentException();
        }
        return appendSuffix(new RegExAffix(regularExpressions, suffixes));
    }

    /**
     * Append a field suffix which applies only to the last appended field. If
     * the field is not printed, neither is the suffix.
     *
     * @param suffix custom suffix
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if no field exists to append to
     * @see #appendPrefix
     */
    private PeriodFormatterBuilder appendSuffix(PeriodFieldAffix suffix) {
        final Object originalPrinter;
        final Object originalParser;
        if (iElementPairs.size() > 0) {
            originalPrinter = iElementPairs.get(iElementPairs.size() - 2);
            originalParser = iElementPairs.get(iElementPairs.size() - 1);
        } else {
            originalPrinter = null;
            originalParser = null;
        }

        if (originalPrinter == null || originalParser == null ||
                originalPrinter != originalParser ||
                !(originalPrinter instanceof FieldFormatter)) {
            throw new IllegalStateException("No field to apply suffix to");
        }

        clearPrefix();
        FieldFormatter newField = new FieldFormatter((FieldFormatter) originalPrinter, suffix);
        iElementPairs.set(iElementPairs.size() - 2, newField);
        iElementPairs.set(iElementPairs.size() - 1, newField);
        return this;
    }

    /**
     * Append a separator, which is output if fields are printed both before
     * and after the separator.
     * <p>
     * For example, <code>builder.appendDays().appendSeparator(",").appendHours()</code>
     * will only output the comma if both the days and hours fields are output.
     * <p>
     * The text will be parsed case-insensitively.
     * <p>
     * Note: appending a separator discontinues any further work on the latest
     * appended field.
     *
     * @param text the text to use as a separator
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if this separator follows a previous one
     */
    public PeriodFormatterBuilder appendSeparator(String text) {
        return appendSeparator(text, text, null, true, true);
    }

    /**
     * Append a separator, which is output only if fields are printed after the separator.
     * <p>
     * For example,
     * <code>builder.appendDays().appendSeparatorIfFieldsAfter(",").appendHours()</code>
     * will only output the comma if the hours fields is output.
     * <p>
     * The text will be parsed case-insensitively.
     * <p>
     * Note: appending a separator discontinues any further work on the latest
     * appended field.
     *
     * @param text the text to use as a separator
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if this separator follows a previous one
     */
    public PeriodFormatterBuilder appendSeparatorIfFieldsAfter(String text) {
        return appendSeparator(text, text, null, false, true);
    }

    /**
     * Append a separator, which is output only if fields are printed before the separator.
     * <p>
     * For example,
     * <code>builder.appendDays().appendSeparatorIfFieldsBefore(",").appendHours()</code>
     * will only output the comma if the days fields is output.
     * <p>
     * The text will be parsed case-insensitively.
     * <p>
     * Note: appending a separator discontinues any further work on the latest
     * appended field.
     *
     * @param text the text to use as a separator
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if this separator follows a previous one
     */
    public PeriodFormatterBuilder appendSeparatorIfFieldsBefore(String text) {
        return appendSeparator(text, text, null, true, false);
    }

    public PeriodFormatterBuilder appendSeparator(String text, String finalText) {
        return appendSeparator(text, finalText, null, true, true);
    }

    /**
     * Append a separator, which is output if fields are printed both before
     * and after the separator.
     * <p>
     * This method changes the separator depending on whether it is the last separator
     * to be output.
     * <p>
     * For example,
     * <code>builder.appendDays().appendSeparator(",", "&amp;").appendHours()
     * .appendSeparator(",", "&amp;").appendMinutes()</code>
     * will output '1,2&amp;3' if all three fields are output, '1&amp;2' if two fields are output
     * and '1' if just one field is output.
     * <p>
     * The text will be parsed case-insensitively.
     * <p>
     * Note: appending a separator discontinues any further work on the latest
     * appended field.
     *
     * @param text      the text to use as a separator
     * @param finalText the text used used if this is the final separator to be printed
     * @param variants  set of text values which are also acceptable when parsed
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if this separator follows a previous one
     */
    public PeriodFormatterBuilder appendSeparator(String text, String finalText,
                                                  String[] variants) {
        return appendSeparator(text, finalText, variants, true, true);
    }

    private PeriodFormatterBuilder appendSeparator(String text, String finalText,
                                                   String[] variants,
                                                   boolean useBefore, boolean useAfter) {
        if (text == null || finalText == null) {
            throw new IllegalArgumentException();
        }

        clearPrefix();

        // optimise zero formatter case
        List<Object> pairs = iElementPairs;
        if (pairs.size() == 0) {
            if (useAfter && !useBefore) {
                Separator separator = new Separator(text, finalText, variants,
                        Literal.EMPTY, Literal.EMPTY, false, true);
                append0(separator, separator);
            }
            return this;
        }

        // find the last separator added
        int i;
        Separator lastSeparator = null;
        for (i = pairs.size(); --i >= 0; ) {
            if (pairs.get(i) instanceof Separator) {
                lastSeparator = (Separator) pairs.get(i);
                pairs = pairs.subList(i + 1, pairs.size());
                break;
            }
            i--;  // element pairs
        }

        // merge formatters
        if (lastSeparator != null && pairs.size() == 0) {
            throw new IllegalStateException("Cannot have two adjacent separators");
        } else {
            Object[] comp = createComposite(pairs);
            pairs.clear();
            Separator separator = new Separator(
                    text, finalText, variants,
                    (PeriodPrinter) comp[0], (PeriodParser) comp[1],
                    useBefore, useAfter);
            pairs.add(separator);
            pairs.add(separator);
        }

        return this;
    }

    private void clearPrefix() throws IllegalStateException {
        if (iPrefix != null) {
            throw new IllegalStateException("Prefix not followed by field");
        }
        iPrefix = null;
    }

    private PeriodFormatterBuilder append0(PeriodPrinter printer, PeriodParser parser) {
        iElementPairs.add(printer);
        iElementPairs.add(parser);
        iNotPrinter |= (printer == null);
        iNotParser |= (parser == null);
        return this;
    }

    /**
     * Defines a formatted field's prefix or suffix text.
     * This can be used for fields such as 'n hours' or 'nH' or 'Hour:n'.
     */
    interface PeriodFieldAffix {

        int calculatePrintedLength(int value);

        void printTo(StringBuilder buf, int value);

        void printTo(Writer out, int value) throws IOException;

        /**
         * @return new position after parsing affix, or ~position of failure
         */
        int parse(String periodStr, int position);

        /**
         * @return position where affix starts, or original ~position if not found
         */
        int scan(String periodStr, int position);

        /**
         * @return a copy of array of affixes
         */
        String[] getAffixes();

        /**
         * This method should be called only once.
         * After first call consecutive calls to this methods will have no effect.
         * Causes this affix to ignore a match (parse and scan
         * methods) if there is an affix in the passed list that holds
         * affix text which satisfy both following conditions:
         * - the affix text is also a match
         * - the affix text is longer than the match from this object
         *
         * @param affixesToIgnore affixes to ignore
         */
        void finish(Set<PeriodFieldAffix> affixesToIgnore);
    }

    /**
     * An affix that can be ignored.
     */
    abstract static class IgnorableAffix implements PeriodFieldAffix {
        private volatile String[] iOtherAffixes;

        public void finish(Set<PeriodFieldAffix> periodFieldAffixesToIgnore) {
            if (iOtherAffixes == null) {
                // Calculate the shortest affix in this instance.
                int shortestAffixLength = Integer.MAX_VALUE;
                String shortestAffix = null;
                for (String affix : getAffixes()) {
                    if (affix.length() < shortestAffixLength) {
                        shortestAffixLength = affix.length();
                        shortestAffix = affix;
                    }
                }

                // Pick only affixes that are longer than the shortest affix in this instance.
                // This will reduce the number of parse operations and thus speed up the PeriodFormatter.
                // also need to pick affixes that differ only in case (but not those that are identical)
                Set<String> affixesToIgnore = new HashSet<String>();
                for (PeriodFieldAffix periodFieldAffixToIgnore : periodFieldAffixesToIgnore) {
                    if (periodFieldAffixToIgnore != null) {
                        for (String affixToIgnore : periodFieldAffixToIgnore.getAffixes()) {
                            if (affixToIgnore.length() > shortestAffixLength ||
                                    (affixToIgnore.equalsIgnoreCase(shortestAffix) &&
                                            !affixToIgnore.equals(shortestAffix))) {
                                affixesToIgnore.add(affixToIgnore);
                            }
                        }
                    }
                }
                iOtherAffixes = affixesToIgnore.toArray(new String[affixesToIgnore.size()]);
            }
        }

        /**
         * Checks if there is a match among the other affixes (stored internally)
         * that is longer than the passed value (textLength).
         *
         * @param textLength the length of the match
         * @param periodStr  the Period string that will be parsed
         * @param position   the position in the Period string at which the parsing should be started.
         * @return true if the other affixes (stored internally) contain a match
         * that is longer than the textLength parameter, false otherwise
         */
        protected boolean matchesOtherAffix(int textLength, String periodStr, int position) {
            if (iOtherAffixes != null) {
                // ignore case when affix length differs
                // match case when affix length is same
                for (String affixToIgnore : iOtherAffixes) {
                    int textToIgnoreLength = affixToIgnore.length();
                    if ((textLength < textToIgnoreLength &&
                            periodStr.regionMatches(true, position, affixToIgnore, 0, textToIgnoreLength)) ||
                            (textLength == textToIgnoreLength &&
                                    periodStr.regionMatches(false, position, affixToIgnore, 0, textToIgnoreLength))) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Implements an affix where the text does not vary by the amount.
     */
    static class SimpleAffix extends IgnorableAffix {
        private final String iText;

        SimpleAffix(String text) {
            iText = text;
        }

        public int calculatePrintedLength(int value) {
            return iText.length();
        }

        public void printTo(StringBuilder buf, int value) {
            buf.append(iText);
        }

        public void printTo(Writer out, int value) throws IOException {
            out.write(iText);
        }

        public int parse(String periodStr, int position) {
            String text = iText;
            int textLength = text.length();
            if (periodStr.regionMatches(true, position, text, 0, textLength) &&
                !matchesOtherAffix(textLength, periodStr, position)) {
                return position + textLength;
            }
            return ~position;
        }

        public int scan(String periodStr, final int position) {
            String text = iText;
            int textLength = text.length();
            int sourceLength = periodStr.length();
            search:
            for (int pos = position; pos < sourceLength; pos++) {
                if (periodStr.regionMatches(true, pos, text, 0, textLength) &&
                        !matchesOtherAffix(textLength, periodStr, pos)) {
                    return pos;
                }
                // Only allow number characters to be skipped in search of suffix.
                switch (periodStr.charAt(pos)) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case '.':
                    case ',':
                    case '+':
                    case '-':
                        break;
                    default:
                        break search;
                }
            }
            return ~position;
        }

        public String[] getAffixes() {
            return new String[]{iText};
        }
    }

    /**
     * Implements an affix where the text varies by the amount of the field.
     * Only singular (1) and plural (not 1) are supported.
     */
    private static class PluralAffix extends IgnorableAffix {
        private final String iSingularText;
        private final String iPluralText;

        PluralAffix(String singularText, String pluralText) {
            iSingularText = singularText;
            iPluralText = pluralText;
        }

        public int calculatePrintedLength(int value) {
            return (value == 1 ? iSingularText : iPluralText).length();
        }

        public void printTo(StringBuilder buf, int value) {
            buf.append(value == 1 ? iSingularText : iPluralText);
        }

        public void printTo(Writer out, int value) throws IOException {
            out.write(value == 1 ? iSingularText : iPluralText);
        }

        public int parse(String periodStr, int position) {
            String text1 = iPluralText;
            String text2 = iSingularText;
            if (text1.length() < text2.length()) {
                // Swap in order to match longer one first.
                String temp = text1;
                text1 = text2;
                text2 = temp;
            }
            if (periodStr.regionMatches(true, position, text1, 0, text1.length()) &&
                    !matchesOtherAffix(text1.length(), periodStr, position)) {
                return position + text1.length();
            }
            if (periodStr.regionMatches(true, position, text2, 0, text2.length()) &&
                    !matchesOtherAffix(text2.length(), periodStr, position)) {
                return position + text2.length();
            }
            return ~position;
        }

        public int scan(String periodStr, final int position) {
            String text1 = iPluralText;
            String text2 = iSingularText;

            if (text1.length() < text2.length()) {
                // Swap in order to match longer one first.
                String temp = text1;
                text1 = text2;
                text2 = temp;
            }

            int textLength1 = text1.length();
            int textLength2 = text2.length();

            int sourceLength = periodStr.length();
            for (int pos = position; pos < sourceLength; pos++) {
                if (periodStr.regionMatches(true, pos, text1, 0, textLength1) &&
                        !matchesOtherAffix(text1.length(), periodStr, pos)) {
                    return pos;
                }
                if (periodStr.regionMatches(true, pos, text2, 0, textLength2) &&
                        !matchesOtherAffix(text2.length(), periodStr, pos)) {
                    return pos;
                }
            }
            return ~position;
        }

        public String[] getAffixes() {
            return new String[]{iSingularText, iPluralText};
        }
    }

    /**
     * Implements an affix where the text varies by the amount of the field.
     * Different amounts are supported based on the provided parameters.
     */
    private static class RegExAffix extends IgnorableAffix {
        private static final Comparator<String> LENGTH_DESC_COMPARATOR = new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        };

        private final String[] iSuffixes;
        private final Pattern[] iPatterns;

        // The parse method has to iterate over the suffixes from the longest one to the shortest one
        // Otherwise it might consume not enough characters.
        private final String[] iSuffixesSortedDescByLength;

        RegExAffix(String[] regExes, String[] texts) {
            iSuffixes = texts.clone();
            iPatterns = new Pattern[regExes.length];
            for (int i = 0; i < regExes.length; i++) {
                Pattern pattern = PATTERNS.get(regExes[i]);
                if (pattern == null) {
                    pattern = Pattern.compile(regExes[i]);
                    Pattern p = PATTERNS.putIfAbsent(regExes[i], pattern);
                }
                iPatterns[i] = pattern;
            }
            iSuffixesSortedDescByLength = iSuffixes.clone();
            Arrays.sort(iSuffixesSortedDescByLength, LENGTH_DESC_COMPARATOR);
        }

        private int selectSuffixIndex(int value) {
            String valueString = String.valueOf(value);
            for (int i = 0; i < iPatterns.length; i++) {
                if (iPatterns[i].matcher(valueString).matches()) {
                    return i;
                }
            }
            return iPatterns.length - 1;
        }

        @Override
        public int calculatePrintedLength(int value) {
            return iSuffixes[selectSuffixIndex(value)].length();
        }

        @Override
        public void printTo(StringBuilder buf, int value) {
            buf.append(iSuffixes[selectSuffixIndex(value)]);
        }

        @Override
        public void printTo(Writer out, int value) throws IOException {
            out.write(iSuffixes[selectSuffixIndex(value)]);
        }

        @Override
        public int parse(String periodStr, int position) {
            for (String text : iSuffixesSortedDescByLength) {
                if (periodStr.regionMatches(true, position, text, 0, text.length()) &&
                        !matchesOtherAffix(text.length(), periodStr, position)) {
                    return position + text.length();
                }
            }
            return ~position;
        }

        @Override
        public int scan(String periodStr, final int position) {
            int sourceLength = periodStr.length();
            for (int pos = position; pos < sourceLength; pos++) {
                for (String text : iSuffixesSortedDescByLength) {
                    if (periodStr.regionMatches(true, pos, text, 0, text.length()) &&
                            !matchesOtherAffix(text.length(), periodStr, pos)) {
                        return pos;
                    }
                }
            }
            return ~position;
        }

        @Override
        public String[] getAffixes() {
            return iSuffixes.clone();
        }
    }

    /**
     * Builds a composite affix by merging two other affix implementations.
     */
    private static class CompositeAffix extends IgnorableAffix {
        private final PeriodFieldAffix iLeft;
        private final PeriodFieldAffix iRight;
        private final String[] iLeftRightCombinations;

        CompositeAffix(PeriodFieldAffix left, PeriodFieldAffix right) {
            iLeft = left;
            iRight = right;

            // We need to construct all possible combinations of left and right.
            // We are doing it once in constructor so that getAffixes() is quicker.
            Set<String> result = new HashSet<String>();
            for (String leftText : iLeft.getAffixes()) {
                for (String rightText : iRight.getAffixes()) {
                    result.add(leftText + rightText);
                }
            }
            iLeftRightCombinations = result.toArray(new String[result.size()]);
        }

        @Override
        public int calculatePrintedLength(int value) {
            return iLeft.calculatePrintedLength(value)
                    + iRight.calculatePrintedLength(value);
        }

        @Override
        public void printTo(StringBuilder buf, int value) {
            iLeft.printTo(buf, value);
            iRight.printTo(buf, value);
        }

        @Override
        public void printTo(Writer out, int value) throws IOException {
            iLeft.printTo(out, value);
            iRight.printTo(out, value);
        }

        @Override
        public int parse(String periodStr, int position) {
            int pos = iLeft.parse(periodStr, position);
            if (pos >= 0) {
                pos = iRight.parse(periodStr, pos);
                if (pos >= 0 && matchesOtherAffix(parse(periodStr, pos) - pos, periodStr, position)) {
                    return ~position;
                }
            }
            return pos;
        }

        @Override
        public int scan(String periodStr, final int position) {
            int leftPosition = iLeft.scan(periodStr, position);
            if (leftPosition >= 0) {
                int rightPosition = iRight.scan(periodStr, iLeft.parse(periodStr, leftPosition));
                if (!(rightPosition >= 0 && matchesOtherAffix(iRight.parse(periodStr, rightPosition) -
                        leftPosition, periodStr, position))) {
                    if (leftPosition > 0) {
                        return leftPosition;
                    } else {
                        return rightPosition;
                    }
                }
            }
            return ~position;
        }

        @Override
        public String[] getAffixes() {
            return iLeftRightCombinations.clone();
        }
    }

    /**
     * Formats the numeric value of a field, potentially with prefix/suffix.
     */
    private static class FieldFormatter implements PeriodPrinter, PeriodParser {
        private final int iMinPrintedDigits;
        //private final int iPrintZeroSetting;
        private final int iMaxParsedDigits;
        private final boolean iRejectSignedValues;

        /**
         * The index of the field type, 0=year, etc.
         */
        private final ChronoUnit unit;
        /**
         * The array of the latest formatter added for each type.
         * This is shared between all the field formatters in a formatter.
         */
        //private final FieldFormatter[] iFieldFormatters;

        private final PeriodFieldAffix iPrefix;
        private final PeriodFieldAffix iSuffix;

        FieldFormatter(int minPrintedDigits,
                       int maxParsedDigits, boolean rejectSignedValues,
                       ChronoUnit chronoUnit,
                       PeriodFieldAffix prefix, PeriodFieldAffix suffix) {
            iMinPrintedDigits = minPrintedDigits;
            iMaxParsedDigits = maxParsedDigits;
            iRejectSignedValues = rejectSignedValues;
            this.unit = chronoUnit;
            iPrefix = prefix;
            iSuffix = suffix;
        }

        FieldFormatter(FieldFormatter field, PeriodFieldAffix suffix) {
            iMinPrintedDigits = field.iMinPrintedDigits;
            iMaxParsedDigits = field.iMaxParsedDigits;
            iRejectSignedValues = field.iRejectSignedValues;
            this.unit = field.unit;
            iPrefix = field.iPrefix;
            if (field.iSuffix != null) {
                suffix = new CompositeAffix(field.iSuffix, suffix);
            }
            iSuffix = suffix;
        }

        public void finish(FieldFormatter[] fieldFormatters) {
            Set<PeriodFieldAffix> prefixesToIgnore = new HashSet<>();
            Set<PeriodFieldAffix> suffixesToIgnore = new HashSet<>();
            for (FieldFormatter fieldFormatter : fieldFormatters) {
                if (fieldFormatter != null && !this.equals(fieldFormatter)) {
                    prefixesToIgnore.add(fieldFormatter.iPrefix);
                    suffixesToIgnore.add(fieldFormatter.iSuffix);
                }
            }
            // if we have a prefix then allow ignore behaviour
            if (iPrefix != null) {
                iPrefix.finish(prefixesToIgnore);
            }
            // if we have a suffix then allow ignore behaviour
            if (iSuffix != null) {
                iSuffix.finish(suffixesToIgnore);
            }
        }

        public int countFieldsToPrint(Period period, int stopAt, Locale locale) {
            if (stopAt <= 0) {
                return 0;
            }
            if (getFieldValue(period) != Long.MAX_VALUE) {
                return 1;
            }
            return 0;
        }

        public int calculatePrintedLength(Period period, Locale locale) {
            long valueLong = getFieldValue(period);
            if (valueLong == Long.MAX_VALUE) {
                return 0;
            }

            int sum = Math.max(FormatUtils.calculateDigitCount(valueLong), iMinPrintedDigits);
            int value = (int) valueLong;

            if (iPrefix != null) {
                sum += iPrefix.calculatePrintedLength(value);
            }
            if (iSuffix != null) {
                sum += iSuffix.calculatePrintedLength(value);
            }

            return sum;
        }

        public void printTo(StringBuilder buf, Period period, Locale locale) {
            long valueLong = getFieldValue(period);
            if (valueLong == Long.MAX_VALUE) {
                return;
            }
            int value = (int) valueLong;
            if (iPrefix != null) {
                iPrefix.printTo(buf, value);
            }
            int minDigits = iMinPrintedDigits;
            if (minDigits <= 1) {
                FormatUtils.appendUnpaddedInteger(buf, value);
            } else {
                FormatUtils.appendPaddedInteger(buf, value, minDigits);
            }
            if (iSuffix != null) {
                iSuffix.printTo(buf, value);
            }
        }

        public void printTo(Writer out, Period period, Locale locale) throws IOException {
            long valueLong = getFieldValue(period);
            if (valueLong == Long.MAX_VALUE) {
                return;
            }
            int value = (int) valueLong;
            if (iPrefix != null) {
                iPrefix.printTo(out, value);
            }
            int minDigits = iMinPrintedDigits;
            if (minDigits <= 1) {
                FormatUtils.writeUnpaddedInteger(out, value);
            } else {
                FormatUtils.writePaddedInteger(out, value, minDigits);
            }
            if (iSuffix != null) {
                iSuffix.printTo(out, value);
            }
        }

        public int parseInto(PeriodAmount period, String text, int position, Locale locale) {

            if (position >= text.length()) {
                return ~position;
            }

            if (iPrefix != null) {
                position = iPrefix.parse(text, position);
                if (position < 0) {
                    return position;
                }
            }
            int suffixPos = -1;
            int limit;
            limit = Math.min(iMaxParsedDigits, text.length() - position);
            int length = 0;
            int fractPos = -1;
            boolean hasDigits = false;
            boolean negative = false;
            while (length < limit) {
                char c = text.charAt(position + length);
                // leading sign
                if (length == 0 && (c == '-' || c == '+') && !iRejectSignedValues) {
                    negative = c == '-';

                    // Next character must be a digit.
                    if (length + 1 >= limit ||
                            (c = text.charAt(position + length + 1)) < '0' || c > '9') {
                        break;
                    }

                    if (negative) {
                        length++;
                    } else {
                        // Skip the '+' for parseInt to succeed.
                        position++;
                    }
                    // Expand the limit to disregard the sign character.
                    limit = Math.min(limit + 1, text.length() - position);
                    continue;
                }
                // main number
                if (c >= '0' && c <= '9') {
                    hasDigits = true;
                } else {
                    break;
                }
                length++;
            }

            if (!hasDigits) {
                return ~position;
            }
            setFieldValue(period, unit, parseInt(text, position, length));
            position += length;
            if (position >= 0 && iSuffix != null) {
                position = iSuffix.parse(text, position);
            }
            return position;
        }

        /**
         * @param text     text to parse
         * @param position position in text
         * @param length   exact count of characters to parse
         * @return parsed int value
         */
        private int parseInt(String text, int position, int length) {
            if (length >= 10) {
                return Integer.parseInt(text.substring(position, position + length));
            }
            if (length <= 0) {
                return 0;
            }
            int value = text.charAt(position++);
            length--;
            boolean negative;
            if (value == '-') {
                if (--length < 0) {
                    return 0;
                }
                negative = true;
                value = text.charAt(position++);
            } else {
                negative = false;
            }
            value -= '0';
            while (length-- > 0) {
                value = ((value << 3) + (value << 1)) + text.charAt(position++) - '0';
            }
            return negative ? -value : value;
        }

        /**
         * @return Long.MAX_VALUE if nothing to print, otherwise value
         */
        long getFieldValue(Period period) {
            long value;
            switch (unit) {
                default:
                    return Long.MAX_VALUE;
                case YEARS:
                    value = period.get(ChronoUnit.YEARS);
                    break;
                case MONTHS:
                    value = period.get(ChronoUnit.MONTHS);
                    break;
                case WEEKS:
                    value = period.get(ChronoUnit.WEEKS);
                    break;
                case DAYS:
                    value = period.get(ChronoUnit.DAYS);
                    break;
                case HOURS:
                    value = period.get(ChronoUnit.HOURS);
                    break;
                case MINUTES:
                    value = period.get(ChronoUnit.MINUTES);
                    break;
                case SECONDS:
                    value = period.get(ChronoUnit.SECONDS);
                    break;
                case MILLIS:
                    value = period.get(ChronoUnit.MILLIS);
                    break;
            }

            return value;
        }

        void setFieldValue(PeriodAmount period, ChronoUnit field, long value) {
            switch (field) {
                default:
                    break;
                case YEARS:
                    period.set(ChronoUnit.YEARS, value);
                    break;
                case MONTHS:
                    period.set(ChronoUnit.MONTHS, value);
                    break;
                case WEEKS:
                    period.set(ChronoUnit.WEEKS, value);
                    break;
                case DAYS:
                    period.set(ChronoUnit.DAYS, value);
                    break;
                case HOURS:
                    period.set(ChronoUnit.HOURS, value);
                    break;
                case MINUTES:
                    period.set(ChronoUnit.MINUTES, value);
                    break;
                case SECONDS:
                    period.set(ChronoUnit.SECONDS, value);
                    break;
                case MILLIS:
                    period.set(ChronoUnit.MILLIS, value);
                    break;
            }
        }

        ChronoUnit getFieldType() {
            return unit;
        }
    }

    /**
     * Handles a simple literal piece of text.
     */
    private static class Literal implements PeriodPrinter, PeriodParser {
        static final Literal EMPTY = new Literal("");
        private final String iText;

        Literal(String text) {
            iText = text;
        }

        public int countFieldsToPrint(Period period, int stopAt, Locale locale) {
            return 0;
        }

        public int calculatePrintedLength(Period period, Locale locale) {
            return iText.length();
        }

        public void printTo(StringBuilder buf, Period period, Locale locale) {
            buf.append(iText);
        }

        public void printTo(Writer out, Period period, Locale locale) throws IOException {
            out.write(iText);
        }

        public int parseInto(PeriodAmount period, String periodStr,
                             int position, Locale locale) {
            if (periodStr.regionMatches(true, position, iText, 0, iText.length())) {
                return position + iText.length();
            }
            return ~position;
        }
    }

    /**
     * Handles a separator, that splits the fields into multiple parts.
     * For example, the 'T' in the ISO8601 standard.
     */
    private static class Separator implements PeriodPrinter, PeriodParser {
        private final String iText;
        private final String iFinalText;
        private final String[] iParsedForms;

        private final boolean iUseBefore;
        private final boolean iUseAfter;

        private final PeriodPrinter iBeforePrinter;
        private final PeriodParser iBeforeParser;
        private volatile PeriodPrinter iAfterPrinter;
        private volatile PeriodParser iAfterParser;

        Separator(String text, String finalText, String[] variants,
                  PeriodPrinter beforePrinter, PeriodParser beforeParser,
                  boolean useBefore, boolean useAfter) {
            iText = text;
            iFinalText = finalText;

            if ((finalText == null || text.equals(finalText)) &&
                    (variants == null || variants.length == 0)) {

                iParsedForms = new String[]{text};
            } else {
                // Filter and reverse sort the parsed forms.
                TreeSet<String> parsedSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                parsedSet.add(text);
                parsedSet.add(finalText);
                if (variants != null) {
                    for (int i = variants.length; --i >= 0; ) {
                        parsedSet.add(variants[i]);
                    }
                }
                ArrayList<String> parsedList = new ArrayList<String>(parsedSet);
                Collections.reverse(parsedList);
                iParsedForms = parsedList.toArray(new String[parsedList.size()]);
            }

            iBeforePrinter = beforePrinter;
            iBeforeParser = beforeParser;
            iUseBefore = useBefore;
            iUseAfter = useAfter;
        }

        public int countFieldsToPrint(Period period, int stopAt, Locale locale) {
            int sum = iBeforePrinter.countFieldsToPrint(period, stopAt, locale);
            if (sum < stopAt) {
                sum += iAfterPrinter.countFieldsToPrint(period, stopAt, locale);
            }
            return sum;
        }

        public int calculatePrintedLength(Period period, Locale locale) {
            PeriodPrinter before = iBeforePrinter;
            PeriodPrinter after = iAfterPrinter;

            int sum = before.calculatePrintedLength(period, locale)
                    + after.calculatePrintedLength(period, locale);

            if (iUseBefore) {
                if (before.countFieldsToPrint(period, 1, locale) > 0) {
                    if (iUseAfter) {
                        int afterCount = after.countFieldsToPrint(period, 2, locale);
                        if (afterCount > 0) {
                            sum += (afterCount > 1 ? iText : iFinalText).length();
                        }
                    } else {
                        sum += iText.length();
                    }
                }
            } else if (iUseAfter && after.countFieldsToPrint(period, 1, locale) > 0) {
                sum += iText.length();
            }

            return sum;
        }

        public void printTo(StringBuilder buf, Period period, Locale locale) {
            PeriodPrinter before = iBeforePrinter;
            PeriodPrinter after = iAfterPrinter;

            before.printTo(buf, period, locale);
            if (iUseBefore) {
                if (before.countFieldsToPrint(period, 1, locale) > 0) {
                    if (iUseAfter) {
                        int afterCount = after.countFieldsToPrint(period, 2, locale);
                        if (afterCount > 0) {
                            buf.append(afterCount > 1 ? iText : iFinalText);
                        }
                    } else {
                        buf.append(iText);
                    }
                }
            } else if (iUseAfter && after.countFieldsToPrint(period, 1, locale) > 0) {
                buf.append(iText);
            }
            after.printTo(buf, period, locale);
        }

        public void printTo(Writer out, Period period, Locale locale) throws IOException {
            PeriodPrinter before = iBeforePrinter;
            PeriodPrinter after = iAfterPrinter;

            before.printTo(out, period, locale);
            if (iUseBefore) {
                if (before.countFieldsToPrint(period, 1, locale) > 0) {
                    if (iUseAfter) {
                        int afterCount = after.countFieldsToPrint(period, 2, locale);
                        if (afterCount > 0) {
                            out.write(afterCount > 1 ? iText : iFinalText);
                        }
                    } else {
                        out.write(iText);
                    }
                }
            } else if (iUseAfter && after.countFieldsToPrint(period, 1, locale) > 0) {
                out.write(iText);
            }
            after.printTo(out, period, locale);
        }

        public int parseInto(PeriodAmount period, String periodStr,
                             int position, Locale locale) {
            int oldPos = position;
            position = iBeforeParser.parseInto(period, periodStr, position, locale);

            if (position < 0) {
                return position;
            }

            boolean found = false;
            int parsedFormLength = -1;
            if (position > oldPos) {
                // Consume this separator.
                for (String parsedForm : iParsedForms) {
                    if ((parsedForm == null || parsedForm.length() == 0) ||
                            periodStr.regionMatches(true, position, parsedForm, 0, parsedForm.length())) {
                        parsedFormLength = (parsedForm == null ? 0 : parsedForm.length());
                        position += parsedFormLength;
                        found = true;
                        break;
                    }
                }
            }
            oldPos = position;
            position = iAfterParser.parseInto(period, periodStr, position, locale);
            if (position < 0) {
                return position;
            }
            if (found && position == oldPos && parsedFormLength > 0) {
                // Separator should not have been supplied.
                return ~oldPos;
            }

            if (position > oldPos && !found && !iUseBefore) {
                // Separator was required.
                return ~oldPos;
            }

            return position;
        }

        Separator finish(PeriodPrinter afterPrinter, PeriodParser afterParser) {
            iAfterPrinter = afterPrinter;
            iAfterParser = afterParser;
            return this;
        }
    }

    /**
     * Composite implementation that merges other fields to create a full pattern.
     */
    private static class Composite implements PeriodPrinter, PeriodParser {

        private final PeriodPrinter[] iPrinters;
        private final PeriodParser[] iParsers;

        Composite(List<Object> elementPairs) {
            List<Object> printerList = new ArrayList<>();
            List<Object> parserList = new ArrayList<>();

            decompose(elementPairs, printerList, parserList);

            if (printerList.size() <= 0) {
                iPrinters = null;
            } else {
                iPrinters = printerList.toArray(new PeriodPrinter[printerList.size()]);
            }

            if (parserList.size() <= 0) {
                iParsers = null;
            } else {
                iParsers = parserList.toArray(new PeriodParser[parserList.size()]);
            }
        }

        public int countFieldsToPrint(Period period, int stopAt, Locale locale) {
            int sum = 0;
            PeriodPrinter[] printers = iPrinters;
            for (int i = printers.length; sum < stopAt && --i >= 0; ) {
                sum += printers[i].countFieldsToPrint(period, Integer.MAX_VALUE, locale);
            }
            return sum;
        }

        public int calculatePrintedLength(Period period, Locale locale) {
            int sum = 0;
            PeriodPrinter[] printers = iPrinters;
            for (int i = printers.length; --i >= 0; ) {
                sum += printers[i].calculatePrintedLength(period, locale);
            }
            return sum;
        }

        public void printTo(StringBuilder buf, Period period, Locale locale) {
            PeriodPrinter[] printers = iPrinters;
            int len = printers.length;
            for (PeriodPrinter printer : printers) {
                printer.printTo(buf, period, locale);
            }
        }

        public void printTo(Writer out, Period period, Locale locale) throws IOException {
            PeriodPrinter[] printers = iPrinters;
            int len = printers.length;
            for (PeriodPrinter printer : printers) {
                printer.printTo(out, period, locale);
            }
        }

        public int parseInto(PeriodAmount period, String periodStr, int position, Locale locale) {
            PeriodParser[] parsers = iParsers;
            if (parsers == null) {
                throw new UnsupportedOperationException();
            }

            int len = parsers.length;
            for (int i = 0; i < len && position >= 0; i++) {
                position = parsers[i].parseInto(period, periodStr, position, locale);
            }
            return position;
        }

        private void decompose(List<Object> elementPairs, List<Object> printerList, List<Object> parserList) {
            int size = elementPairs.size();
            for (int i = 0; i < size; i += 2) {
                Object element = elementPairs.get(i);
                if (element instanceof PeriodPrinter) {
                    if (element instanceof Composite) {
                        addArrayToList(printerList, ((Composite) element).iPrinters);
                    } else {
                        printerList.add(element);
                    }
                }

                element = elementPairs.get(i + 1);
                if (element instanceof PeriodParser) {
                    if (element instanceof Composite) {
                        addArrayToList(parserList, ((Composite) element).iParsers);
                    } else {
                        parserList.add(element);
                    }
                }
            }
        }

        private void addArrayToList(List<Object> list, Object[] array) {
            if (array != null) {
                Collections.addAll(list, array);
            }
        }
    }
}
