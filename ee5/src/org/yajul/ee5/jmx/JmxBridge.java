/**
 * A singleton that contains JMX MBean proxies that can allow a JMX service to invoke local EJBs inside
 * an application's class loading context.
 * <br>User: Joshua Davis
 * Date: Aug 29, 2007
 * Time: 5:55:18 AM
 */
package org.yajul.ee5.jmx;

import java.util.HashMap;
import java.util.Map;

public class JmxBridge {
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
            Proxy proxy = proxiesByImplementationClassName.get(implementationClassName);
            if (proxy == null) {
                proxy = new Proxy(implementationClassName);
                proxiesByImplementationClassName.put(implementationClassName, proxy);
            }
            return proxy;
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
    }

}
