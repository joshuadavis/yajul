package org.yajul.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Random;
import static org.junit.Assert.*;

/**
 * Tests for MathUtil.
 * <br>
 * User: josh
 * Date: Jun 3, 2010
 * Time: 1:53:21 PM
 */
public class MathUtilTest {
    private static final double EPSILON = 1e-9;

    @Test
    public void testRounding() {
        assertEquals("rint 1.235", 1.24, MathUtil.rint(1.235, 2), EPSILON);
        assertEquals("rint 1.2349", 1.23, MathUtil.rint(1.2349, 2), EPSILON);
        assertEquals("rint -1.235", -1.24, MathUtil.rint(-1.235, 2), EPSILON);
        assertEquals("rint -1.2349", -1.23, MathUtil.rint(-1.2349, 2), EPSILON);

        assertEquals("rint -1.5", -2, MathUtil.rint(-1.5, 0), EPSILON);
        assertEquals("rint -1.4", -1, MathUtil.rint(-1.4, 0), EPSILON);
        assertEquals("rint -1.576", -1.58, MathUtil.rint(-1.576, 2), EPSILON);
        assertEquals("rint 1.576", 1.58, MathUtil.rint(1.576, 2), EPSILON);
    }

    @Test
    public void testHashCode() {
        int h1 = MathUtil.doubleHashCode(3.14159);
        int h2 = MathUtil.doubleHashCode(3.142);
        assert h1 != h2;
    }

    @Test
    public void testExponentialMovingAverage() {
        ExponentialMovingAverage a = new ExponentialMovingAverage(1.0, 0, 0);
        ExponentialMovingAverage b = new ExponentialMovingAverage(5.0, 0, 0);
        ExponentialMovingAverage c = new ExponentialMovingAverage(15.0, 0, 0);

        Random r = new Random(31);

        for (int i = 0; i < 1000; i++) {
            //long t = r.nextInt(1000);
            //double v = r.nextGaussian();
            long t = i * 5000;
            double v = (i < 100 ? 10.0 : 0.0);
            final double alpha = a.alpha(t);
            double va = a.next(t, v);
            double vb = b.next(t, v);
            double vc = c.next(t, v);
            //System.out.println("[" + i + "] t=" + t + " v=" + v + " alpha=" + alpha + " : " + va + ", " + vb + ", " + vc);
        }
    }

    public static class ExponentialMovingAverage {
        private double w;               // Averaging period (seconds)
        private double prev;            // The previous value.
        private long prevTime;          // The previous timestamp.
        private boolean first = true;   // True if this is the first.

        public ExponentialMovingAverage(double w, double prev, long prevTime) {
            this.w = w;
            this.prev = prev;
            this.prevTime = prevTime;
        }

        public double seconds(long time) {
            return (double)time / 1000.0;
        }

        public double diff(long time) {
            return seconds(time) - seconds(prevTime);
        }

        public double alpha(long time) {
            return 1.0 - Math.exp(-(diff(time) / (w * 60.0)));
        }

        public double next(long time, double value) {
            if (first) {
                first = false;
                prevTime = time - 1;
            }
            double a = alpha(time);
            double sn = a * value + (1.0 - a) * prev;
            prev = sn;
            prevTime = time;
            return sn;
        }
    }
}
