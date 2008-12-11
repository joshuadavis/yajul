package org.yajul.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.micro.MicroContainer;
import org.yajul.util.Lifecycle;

/**
 * A proxy that instantiates the implementation JMX MBean when needed.
 * <br>User: Joshua Davis
 * Date: Aug 29, 2007
 * Time: 6:17:01 AM
 */
public class Proxy implements Lifecycle {
    private static final Logger log = LoggerFactory.getLogger(Proxy.class);
    private String implementationClassName;
    private Class implementationClass;
    private Lifecycle implementation;
    private boolean started;
    private boolean implementationStarted;
    private Exception exception;
    private MicroContainer container;

    Proxy(String implementationClassName, MicroContainer implementationContainer) {
        this.implementationClassName = implementationClassName;
        started = false;
        implementationStarted = false;
        this.container = implementationContainer;
    }

    public String getImplementationClassName() {
        return implementationClassName;
    }

    public void start() throws Exception {
        synchronized (this) {
            started = true;
            startImplementation();
        }
    }

    public void stop() {
        synchronized (this) {
            started = false;
            stopImplementation();
        }
    }

    /**
     * Returns the implementation.
     *
     * @return the implementation
     */
    public Lifecycle getImplementation() {
        return implementation;
    }

    /**
     * Returns true if the implementation has been started.
     *
     * @return true if the implementation has been started
     */
    public boolean isImplementationStarted() {
        synchronized (this) {
            return implementationStarted;
        }
    }

    /**
     * Returns any exception thrown while starting the implementation.
     *
     * @return an exception, or null
     */
    public Exception getException() {
        synchronized (this) {
            return exception;
        }
    }

    /**
     * Internal method - initialize the implementation using the current thread context class loader.
     *
     * @throws Exception if something goes wrong.
     */
    void initialize() throws Exception {
        synchronized (this) {
            if (implementation != null)
                return;
            String className = getImplementationClassName();
            try {
                log.info("initialize() : Looking up implementation class " + className + " ...");
                implementationClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                // Call the start method (delayed) if the proxy is in the started state.
                log.info("initialize() : " + className + " created.");
                if (started) {
                    startImplementation();
                }
            } catch (Exception e) {
                log.error("** Unable to initialize " + className + " due to " + e,e);
                throw e;
            }
        }
    }

    private void startImplementation() throws Exception {
        if (implementationClass != null && implementation == null) {
            log.info("Instantiating " + implementationClass.getName() + " ...");
            Object impl = container.getComponent(implementationClass);
            if (!(impl instanceof Lifecycle))
                throw new ClassCastException("Class " + implementationClass.getName() + " doesn't implement " + Lifecycle.class.getName());
            implementation = (Lifecycle) impl;
        }

        if (implementation == null) {
            log.info("No implementation for " + getImplementationClassName() + " yet, deferring start.");
        }
        else {
            try {
                log.info("Starting instance ...");
                implementation.start();
            } catch (Exception e) {
                log.error("Unable to start " + implementation.getClass().getName() + " due to " + e,e);
                implementationStarted = false;
                exception = e;
                throw e;
            }
            // No need to catch the exception here.
            implementationStarted = true;
        }
    }

    private void stopImplementation() {
        implementationStarted = false;
        if (implementation != null) {
            log.info("Stopping and releasing implementation " + implementation + " ...");
            implementation.stop();
            implementation = null;
        }
    }
}
