/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/
package org.yajul.spi;

import org.apache.log4j.Logger;
import org.yajul.util.ObjectFactory;
import org.yajul.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Creates service providers based on descriptor information.  Provides
 * information about the service provider such as the name of the class that it implements,
 * the URL of the descriptor resource, and the name of the implementation class.
 * User: josh
 * Date: Nov 2, 2003
 * Time: 10:17:53 AM
 */
public class ServiceProviderFactory
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(ServiceProviderFactory.class);

    private String key;
    private String location;
    private String serviceResourceName;
    private ClassLoader classLoader;
    private URL descriptorURL;
    private String implementationClassName;
    private static final String SERVICE_RESOURCE_PREFIX = "META-INF/services/";

    /**
     * Returns the service id (service descriptor URI) for a given key.
     * @param key The SPI key.
     * @return String - The URI where the service implementation will be registered.
     */
    public static String getServiceResourceName(String key)
    {
        return SERVICE_RESOURCE_PREFIX + key;
    }

    /**
     * Find and creates a service provider using the given key, the default implementation,
     * and the specified ClassLoader.   This method will look for service providers in the
     * following way:
     * <ol>
     * <li>Use a system property to find the impelementation name.  The name of
     * the system property will be the key.</li>
     * <li>If the system property is not set, or the implementation class that the
     * system property refers to is not found, look for service providers in the class
     * path.  NOTE: This step will attempt to instantiate the service provider class if
     * a descriptor is found.  This may have side effects.  If the side effects are
     * not acceptable, use findServiceProviderFactory().<li>
     * <li>If no valid implementations were found in the classpath, use the default
     * implementation class name.  If no default implementation class name was specified,
     * an exception will be thrown.</li>
     * </ol>
     * @param key The SPI key, usually the name of the abstract class or interface
     * that the service provider implements.
     * @param classLoader The class loader to use when finding resources and classes.
     * If null, the current class loader will be used.
     * @param defaultImpl The name of a default implementation class to use if
     * the system property and the classpath lookups fail.
     * @return Object - A service provider implementation object for the SPI key.
     * @throws ServiceProviderNotFoundException if a suitable service provider cannot
     * be found.
     * @throws ServiceProviderInitializationException if the service provider was found,
     * but could not be instantiated.
     */
    public static Object findServiceProvider(String key, String defaultImpl,
                                             ClassLoader classLoader)
            throws ServiceProviderNotFoundException, ServiceProviderInitializationException
    {
        if (log.isDebugEnabled())
            log.debug("findServiceProvider(" + key + "," + defaultImpl + ","
                    + classLoader + ")");

        ServiceProviderFactory spf = findServiceProviderFactory(key, null, classLoader);
        if (spf != null)
        {
            try
            {
                return spf.createInstance();
            }
                    // Ignore the default provider not specified error because this case is
                    // handled explicitly below.
            catch (DefaultServiceProviderNotSpecified ignore)
            {
            }
                    // Any other exception should be logged before using the default
                    // implementation.
            catch (Exception e)
            {
                log.error("Unable to instantiate service provider due to: "
                        + e.getMessage() + " using default implementation.", e);
            }
        }
        if (defaultImpl == null)
        {
            throw new DefaultServiceProviderNotSpecified("Provider for " + key
                    + " cannot be found, no default implementation was specified.");
        }
        return new ServiceProviderFactory(key, classLoader, defaultImpl, "(default)").createInstance();
    }

    /**
     * Find a service provider factory using the given key, the default implementation,
     * and the specified ClassLoader.   This method will look for service providers in the
     * following way:
     * <ol>
     * <li>Use a system property to find the impelementation name.  The name of
     * the system property will be the key.</li>
     * <li>If the system property is not set, or the implementation class that the
     * system property referrs to is not found, look for service providers in the class
     * path.  NOTE: This step will *NOT* attempt to instantiate the service provider class if
     * a descriptor is found.<li>
     * <li>If no valid implementations were found in the classpath, use the default
     * implementation class name.  If no default implementation class name was specified,
     * an exception will be thrown.</li>
     * </ol>
     * @param key The SPI key, usually the name of the abstract class or interface
     * that the service provider implements.
     * @param classLoader The class loader to use when finding resources and classes.
     * If null, the current class loader will be used.
     * @param defaultImpl The name of a default implementation class to use if
     * the system property and the classpath lookups fail.
     * @return ServiceProviderFactory - A factory that should be able to instantiate
     * the service provider.
     * @throws ServiceProviderNotFoundException if a suitable service provider cannot
     * be found.
     */
    public static ServiceProviderFactory findServiceProviderFactory(String key, String defaultImpl, ClassLoader classLoader)
            throws ServiceProviderNotFoundException
    {
        if (log.isDebugEnabled())
            log.debug("findServiceProvider(" + key + "," + defaultImpl + ","
                    + classLoader + ")");

        // Look for a system property override.
        try
        {
            String systemProp = System.getProperty(key);

            if (!StringUtil.isEmpty(systemProp))
                return new ServiceProviderFactory(key, classLoader, systemProp, "(system property)");
        }
        catch (SecurityException se)
        {
            if (log.isDebugEnabled())
                log.debug("SecurityException " + se.getMessage(), se);
        }

        // Look for an implementation in the classpath.
        try
        {
            ServiceProviderFactory spf = new ServiceProviderFactory(key, classLoader);
            if (spf.implementationClassIsSpecified())
            {
                return spf;
            }
            if (log.isDebugEnabled())
                log.debug("Service '" + spf.getServiceResourceName() + "' not found in the classpath.");
        }
        catch (Exception ex)
        {
            if (log.isDebugEnabled())
                log.debug("Unexpected exception: " + ex.getMessage(), ex);
        }

        // Use the default implementation.
        if (defaultImpl == null)
        {
            throw new DefaultServiceProviderNotSpecified("Provider for " + key
                    + " cannot be found, no default implementation was specified.");
        }
        return new ServiceProviderFactory(key, classLoader, defaultImpl, "(default)");
    }

    /**
     * Creates a new service descriptor for the given SPI key.
     * @param key The SPI key, usually the name of the abstract class or interface
     * that the service provider implements.
     * @param loader The class loader to use when finding resources and classes.
     * @throws IOException if something goes wrong reading the service
     * descriptor resources.
     */
    public ServiceProviderFactory(String key, ClassLoader loader) throws IOException
    {
        initialize(key, loader);
        descriptorURL = classLoader.getResource(serviceResourceName);
        if (descriptorURL == null)
            throw new IOException("Unable to find descriptor URL for service resource: " + serviceResourceName);
        location = descriptorURL.toExternalForm();
        readClassName(classLoader.getResourceAsStream(serviceResourceName));
    }

    /**
     * Creates a new service descriptor with an explicitly defined implementation class name.
     * ServiceDescriptors created in this way will not search for a descriptor resource.
     * @param key The SPI key, usually the name of the abstract class or interface
     * that the service provider implements.
     * @param loader The class loader to use when finding classes.
     * @param implementationClassName The implementation class name.
     * @param location A string describing where the implementation class name
     * was found.  For example: "system property" or "default".
     */
    public ServiceProviderFactory(String key, ClassLoader loader, String implementationClassName,
                                  String location)
    {
        this.key = key;
        this.location = location;
        classLoader = (loader == null) ? ObjectFactory.getCurrentClassLoader() : loader;
        this.implementationClassName = implementationClassName;
    }

    /**
     * Creates a new ServiceProviderFactory given the SPI key and the URL that references
     * a service descriptor resource.   Service descriptors created in this way will
     * get the implementation class name by reading the descriptor resource.
     * @param key The SPI key, usually the name of the abstract class or interface
     * that the service provider implements.
     * @param loader The class loader to use when finding classes.
     * @param url The URL where the service descriptor resource can be found.
     * @throws IOException if the service descriptor resource URL could not be
     * read.
     */
    public ServiceProviderFactory(String key, ClassLoader loader, URL url) throws IOException
    {
        initialize(key, loader);
        descriptorURL = url;
        location = descriptorURL.toExternalForm();
        readClassName(descriptorURL.openStream());
    }

    /**
     * Returns true if the implementation class name has been specified.
     * @return boolean - True if the implementation class name has been specified.
     */
    public boolean implementationClassIsSpecified()
    {
        return !(StringUtil.isEmpty(implementationClassName));
    }

    /**
     * Returns the SPI key.
     * @return String - The SPI key, which is usually the name of the
     * abstract class or interface that the service provider should implement.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Returns the name of the service descriptor resource.
     * @return String - The name of the descriptor resource.
     */
    public String getServiceResourceName()
    {
        return serviceResourceName;
    }

    /**
     * Returns the ClassLoader that will be used to load classes and resources.
     * @return ClassLoader - The class loader that will be used to load classes and resources.
     */
    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    /**
     * Returns the URL of the service descriptor, or null if there is none.
     * @return URL - The URL of the service descriptor, or null if there is none (i.e. the
     * implementation class name did not come from a service descriptor).
     */
    public URL getDescriptorURL()
    {
        return descriptorURL;
    }

    /**
     * Returns a friendly string name for the location of the service descriptor.
     * @return String - The name of the service descriptor.
     */
    public String getDescriptorLocation()
    {
        return location;
    }

    /**
     * Returns the name of the implementation class.
     * @return String - The name of the SPI implementation class.
     */
    public String getImplementationClassName()
    {
        return implementationClassName;
    }

    /**
     * Creates a new instance of the implementation, or service provider.
     * @return Object - The new instance of the service.
     * @throws ServiceProviderNotFoundException if the implementation class could not be
     * found.
     * @throws ServiceProviderInitializationException if the service provider implementation class
     * was found, but could not be instantiated.
     */
    public Object createInstance() throws ServiceProviderNotFoundException, ServiceProviderInitializationException
    {
        String className = implementationClassName;
        ClassLoader loader = classLoader;

        if (StringUtil.isEmpty(className))
        {
            log.error("createInstance() : Implementation class name is an empty string! Returning [null].\n"
                    + " key=" + key + " location=" + getDescriptorLocation());
            return null;
        }

        try
        {
            if (loader == null)
                throw new IllegalStateException("No class loader was specified!");
            Class c = loader.loadClass(className);
            return c.newInstance();
        }
        catch (ClassNotFoundException x)
        {
            throw new ServiceProviderNotFoundException("Implementation class "
                    + className
                    + " not found for SPI " + key + "\n"
                    + "(referenced by " + getDescriptorLocation() + ")"
                    + "\nPlease check your classpath, or correct the implementation reference.", x);
        }
        catch (ExceptionInInitializerError eiie)
        {
            throw new ServiceProviderInitializationException("Implementation class "
                    + className
                    + "  for SPI " + key + " failed to initialize.\n",
                    eiie);
        }
        catch (InstantiationException ie)
        {
            throw new ServiceProviderInitializationException("Implementation class "
                    + className
                    + "  for SPI " + key
                    + " could not be instantiated.\n"
                    + "This may be because it is abstract, an interface, an array, or it lacks an empty constructor."
                    , ie);
        }
        catch (IllegalAccessException iae)
        {
            throw new ServiceProviderInitializationException("Implementation class "
                    + className
                    + "  for SPI " + key
                    + " could not be accessed.\n"
                    + "It may be private, or it's empty constructor may be private.",
                    iae);
        }
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("[ServiceProviderFactory key=").append(key);
        buf.append(" location=").append(location);
        buf.append(" serviceResourceName=").append(serviceResourceName);
        buf.append(" classLoader=").append(classLoader);
        buf.append(" descriptorURL=").append(descriptorURL);
        buf.append(" implementationClassName=").append(implementationClassName);
        buf.append("]");
        return buf.toString();
    }

    private void initialize(String key, ClassLoader loader)
    {
        this.key = key;
        serviceResourceName = getServiceResourceName(key);
        classLoader = (loader == null) ? ObjectFactory.getCurrentClassLoader() : loader;
    }

    private void readClassName(InputStream is) throws IOException
    {
        if (is != null)
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            implementationClassName = rd.readLine().trim();
            rd.close();
        }
    }

}
