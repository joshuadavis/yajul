package org.yajul.util;

/**
 * System time access - basically a wrapper around
 * <br>
 * User: josh
 * Date: 1/8/13
 * Time: 7:05 AM
 */
public interface Clock {
    /**
     * The current time, in milliseconds since the epoch (1/1/1970 00:00:00).
     * @return the number of milliseconds since the epoch.
     */
    long currentTimeMillis();
}
