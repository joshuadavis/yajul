package org.yajul.util;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

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
        InputStream is = getResourceAsStream(resourceName);
        if (is == null)     // If the resource was not found,
            return null;    // notify the caller by returning null.

        // Load the properties file from the current class loader, if it isn't already
        // loaded.
        Properties properties = new Properties();
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

    /**
     * Returns an input stream for the named resource, or null if it was not
     * found.  Uses the current class loader.
     * @param resourceName The name of the resource
     * @return InputStream - The input stream, or null if the resource
     * was not found.
     */
    public static InputStream getResourceAsStream(String resourceName)
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(resourceName);
        return is;
    }

    /**
     * Returns the resource as a byte array.
     * @param name The resource name.
     * @return byte[] The contents of the resource.
     * @throws IOException if anything goes wrong.
     */
    public static byte[] resourceAsBytes(String name) throws IOException
    {
        // Read the resource input stream into a byte array.
        InputStream is = getResourceAsStream(name);
        if (is == null)
            return null;
        return Copier.toByteArray(is);
    }
}
