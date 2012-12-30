package org.yajul.util;

/**
 * Numerical helper functions.
 * <br>
 * User: josh
 * Date: Jun 3, 2010
 * Time: 1:28:55 PM
 */
public class MathUtil
{
    public static final double TEN = 10.0;

    private static final int I2 = 2;
    private static final int I3 = 3;
    private static final int I4 = 4;
    private static final int I5 = 5;

    private static double[] POS_POWERS_OF_TEN =
            {
                    Math.pow(TEN, 0),
                    Math.pow(TEN, 1),
                    Math.pow(TEN, I2),
                    Math.pow(TEN, I3),
                    Math.pow(TEN, I4),
                    Math.pow(TEN, I5),
            };
    private static double[] NEG_POWERS_OF_TEN =
            {
                    Math.pow(TEN, 0),
                    Math.pow(TEN, -1),
                    Math.pow(TEN, -I2),
                    Math.pow(TEN, -I3),
                    Math.pow(TEN, -I4),
                    Math.pow(TEN, -I5),
            };

    /**
     * Quickly return a power of ten.
     *
     * @param power the power of ten, can be negative
     * @return ten to the (power).
     */
    public static double powerOfTen(int power)
    {
        int index;
        double[] array;
        if (power < 0)
        {
            index = -power;
            array = NEG_POWERS_OF_TEN;
        }
        else
        {
            index = power;
            array = POS_POWERS_OF_TEN;
        }
        return (index < array.length) ?
               array[index] :
               Math.pow(TEN, power);
    }

    /**
     * Round to the specified number of decimals.
     * @param v the value
     * @param decimals number of decimals
     * @return the value, rounded to the specified number of decimals
     */
    public static double rint(double v, int decimals)
    {
        if (decimals == 0)
        {
            return Math.rint(v);
        }
        else
        {
            double factor = powerOfTen(decimals);
            double x = Math.rint(v * factor);
            x = x / factor;
            return x;
        }
    }
    
    public static int doubleHashCode(double d)
    {
        long bits = Double.doubleToLongBits(d);
        return (int) (bits ^ (bits >>> 32));
    }
}
