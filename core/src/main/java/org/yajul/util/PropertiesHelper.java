package org.yajul.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Helper methods for loading and parsing properties files.
 * <br>
 * User: Josh
 * Date: Nov 14, 2009
 * Time: 7:33:16 AM
 */
public class PropertiesHelper {
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static enum BooleanParse {
        /**
         * Causes getBoolean() to behave like the JDK Boolean.parseBoolean(String) function:
         * <p>
         * The <code>boolean</code>
         * returned represents the value <code>true</code> if the string argument
         * is not <code>null</code> and is equal, ignoring case, to the string
         * {@code "true"}.
         * </p>
         * <b>ANY VALUE BUT "true" (ignoring case) WILL RETURN false!</b>
         * <ul>Examples:
         * <li>{@code "true"} => {@code true}</li>
         * <li>{@code "True"} => {@code true}</li>
         * <li>{@code "false"} => {@code false}</li>
         * <li>{@code "yes"} => {@code false} (ouch!)</li>
         * <li>{@code " true"} (space before "true") => {@code false} (ouch!)</li>
         * </ul>
         * @see java.lang.Boolean#parseBoolean(String)
         */
        JDK,
        /**
         * Only "true" and "false" are accepted, <i>ignoring case</i>.
         * <ul>Examples:
         * <li>{@code "true"} => {@code true}</li>
         * <li>{@code "True"} => {@code true}</li>
         * <li>{@code "false"} => {@code false}</li>
         * <li>{@code "yes"} => {@code IllegalArgumentException}</li>
         * <li>{@code " true"} (space before "true") => {@code IllegalArgumentException}</li>
         * </ul>
         * Everything else throws an IllegalArgumentException.
         */
        STRICT,
    }

    private static final Logger log = LoggerFactory.getLogger(PropertiesHelper.class);

    public static Properties loadFromFile(File file, Properties defaults) {
        Properties properties = (defaults != null) ? new Properties(defaults) : new Properties();
        try {
            if (file.exists())
                properties.load(new FileInputStream(file));
            else
                log.trace("File not found: " + file.getCanonicalPath());
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return properties;
    }

    public static Properties loadFromResource(String resource, Properties defaults, Class<?> clazz) {
        try {
            defaults = ResourceUtil.loadProperties(resource, defaults, clazz);
            if (defaults == null)
                log.trace("Resource not found: " + resource);
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return defaults;
    }

    public static List<String> getNameList(Properties props) {
        List<String> names = new ArrayList<String>(props.size());
        for (Object o : props.keySet())
            names.add((String) o);
        return names;
    }

    /**
     * @param properties   The properties object.
     * @param key          The property to get
     * @param mode         Parsing mode.
     * @param defaultValue The value to return if the key doesn't exist
     * @return the boolean value of the property.
     */
    public static boolean getBoolean(Properties properties, String key, boolean defaultValue, BooleanParse mode) {
        if (!properties.containsKey(key))
            return defaultValue;
        String v = properties.getProperty(key);
        switch (mode) {
            case JDK:
                return ((v != null) && v.equalsIgnoreCase(TRUE));
            case STRICT:
                if (TRUE.equalsIgnoreCase(v))
                    return true;
                else if (FALSE.equalsIgnoreCase(v))
                    return false;
                else
                    throw new IllegalArgumentException("Illegal boolean value '" + v + "'!");
            default:
                throw new IllegalArgumentException("Unexpected mode: " + mode);
        }
    }

    /**
     * @param properties The properties object.
     * @param key        The property to get
     * @return the boolean value of the property.
     */
    public static boolean getBoolean(Properties properties, String key) {
        return getBoolean(properties, key, false, BooleanParse.JDK);
    }

    /**
     * @param properties   The properties object.
     * @param key          The property to get
     * @param defaultValue The value to return if the key doesn't exist
     * @return the boolean value of the property.
     */
    public static boolean getBoolean(Properties properties, String key, boolean defaultValue) {
        return getBoolean(properties, key, defaultValue, BooleanParse.JDK);
    }

    public static Integer getInteger(Properties properties, String key) {
        String strValue = properties.getProperty(key);
        return StringUtil.isEmpty(strValue) ? null : Integer.parseInt(strValue);
    }

    public static double getDouble(Properties properties, String key) {
        String strValue = properties.getProperty(key);
        return StringUtil.isEmpty(strValue) ? null : Double.parseDouble(strValue);
    }

    public static double getDouble(Properties properties, String key, double defaultValue) {
        String strValue = properties.getProperty(key);
        return StringUtil.isEmpty(strValue) ? defaultValue : Double.parseDouble(strValue);
    }

    public static long getLong(Properties properties, String key, long defaultValue) {
        String strValue = properties.getProperty(key);
        return StringUtil.isEmpty(strValue) ? defaultValue : Long.parseLong(strValue);
    }

    public static int getInt(Properties properties, String key, int defaultValue) {
        String strValue = properties.getProperty(key);
        return StringUtil.isEmpty(strValue) ? defaultValue : Integer.parseInt(strValue);
    }

    public static String debugString(Properties props) {
        List<String> names = getSortedNames(props);
        StringBuilder sb = new StringBuilder();
        for (String name : names)
            sb.append("\n").append(name).append('=').append(props.get(name));
        return sb.toString();
    }

    public static List<String> getSortedNames(Properties props) {
        List<String> names = getNameList(props);
        Collections.sort(names);
        return names;
    }

}
