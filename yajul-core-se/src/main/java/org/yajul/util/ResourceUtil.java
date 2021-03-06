package org.yajul.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Provides utility methods for finding and loading resources. User: josh Date: Sep 20, 2003 Time: 7:06:23 PM
 */
public class ResourceUtil {
    /**
     * Loads a properties resource.  Returns null if the resource was not found.
     *
     * @param resourceName The name of the resource.
     * @return Properties - The loaded properties, or <i>null</i> if the resource was not found.
     * @throws java.io.IOException if the resource could not be parsed
     */
    public static Properties loadProperties(String resourceName) throws IOException {
        return loadProperties(resourceName, null, null);
    }

    /**
     * Loads a properties resource.  Returns null if the resource was not found.
     *
     * @param resourceName The name of the resource.
     * @param defaults     A set of default properties, for 'layering' of properties files.
     *                     See http://www.javaworld.com/javaworld/javatips/jw-javatip135.html?page=1
     * @param aClass       optional class for loading properties inside a package.  If this is specified (not null)
     *                     the resource name will be loaded from the same class loader and package as this class.  The package path
     *                     will automatically be added to the resource name in this case.
     * @return Properties - The loaded properties, or <i>null</i> if the resource was not found.
     * @throws java.io.IOException if the resource could not be parsed
     */
    public static Properties loadProperties(String resourceName, Properties defaults, Class aClass) throws IOException {
        // If a class was not specified, load the resource using it's name, assuming it's just
        // somewhere in the current class loader.   Otherwise, load it using the class.
        InputStream is = (aClass == null) ?
                getResourceAsStream(resourceName) : aClass.getResourceAsStream(resourceName);

        if (is == null)     // If the resource was not found,
            return null;    // notify the caller by returning null.

        // Load the properties file from the current class loader.  Use the defaults if they were
        // supplied.
        Properties properties = (defaults == null) ? new Properties() : new Properties(defaults);
        properties.load(is);
        return properties;
    }

    /**
     * Returns an input stream for the named resource, or null if it was not found.  Uses the current class loader.
     *
     * @param resourceName The name of the resource
     * @return InputStream - The input stream, or null if the resource was not found.
     */
    public static InputStream getResourceAsStream(String resourceName) {
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
    public static byte[] resourceAsBytes(String name) throws IOException {
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
    public static boolean exists(String name) {
        InputStream is = getResourceAsStream(name);
        return is != null;
    }

   public static String getFilePathFromURL(String urlPath) {
      // On windows urlpath looks like file:/C: on Linux file:/home
      // substring(5) works for both
      urlPath = urlPath.substring(5);
      return urlPath;
   }

   public static String getPath(URL url) throws UnsupportedEncodingException {
      String urlPath = url.getFile();
      urlPath = URLDecoder.decode(urlPath, "UTF-8");
      return urlPath;
   }

   public static boolean isFileURL(String urlPath) {
      return urlPath.startsWith("file:");
   }
}
