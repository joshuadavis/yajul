
package org.yajul.util;

import org.yajul.log.Logger;

import java.lang.reflect.*;
import java.text.SimpleDateFormat;

/**
 * Provides a 'toString' implementation for objects that don't implement it.
 * Prints out all of the fields in an object using introspection.  If any
 * nested objects define toString(), that will be used.
 *
 * NOTE: THIS IS FOR DEBUGGING PURPOSES ONLY! DO NOT INVOKE THIS IF DEBUG TRACING
 * IS DISABLED!  Call CategoryLogWriter.isDebugEnabled() before invoking any methods
 * in this class.
 */
public class ObjectDebugPrinter
{
    private static Logger log = Logger.getLogger(ObjectDebugPrinter.class);
    private static final Class[] NO_PARAMETERS = new Class[0];
    private static final Object[] NO_ARGUMENTS = new Object[0];
    private static DateFormatter df = new DateFormatter(new SimpleDateFormat(DateFormatter.ISO8601_UTC_FORMAT));
    /**
     * Prints the class name of the object, followed by '@', followed by the hash code
     * of the object, just like java.lang.Object.toString().
     * @param o - The object to print.
     */
    public static final String defaultToString(Object o)
    {
        return org.yajul.util.StringUtil.defaultToString(o);
    }

    /**
     * Prints all of the fields of the object into a string.
     * @param o - The object to print.
     */
    public static final String toString(Object o)
    {
        StringBuffer buf = new StringBuffer();
        append(o,buf);
        return buf.toString();
    }
    
    /**
     * Appends all of the fields of the object to the string buffer.
     * @param o - The object to print.
     * @param buf - The buffer to print to.
     */
    public static final void append(Object o,StringBuffer buf)
    {
        if (o == null)
        {
            buf.append("null");
            return;
        }
        
        // Write the class name and a square bracket.
        Class c = o.getClass();
        buf.append(c.getName());
        buf.append(" [ ");
        
        // Get the fields in the object's class.
        Field[] fields = c.getFields();
        
        // Print out each field.
        for(int i = 0; i < fields.length ; i++)
        {
            if (i > 0)
                buf.append(", ");
            buf.append(fields[i].getName());
            buf.append("=");
            try
            {
                Class fieldType = fields[i].getType();
                Object fieldValue = fields[i].get(o);
                // If the value is null, just use 'null'.
                if (fieldValue == null)
                    buf.append("null");
                // If the value is a primitive or a number, just append it.
                else if (fieldType.isPrimitive() || Number.class.isAssignableFrom(fieldType))
                    buf.append(fieldValue);
                // Use UTC format for dates.
                else if (java.util.Date.class.isAssignableFrom(fieldType))
                    buf.append(df.format((java.util.Date)fieldValue));
                // Print strings in quotes.
                else if (String.class.isAssignableFrom(fieldType))
                    appendValue(fieldValue,"'","'",buf);
                // Print arrays in brackets.
                else if (fieldType.isArray())
                    appendValue(fieldValue,"{","}",buf);
                // Print objects nested in vertical bars.
                else
                {
                    String stringValue = null;
                    // If the class defines 'toString()', then use that.
                    Method m = null;
                    try
                    {
                        m = fieldType.getDeclaredMethod("toString",NO_PARAMETERS);
                    }
                    catch (NoSuchMethodException ignore) {}
                    if (m != null)
                    {
                        try
                        {
                            stringValue = (String)m.invoke(fieldValue,NO_ARGUMENTS);
                        }
                        catch(InvocationTargetException ite)
                        {
                            log.error("Unexpected exception: " + ite.getMessage(), ite);
                            stringValue = null;
                        }
                    }
                    if (stringValue != null)
                        appendValue(stringValue,"#","#",buf);
                    // Otherwise, recurse...
                    else
                    {
                        buf.append("|");
                        append(fieldValue,buf);
                        buf.append("|");
                    }
                }
            }
            catch(IllegalAccessException e)
            {
                log.error("Unexpected exception: " + e.getMessage(), e);
                buf.append("<error!>");
            }
        }
        buf.append(" ]");
    }        
    
    private static final void appendValue(Object fieldValue,String startDelim,String endDelim,StringBuffer buf)
    {
        buf.append(startDelim);
        buf.append(fieldValue);
        buf.append(endDelim);  
    }        
}
