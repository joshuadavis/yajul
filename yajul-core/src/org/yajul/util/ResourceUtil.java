package org.yajul.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides utility methods for finding and loading resources. User: josh Date: Sep 20, 2003 Time: 7:06:23 PM
 */
public class ResourceUtil
{
    /**
     * Loads a properties resource.  Returns null if the resource was not found.
     * @param resourceName The name of the resource.
     * @return Properties - The loaded properties, or <i>null</i> if the resource was not found.
     */
    public static Properties loadProperties(String resourceName) throws IOException
    {
        InputStream is = getResourceAsStream(resourceName);
        if (is == null)     // If the resource was not found,
            return null;    // notify the caller by returning null.

        // Load the properties file from the current class loader, if it isn't already
        // loaded.
        Properties properties = new Properties();
        properties.load(is);
        return properties;
    }

    /**
     * Returns an input stream for the named resource, or null if it was not found.  Uses the current class loader.
     *
     * @param resourceName The name of the resource
     * @return InputStream - The input stream, or null if the resource was not found.
     */
    public static InputStream getResourceAsStream(String resourceName)
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl.getResourceAsStream(resourceName);
    }

    /**
     * Returns the resource as a byte array.
     *
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

    /**
     * Returns true if the specified resource exists.
     *
     * @param name The resource name
     * @return true if the specified resource exists.
     */
    public static boolean exists(String name)
    {
        InputStream is = getResourceAsStream(name);
        return is != null;
    }
}
