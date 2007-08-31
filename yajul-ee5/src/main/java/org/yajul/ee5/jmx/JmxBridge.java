/**
 * A singleton that contains JMX MBean proxies that can allow a JMX service to invoke local EJBs inside
 * an application's class loading context.
 * <br>User: Joshua Davis
 * Date: Aug 29, 2007
 * Time: 5:55:18 AM
 */
package org.yajul.ee5.jmx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class JmxBridge {
    private static final Log log = LogFactory.getLog(JmxBridge.class);

    private static JmxBridge ourInstance;

    private Map<String, Proxy> proxiesByImplementationClassName;

    public static JmxBridge getInstance() {
        synchronized (JmxBridge.class) {
            if (ourInstance == null)
                ourInstance = new JmxBridge();
            return ourInstance;
        }
    }

    private JmxBridge() {
        proxiesByImplementationClassName = new HashMap<String, Proxy>();
        log.info("created.");
    }

    public void reset() {
        synchronized (this) {
            for (Proxy proxy : proxiesByImplementationClassName.values()) {
                proxy.stop();
            }
            proxiesByImplementationClassName.clear();
        }
    }

    public Proxy getProxy(String implementationClassName) {
        synchronized (this) {
            return doGetProxy(implementationClassName);
        }
    }

    /**
     * Register a specific implementation class with the bridge.   Invoke this from a suitable class loading context.  For example, from
     * a startup Servlet.
     * @param implClass the implementation class.
     * @throws Exception if the proxies created by the MBeans could not be initialized.
     */
    public void register(Class implClass) throws Exception {
        synchronized(this) {
            // Get or create the proxy.
            Proxy proxy = doGetProxy(implClass.getName());
            // Initialize it now.
            proxy.initialize();
        }
    }
    /**
     * Initializes all the proxies.   Invoke this from a suitable class loading context.  For example, from
     * a startup Servlet.
     * @throws Exception if the proxies created by the MBeans could not be initialized.
     */
    public void initializeProxies() throws Exception {
        synchronized (this) {
            for (Proxy proxy : proxiesByImplementationClassName.values()) {
                proxy.initialize();
            }
        }
        log.info("initializeProxies() : completed.");
    }

    private Proxy doGetProxy(String implementationClassName) {
        Proxy proxy = proxiesByImplementationClassName.get(implementationClassName);
        if (proxy == null) {
            proxy = new Proxy(implementationClassName);
            proxiesByImplementationClassName.put(implementationClassName, proxy);
        }
        return proxy;
    }
}
