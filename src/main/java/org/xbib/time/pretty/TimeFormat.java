package org.xbib.time.pretty;

public interface TimeFormat {
    /**
     * Given a populated {@link TimeUnitQuantity} object. Apply formatting (with rounding) and output the result.
     *
     * @param timeUnitQuantity The original {@link TimeUnitQuantity} instance from which the time string should be decorated.
     * @return the formatted string
     */
    String format(final TimeUnitQuantity timeUnitQuantity);

    /**
     * Given a populated {@link TimeUnitQuantity} object. Apply formatting (without rounding) and output the result.
     *
     * @param timeUnitQuantity The original {@link TimeUnitQuantity} instance from which the time string should be decorated.
     * @return the formatted string
     */
    String formatUnrounded(TimeUnitQuantity timeUnitQuantity);

    /**
     * Decorate with past or future prefix/suffix (with rounding).
     *
     * @param timeUnitQuantity The original {@link TimeUnitQuantity} instance from which the time string should be decorated.
     * @param time             The formatted time string.
     * @return the formatted string
     */
    String decorate(TimeUnitQuantity timeUnitQuantity, String time);

    /**
     * Decorate with past or future prefix/suffix (without rounding).
     *
     * @param timeUnitQuantity The original {@link TimeUnitQuantity} instance from which the time string should be decorated.
     * @param time             The formatted time string.
     * @return the formatted string
     */
    String decorateUnrounded(TimeUnitQuantity timeUnitQuantity, String time);

}
