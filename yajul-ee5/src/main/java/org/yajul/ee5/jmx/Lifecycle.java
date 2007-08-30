package org.yajul.ee5.jmx;

/**
 * Standard JMX MBean lifecycle methods.
 * <br>User: Joshua Davis
 * Date: Aug 29, 2007
 * Time: 5:58:12 AM
 */
public interface Lifecycle {

    /**
     * Called when an MBean is started
     * @throws Exception if something went wrong.
     */
    void start() throws Exception;

    /**
     * Called when an MBean is stopped
     */
    void stop();
}
