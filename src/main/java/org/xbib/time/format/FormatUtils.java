package org.xbib.time.format;

import java.io.IOException;
import java.io.Writer;

/**
 * Utility methods used by formatters.
 * FormatUtils is thread-safe and immutable.
 */
public class FormatUtils {

    private static final double LOG_10 = Math.log(10);

    /**
     * Restricted constructor.
     */
    private FormatUtils() {
    }

    /**
     * Converts an integer to a string, prepended with a variable amount of '0'
     * pad characters, and appends it to the given buffer.
     * This method is optimized for converting small values to strings.
     *
     * @param buf   receives integer converted to a string
     * @param value value to convert to a string
     * @param size  minimum amount of digits to append
     * @throws IOException if appending fails
     */
    public static void appendPaddedInteger(StringBuilder buf, int value, int size) throws IOException {
        appendPaddedInteger((Appendable) buf, value, size);
    }

    /**
     * Converts an integer to a string, prepended with a variable amount of '0'
     * pad characters, and appends it to the given appendable.
     * This method is optimized for converting small values to strings.
     *
     * @param appenadble receives integer converted to a string
     * @param intvalue      value to convert to a string
     * @param digits       minimum amount of digits to append
     * @throws IOException exception
     */
    public static void appendPaddedInteger(Appendable appenadble, int intvalue, int digits) throws IOException {
        int value = intvalue;
        int size = digits;
        if (value < 0) {
            appenadble.append('-');
            if (value != Integer.MIN_VALUE) {
                value = -value;
            } else {
                for (; size > 10; size--) {
                    appenadble.append('0');
                }
                appenadble.append(Long.toString(Integer.MIN_VALUE));
                return;
            }
        }
        if (value < 10) {
            for (; size > 1; size--) {
                appenadble.append('0');
            }
            appenadble.append((char) (value + '0'));
        } else if (value < 100) {
            for (; size > 2; size--) {
                appenadble.append('0');
            }
            // Calculate value div/mod by 10 without using two expensive
            // division operations. (2 ^ 27) / 10 = 13421772. Add one to
            // value to correct rounding error.
            int d = ((value + 1) * 13421772) >> 27;
            appenadble.append((char) (d + '0'));
            // Append remainder by calculating (value - d * 10).
            appenadble.append((char) (value - (d << 3) - (d << 1) + '0'));
        } else {
            int d;
            if (value < 1000) {
                d = 3;
            } else if (value < 10000) {
                d = 4;
            } else {
                d = (int) (Math.log(value) / LOG_10) + 1;
            }
            for (; size > d; size--) {
                appenadble.append('0');
            }
            appenadble.append(Integer.toString(value));
        }
    }

    /**
     * Converts an integer to a string, prepended with a variable amount of '0'
     * pad characters, and appends it to the given buffer.
     * This method is optimized for converting small values to strings.
     *
     * @param buf   receives integer converted to a string
     * @param value value to convert to a string
     * @param size  minimum amount of digits to append
     * @throws IOException if appending fails
     */
    public static void appendPaddedInteger(StringBuilder buf, long value, int size) throws IOException {
        appendPaddedInteger((Appendable) buf, value, size);
    }

    /**
     * Converts an integer to a string, prepended with a variable amount of '0'
     * pad characters, and appends it to the given buffer.
     * This method is optimized for converting small values to strings.
     *
     * @param appendable receives integer converted to a string
     * @param longvalue      value to convert to a string
     * @param digits       minimum amount of digits to append
     * @throws IOException exception
     */
    public static void appendPaddedInteger(Appendable appendable, long longvalue, int digits) throws IOException {
        long value = longvalue;
        int intValue = (int) value;
        int size = digits;
        if (intValue == value) {
            appendPaddedInteger(appendable, intValue, size);
        } else if (size <= 19) {
            appendable.append(Long.toString(value));
        } else {
            if (value < 0) {
                appendable.append('-');
                if (value != Long.MIN_VALUE) {
                    value = -value;
                } else {
                    for (; size > 19; size--) {
                        appendable.append('0');
                    }
                    appendable.append("9223372036854775808");
                    return;
                }
            }
            int d = (int) (Math.log(value) / LOG_10) + 1;
            for (; size > d; size--) {
                appendable.append('0');
            }
            appendable.append(Long.toString(value));
        }
    }

    /**
     * Converts an integer to a string, prepended with a variable amount of '0'
     * pad characters, and writes it to the given writer.
     * This method is optimized for converting small values to strings.
     *
     * @param out   receives integer converted to a string
     * @param val value to convert to a string
     * @param digits  minimum amount of digits to append
     * @throws IOException exception
     */
    public static void writePaddedInteger(Writer out, int val, int digits)
            throws IOException {
        int value = val;
        int size = digits;
        if (value < 0) {
            out.write('-');
            if (value != Integer.MIN_VALUE) {
                value = -value;
            } else {
                for (; size > 10; size--) {
                    out.write('0');
                }
                //out.write("" + -(long) Integer.MIN_VALUE);
                out.write(Long.toString(Integer.MIN_VALUE));
                return;
            }
        }
        if (value < 10) {
            for (; size > 1; size--) {
                out.write('0');
            }
            out.write(value + '0');
        } else if (value < 100) {
            for (; size > 2; size--) {
                out.write('0');
            }
            // Calculate value div/mod by 10 without using two expensive
            // division operations. (2 ^ 27) / 10 = 13421772. Add one to
            // value to correct rounding error.
            int d = ((value + 1) * 13421772) >> 27;
            out.write(d + '0');
            // Append remainder by calculating (value - d * 10).
            out.write(value - (d << 3) - (d << 1) + '0');
        } else {
            int d;
            if (value < 1000) {
                d = 3;
            } else if (value < 10000) {
                d = 4;
            } else {
                d = (int) (Math.log(value) / LOG_10) + 1;
            }
            for (; size > d; size--) {
                out.write('0');
            }
            out.write(Integer.toString(value));
        }
    }

    /**
     * Converts an integer to a string, prepended with a variable amount of '0'
     * pad characters, and writes it to the given writer.
     * This method is optimized for converting small values to strings.
     *
     * @param out   receives integer converted to a string
     * @param longvalue value to convert to a string
     * @param digits  minimum amount of digits to append
     * @throws IOException exception
     */
    public static void writePaddedInteger(Writer out, long longvalue, int digits) throws IOException {
        long value = longvalue;
        int size = digits;
        int intValue = (int) value;
        if (intValue == value) {
            writePaddedInteger(out, intValue, size);
        } else if (size <= 19) {
            out.write(Long.toString(value));
        } else {
            if (value < 0) {
                out.write('-');
                if (value != Long.MIN_VALUE) {
                    value = -value;
                } else {
                    for (; size > 19; size--) {
                        out.write('0');
                    }
                    out.write("9223372036854775808");
                    return;
                }
            }
            int d = (int) (Math.log(value) / LOG_10) + 1;
            for (; size > d; size--) {
                out.write('0');
            }
            out.write(Long.toString(value));
        }
    }

    /**
     * Converts an integer to a string, and appends it to the given buffer.
     * This method is optimized for converting small values to strings.
     *
     * @param buf   receives integer converted to a string
     * @param value value to convert to a string
     * @throws IOException if appending fails
     */
    public static void appendUnpaddedInteger(StringBuilder buf, int value) throws IOException {
        appendUnpaddedInteger((Appendable) buf, value);
    }

    /**
     * Converts an integer to a string, and appends it to the given appendable.
     * This method is optimized for converting small values to strings.
     *
     * @param appendable receives integer converted to a string
     * @param val      value to convert to a string
     * @throws IOException exception
     */
    public static void appendUnpaddedInteger(Appendable appendable, int val) throws IOException {
        int value = val;
        if (value < 0) {
            appendable.append('-');
            if (value != Integer.MIN_VALUE) {
                value = -value;
            } else {
                appendable.append(Long.toString(Integer.MIN_VALUE));
                return;
            }
        }
        if (value < 10) {
            appendable.append((char) (value + '0'));
        } else if (value < 100) {
            // Calculate value div/mod by 10 without using two expensive
            // division operations. (2 ^ 27) / 10 = 13421772. Add one to
            // value to correct rounding error.
            int d = ((value + 1) * 13421772) >> 27;
            appendable.append((char) (d + '0'));
            // Append remainder by calculating (value - d * 10).
            appendable.append((char) (value - (d << 3) - (d << 1) + '0'));
        } else {
            appendable.append(Integer.toString(value));
        }
    }

    /**
     * Converts an integer to a string, and appends it to the given buffer.
     * This method is optimized for converting small values to strings.
     *
     * @param buf   receives integer converted to a string
     * @param value value to convert to a string
     * @throws IOException if appending fails
     */
    public static void appendUnpaddedInteger(StringBuilder buf, long value) throws IOException {
        appendUnpaddedInteger((Appendable) buf, value);
    }

    /**
     * Converts an integer to a string, and appends it to the given appendable.
     * This method is optimized for converting small values to strings.
     *
     * @param appendable receives integer converted to a string
     * @param value      value to convert to a string
     * @throws IOException exception
     */
    public static void appendUnpaddedInteger(Appendable appendable, long value) throws IOException {
        int intValue = (int) value;
        if (intValue == value) {
            appendUnpaddedInteger(appendable, intValue);
        } else {
            appendable.append(Long.toString(value));
        }
    }

    /**
     * Converts an integer to a string, and writes it to the given writer.
     * This method is optimized for converting small values to strings.
     *
     * @param out   receives integer converted to a string
     * @param val value to convert to a string
     * @throws IOException exception
     */
    public static void writeUnpaddedInteger(Writer out, int val)
            throws IOException {
        int value = val;
        if (value < 0) {
            out.write('-');
            if (value != Integer.MIN_VALUE) {
                value = -value;
            } else {
                out.write(Long.toString(Integer.MIN_VALUE));
                return;
            }
        }
        if (value < 10) {
            out.write(value + '0');
        } else if (value < 100) {
            // Calculate value div/mod by 10 without using two expensive
            // division operations. (2 ^ 27) / 10 = 13421772. Add one to
            // value to correct rounding error.
            int d = ((value + 1) * 13421772) >> 27;
            out.write(d + '0');
            // Append remainder by calculating (value - d * 10).
            out.write(value - (d << 3) - (d << 1) + '0');
        } else {
            out.write(Integer.toString(value));
        }
    }

    /**
     * Converts an integer to a string, and writes it to the given writer.
     * This method is optimized for converting small values to strings.
     *
     * @param out   receives integer converted to a string
     * @param value value to convert to a string
     * @throws IOException exception
     */
    public static void writeUnpaddedInteger(Writer out, long value)
            throws IOException {
        int intValue = (int) value;
        if (intValue == value) {
            writeUnpaddedInteger(out, intValue);
        } else {
            out.write(Long.toString(value));
        }
    }

    /**
     * Calculates the number of decimal digits for the given value,
     * including the sign.
     *
     * @param value value
     * @return the number of decimal digits
     */
    public static int calculateDigitCount(long value) {
        if (value < 0) {
            if (value != Long.MIN_VALUE) {
                return calculateDigitCount(-value) + 1;
            } else {
                return 20;
            }
        }
        return value < 10 ? 1 : value < 100 ? 2 : value < 1000 ? 3 : value < 10000 ? 4 :
                                                ((int) (Math.log(value) / LOG_10) + 1);
    }

    static int parseTwoDigits(CharSequence text, int position) {
        int value = text.charAt(position) - '0';
        return ((value << 3) + (value << 1)) + text.charAt(position + 1) - '0';
    }

    static String createErrorMessage(final String text, final int errorPos) {
        int sampleLen = errorPos + 32;
        String sampleText;
        if (text.length() <= sampleLen + 3) {
            sampleText = text;
        } else {
            sampleText = text.substring(0, sampleLen).concat("...");
        }

        if (errorPos <= 0) {
            return "Invalid format: \"" + sampleText + '"';
        }

        if (errorPos >= text.length()) {
            return "Invalid format: \"" + sampleText + "\" is too short";
        }

        return "Invalid format: \"" + sampleText + "\" is malformed at \"" +
                sampleText.substring(errorPos) + '"';
    }
}
