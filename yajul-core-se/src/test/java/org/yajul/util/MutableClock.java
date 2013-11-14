package org.yajul.util;

/**
 * Settable clock for testing.
 * <br>
 * User: josh
 * Date: 1/8/13
 * Time: 7:43 AM
 */
public class MutableClock implements Clock {
    private long time;

    public long currentTimeMillis() {
        return time;
    }

    public void setCurrentTimeMillis(long t) {
        time = t;
    }

    public void increment(int millis) {
        time += millis;
    }
}
