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

package org.yajul.util;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Provides utility methods for instantiating objects with
 * dynamic, or 'soft' linkages.   This is particularly useful
 * for instantiating abstract singletons, or replacing 'hard'
 * dependencies (often circular) with soft linkages that are resolved
 * at run-time.
 * User: josh
 * Date: Oct 21, 2003
 * Time: 8:23:12 AM
 */
public class ObjectFactory
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(ObjectFactory.class);

    /**
     * Creates a new instance of the class using the specified loader.
     * @param className The class to instatiate - if null a default will be used
     * @param loader The class loader to use when obtaining the instance
     * @return An Object which is an instance of the class
     */
    public static Object createInstance(String className, ClassLoader loader)
    {
        if (log.isDebugEnabled())
            log.debug("createInstance() : Creating instance from '" + className + "'");

        try
        {
            Class c;

            if (loader == null)
            {
                c = Class.forName(className);
            }
            else
            {
                c = loader.loadClass(className);
            }

            return c.newInstance();
        }
        catch (ClassNotFoundException x)
        {
            throw new InitializationError("Class " + className
                    + " not found - " + "please check your classpath", x);
        }
        catch (ExceptionInInitializerError eiie)
        {
            throw new InitializationError("Class " + className
                    + " failed to initialize due to: " + eiie.getMessage(), eiie);
        }
        catch (InstantiationException ie)
        {
            throw new InitializationError("Class " + className
                    + " could not be instantiated - "
                    + "is it abstract, an interface, an array, or does it not have an "
                    + "empty constructor?", ie);
        }
        catch (IllegalAccessException iae)
        {
            throw new InitializationError("Class " + className
                    + " could not be accessed - "
                    + "is it private or is the empty constructor private?",
                    iae);
        }
    }

    /**
     * Creates a new instance of the class, throwing an initialization error, if there
     * was a problem.
     * @param className The class name
     * @return Object - The instance.
     */
    public static Object createInstance(String className)
    {
        return createInstance(className,getCurrentClassLoader());
    }

    /**
     * Creates an instance from the specified property in the specified properties-file
     * resource, which will be loaded from the current thread's class loader.  If the
     * class implements Initializeable, it will be initialized.
     * @param resourceName The name of the resource to load.
     * @param propertyName The name of the property to look for in the resource.
     * @param defaultClassName The default class name to use, if the resource was not found.
     * @param resourceAndPropertyRequired Set to true if the resource and property name are required.
     * @return Object - The new instance.
     * @throws InitializationException if the resource could not be loaded.
     */
    public static Object createInstanceFromPropertiesResource(String resourceName,
                                                              String propertyName,
                                                              String defaultClassName,
                                                              boolean resourceAndPropertyRequired)
        throws InitializationException
    {

        Properties properties = ResourceUtil.loadProperties(resourceName);
        String className = defaultClassName;
        if (properties == null)
        {
            String message = "Properties resource " + resourceName
                                + " was not found.";
            if (resourceAndPropertyRequired)
                throw new InitializationException(message);
            if (log.isDebugEnabled())
                log.debug(message + "  Using default class name '" + defaultClassName + "'.");
        }
        else
        {
            className = properties.getProperty(propertyName);
            if (StringUtil.isEmpty(className))
            {
                String message = "Properties resource " + resourceName
                                        + " did not contain a value for " + propertyName
                                            + ".";
                if (resourceAndPropertyRequired)
                    throw new InitializationException(message);
                if (log.isDebugEnabled())
                    log.debug(message + "  Using default class name '" + defaultClassName + "'.");
                className = defaultClassName;
            }
        }
        // Create the instance.
        Object inst = createInstance(className);

        // If the instance supports the Initializeable interface, initialize it.
        if (inst instanceof Initializeable)
        {
            Initializeable initializeableObject = (Initializeable) inst;
            initializeableObject.initialize(properties);
        }
        return inst;
    }

    /**
     * Returnsthe current class loader.
     * @return
     */
    public static ClassLoader getCurrentClassLoader()
    {
        if (log.isDebugEnabled())
            log.debug("getCurrentClassLoader() : Looking for currentclass loader.");
        // lets get a class loader. By using the Thread's class loader, we allow
        // for more flexability.
        ClassLoader classLoader = null;

        try
        {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (log.isDebugEnabled())
                log.debug("Using context ClassLoader " + classLoader);
        }
        catch (SecurityException se)
        {
            classLoader = ObjectFactory.class.getClassLoader();
            if (log.isDebugEnabled())
                log.debug("Using system ClassLoader " + classLoader);
        }

        return classLoader;
    }
}
