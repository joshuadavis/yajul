package org.yajul.ee5.jmx;

/**
 * A proxy that instantiates the implementation JMX MBean when needed.
* <br>User: Joshua Davis
* Date: Aug 29, 2007
* Time: 6:17:01 AM
*/
public class Proxy implements Lifecycle {
    private String implementationClassName;
    private Lifecycle implementation;
    private boolean started;

    Proxy(String implementationClassName) {
        this.implementationClassName = implementationClassName;
        started = false;
    }

    public String getImplementationClassName() {
        return implementationClassName;
    }

    public void start() throws Exception {
        synchronized (this) {
            started = true;
            if (implementation != null)
                implementation.start();
        }
    }

    public void stop() {
        synchronized (this) {
            started = false;
            if (implementation != null)
                implementation.stop();
        }
    }

    /**
     * Internal method - initialize the implementation using the current thread context class loader.
     * @throws Exception if something goes wrong.
     */
    void initialize() throws Exception
    {
        if (implementation != null)
            return;
        String className = getImplementationClassName();
        Class c = Thread.currentThread().getContextClassLoader().loadClass(className);
        Object impl = c.newInstance();
        if (!(impl instanceof Lifecycle))
            throw new ClassCastException("Class " + c.getName() + " doesn't implement " + Lifecycle.class.getName());
        implementation = (Lifecycle) impl;
        // Call the start method (delayed) if the proxy is in the started state.
        if (started)
            implementation.start();
    }

    public Lifecycle getImplementation() {
        return implementation;
    }
}
