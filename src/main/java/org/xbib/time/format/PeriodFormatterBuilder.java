package org.xbib.time.format;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    private int iMinPrintedDigits;

    private int iMaxParsedDigits;

    private boolean iRejectSignedValues;

    private PeriodFieldAffix iPrefix;

    private List<Object> iElementPairs;
    /**
     * Set to true if the formatter is not a printer.
     */
    private boolean iNotPrinter;
    /**
     * Set to true if the formatter is not a parser.
     */
    private boolean iNotParser;

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
        if (elementPairs.size() == 0) {
            return new Object[]{Literal.EMPTY, Literal.EMPTY};
        }
        Composite comp = new Composite(elementPairs);
        return new Object[]{comp, comp};
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
        return toFormatter(iElementPairs, iNotPrinter, iNotParser);
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
     * @param periodFieldAffix custom prefix
     * @return this PeriodFormatterBuilder
     * @see #appendSuffix
     */
    private PeriodFormatterBuilder appendPrefix(PeriodFieldAffix periodFieldAffix) {
        PeriodFieldAffix prefix = periodFieldAffix;
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
        List<Object> pairs = iElementPairs;
        if (pairs.isEmpty()) {
            if (useAfter && !useBefore) {
                Separator separator = new Separator(text, finalText, variants,
                        Literal.EMPTY, Literal.EMPTY, false, true);
                append0(separator, separator);
            }
            return this;
        }
        int i;
        Separator lastSeparator = null;
        for (i = pairs.size(); --i >= 0; ) {
            if (pairs.get(i) instanceof Separator) {
                lastSeparator = (Separator) pairs.get(i);
                pairs = pairs.subList(i + 1, pairs.size());
                break;
            }
            i--;
        }
        if (lastSeparator != null && pairs.isEmpty()) {
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
    }

    private PeriodFormatterBuilder append0(PeriodPrinter printer, PeriodParser parser) {
        iElementPairs.add(printer);
        iElementPairs.add(parser);
        iNotPrinter |= (printer == null);
        iNotParser |= (parser == null);
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
     * @param suffix custom suffix
     * @return this PeriodFormatterBuilder
     * @throws IllegalStateException if no field exists to append to
     * @see #appendPrefix
     */
    private PeriodFormatterBuilder appendSuffix(PeriodFieldAffix suffix) {
        final Object originalPrinter;
        final Object originalParser;
        if (!iElementPairs.isEmpty()) {
            originalPrinter = iElementPairs.get(iElementPairs.size() - 2);
            originalParser = iElementPairs.get(iElementPairs.size() - 1);
        } else {
            originalPrinter = null;
            originalParser = null;
        }
        if (originalPrinter != originalParser || !(originalPrinter instanceof FieldFormatter)) {
            throw new IllegalStateException("No field to apply suffix to");
        }
        clearPrefix();
        FieldFormatter newField = new FieldFormatter((FieldFormatter) originalPrinter, suffix);
        iElementPairs.set(iElementPairs.size() - 2, newField);
        iElementPairs.set(iElementPairs.size() - 1, newField);
        return this;
    }
}
