/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 1, 2002
 * Time: 10:28:19 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.math;

/**
 * Maps input values from a range of values linearly into values between zero and one.
 * If the value is less than the minimum, the output will be zero.
 * If the value is greater than the maximum, the output will be one.
 * <pre>
 *     |
 * 1.0 |           /-----
 *     |         / .
 *     |       /   .
 * 0.0 |-----/     .
 *     |     .     .
 *     +-----------------
 *          min   max
 * </pre>
 */
public class RangeMapper
{
    private double min;
    private double max;
    private double range;
    private static final double ONE = 1.0;
    private static final double ZERO = 0.0;

    /**
     * Creates a new range mapper, given the range.
     * @param min       The minimum value in the range.
     * @param max       The maximum value in the range.
     */
    public RangeMapper(double min, double max)
    {
        this.min = min;
        this.max = max;
        range = max - min;
        if (range <= ZERO)
            throw new IllegalArgumentException("Range cannot be <= 0");
    }

    /**
     * Returns the output value, given the input.
     * @param value     The output value.
     * @return double   The mapped value, between zero and one.
     */
    public double mapValue(double value)
    {
        double v = value - min;
        if (v < ZERO) return ZERO;
        v = v / range;
        return (v > ONE) ? ONE : v;
    }

    public double getMin()
    {
        return min;
    }

    public double getMax()
    {
        return max;
    }

    public double getRange()
    {
        return range;
    }
}
