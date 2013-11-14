package org.yajul.jmx;

/**
 * Provides JMX MBeans implementations.
 * <br>
 * User: josh
 * Date: Jan 28, 2009
 * Time: 12:23:32 PM
 */
public interface ImplementationProvider {
    /**
     * Allows the user of JmxBridge to use a custom class loader
     * @param className the name of the class
     * @return the class
     */
    Class<?> getImplementationClass(String className) throws ClassNotFoundException;


    /**
     * Replaces 'class.newInstance()' in JmxBridge.
     * @param implementationClass the implementation class
     * @param <T> the implementation class
     * @return the instance
     */
    <T> T getImplementation(Class<T> implementationClass);
}
