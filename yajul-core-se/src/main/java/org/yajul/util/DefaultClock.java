package org.yajul.util;

import java.io.Serializable;

/**
 * Implementation of the Clock interface using System.currentTimeMillis(), etc.
 * <br>
 * User: josh
 * Date: 1/8/13
 * Time: 7:07 AM
 */
public class DefaultClock implements Clock, Serializable {

    public static final DefaultClock INSTANCE = new DefaultClock();

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
