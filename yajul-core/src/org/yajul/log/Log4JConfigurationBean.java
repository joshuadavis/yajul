// $Id$
package org.yajul.log;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;
import org.yajul.util.ResourceUtil;

import java.util.Properties;

/**
 * JavaBean that can be used to configure Log4J from Spring. <br> <h3>Spring useage:</h3> <ul> <li>If you are using
 * commons-logging, make sure that there is a log4j.properties resource somewhere in the classpath for 'boot' logging
 * (i.e. logging *before* Spring creates the Log4JConfigurationBean).</li> <li>Instantiate a Log4JConfigurationBean in
 * the main bean descriptor (e.g. beanRefContext.xml, or /WEB-INF/applicationContext.xml).</li> <li>Make sure that
 * <code>lazy-init</code> is set to <b>false</b>, otherwise Spring will not invoke the init method and Log4J will not be
 * configured.</li> </ul> To configure Log4J using the defaults, use the following Spring bean descriptor (in
 * beanRefContext.xml, or wherever the main Spring bean descriptor is):
 * <pre>
 *    &lt;bean id="log4j-config" class="org.yajul.log.Log4JConfigurationBean" lazy-init="false" init-method="init"/>
 * </pre>
 *
 * @author josh Mar 28, 2004 2:30:55 PM
 */
public class Log4JConfigurationBean
{
    /**
     * The Log4J layout for the default logging configuration.
     */
    public static final String DEFAULT_LAYOUT =
            "%-6r [%8t] %-5p %15c{1} - %m\n";

    /**
     * The default logging level (DEBUG). *
     */
    public static final Level DEFAULT_LEVEL = Level.DEBUG;

    private Properties properties;

    private String defaultLayout = DEFAULT_LAYOUT;

    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    public void init()
    {
        if (properties != null) // If log4j configuration properties were set, use them.
        {
            LogManager.resetConfiguration();
            org.apache.log4j.PropertyConfigurator.configure(properties);
        }
        else if (ResourceUtil.exists("log4j.properties"))   // If log4j.properties exists, do nothing.
        {
        }
        else    // Otherwise, there is no log4j.properties and no properties in this bean.  Use the defaults.
        {
            LogManager.resetConfiguration();
            PatternLayout layout = new PatternLayout(defaultLayout);
            Appender appender = new ConsoleAppender(layout);
            BasicConfigurator.configure(appender);
        }
    }
}
