package org.yajul.log;

import org.yajul.util.Lifecycle;

/**
 * JMX Interface for the Juli->Log4J MBean.
 * <br>
 * User: josh
 * Date: Jun 5, 2008
 * Time: 8:44:16 AM
 */
public interface JuliToLog4JServiceMBean extends Lifecycle {

    /**
     * @return The logging level of the java.util.logging->Log4J 'Handler' as a string
     */
    String getHandlerLevel();

    /**
     * Sets the logging level of the java.util.logging->Log4J 'Handler' to the specified value.
     * @param level the logging level for the adapter, e.g. "INFO"
     */
    void setHandlerLevel(String level);
}
