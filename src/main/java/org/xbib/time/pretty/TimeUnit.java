package org.xbib.time.pretty;

/**
 * Defines a Unit of time (e.g. seconds, minutes, hours) and its conversion to milliseconds.
 */
public interface TimeUnit {

    /**
     * The number of milliseconds represented by each instance of this TimeUnit.
     * Must be a positive number greater than zero.
     * @return millis
     */
    long getMillisPerUnit();

    /**
     * The maximum quantity of this Unit to be used as a threshold for the next
     * largest Unit (e.g. if one <code>Second</code> represents 1000ms, and
     * <code>Second</code> has a maxQuantity of 5, then if the difference
     * between compared timestamps is larger than 5000ms, PrettyTime will move
     * on to the next smallest TimeUnit for calculation; <code>Minute</code>, by
     * default)
     * <p>
     * millisPerUnit * maxQuantity = maxAllowedMs
     * <p>
     * If maxQuantity is zero, it will be equal to the next highest
     * <code>TimeUnit.getMillisPerUnit() /
     * this.getMillisPerUnit()</code> or infinity if there are no greater
     * TimeUnits.
     * @return quantity
     */
    long getMaxQuantity();
}
