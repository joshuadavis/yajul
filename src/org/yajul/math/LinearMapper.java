/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jul 28, 2002
 * Time: 11:17:10 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.math;

/**
 * Linearly maps coordinates from one system into another, given the minimum and maximum values
 * for both systems.
 * @author Joshua Davis
 */
public class LinearMapper
{
    private float m;    // Slope
    private float b;    // Offset

    /**
     * Creates the mapper, given the minimum and maximum values for both systems.
     * @param min1 - The minimum of the target system.
     * @param max1 - The maximum of the target system.
     * @param min2 - The minimum of the source system.
     * @param max2 - The maximum of the target system.
     */
    public LinearMapper(float min1, float max1, float min2, float max2)
    {
        float u = min1;
        float v = max1;
        float q = min2;
        float r = max2;
        m = getLinearSlope(u,v,q,r);
        b = getLinearOffset(m,u,q);
    }

    /**
     * Converts the number into the target coordinate system.
     * @param x - The value in the 'source' coordinate system.
     * @return float - The target value.
     */
    public float convert(float x)
    {
        return m * x  + b;
    }

    /**
     * Returns the slope being used to convert the coordinate systems.
     * @return float - The slope (m).
     */
    public float getSlope()
    {
        return m;

    }

    /**
     * Returns the offset being used to convert the coordinate systems.
     * @return float - The offset (b).
     */
    public float getOffset()
    {
        return b;
    }

    /**
     * Calculates the offset, given the slope and the x coordinates of two points.
     * @param m         The slope between the two points.
     * @param u         The minimum x coordinate (point a).
     * @param q         The minimum y coordinate (point a).
     * @return float    The offset of (u,q) and (v,r).
     */
    public static final float getLinearOffset(float m, float u, float q)
    {
        // Substitute u and q into y = m * x + b, solve for b:
        // q = m * u + b
        // -b = m * u - q
        // b = - (m * u - q)
        float b = - ((m * u) - q);
        return b;
    }

    /**
     * Calcualtes the slope, given the two points.
     * @param u         The minimum x coordinate (point a).
     * @param v         The maximum x coordinate (point b).
     * @param q         The minimum y coordinate (point a).
     * @param r         The maximum y coordinate (point b).
     * @return float    The slope of (u,q) and (v,r).
     */
    public static final float getLinearSlope(float u, float v, float q, float r)
    {
        // u = m * q + b
        // v = m * r + b
        // Solve for m
        // u - v = (m * q) - (m * r) + b - b
        // u - v = (m * q) - (m * r)
        // u - v = m * (q - r)
        // u - v / (q - r) = m
        // If (q - r) == 0, m is zero.
        float divisor = (q - r);
        float m = (divisor == 0) ? 0 : (u - v) / divisor;
        return m;
    }
}
