package org.yajul.jmx;

import org.yajul.util.Lifecycle;

/**
 * Test implementation for JmxBridge
 * <br>User: Joshua Davis
 * Date: Aug 29, 2007
 * Time: 6:32:53 AM
 */
public class MyTestImpl implements Lifecycle {
    private boolean started = false;

    public void start() throws Exception {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }
}
