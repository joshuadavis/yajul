package org.yajul.util;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Provides utility methods for finding and loading resources.
 * User: josh
 * Date: Sep 20, 2003
 * Time: 7:06:23 PM
 */
public class ResourceUtil
{
    /**
     * Loads a properties resource.  Returns null if the resource was not found.
     * @param resourceName  The name of the resource.
     * @return Properties - The loaded properties, or <i>null</i> if the
     * resource was not found.
     */
    public static Properties loadProperties(String resourceName) throws InitializationException
    {
        // Load the properties file from the current class loader, if it isn't already
        // loaded.
        Properties properties = new Properties();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(resourceName);
        if (is == null)     // If the resource was not found,
            return null;    // notify the caller by returning null.

        try
        {
            properties.load(is);
        }
        catch (IOException e)
        {
            // There was a problem loading the resource into a 'properties' object,
            // so throw an exception.
            throw new InitializationException("Unable to load resource '"
                    + resourceName + "' due to : " + e.getMessage(),e);
        }
        return properties;
    }
}
