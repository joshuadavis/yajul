package org.yajul.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * Prints the fields of an object into a string buffer.
 * User: jdavis
 * Date: Oct 21, 2003
 * Time: 7:46:24 PM
 * @author jdavis
 */
public class FieldPrinter
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(FieldPrinter.class.getName());

    private static Class[] NO_PARAMETERS = new Class[0];
    private static Object[] NO_ARGUMENTS = new Object[0];

    private StringBuffer buf;
    private DateFormat df;

    public FieldPrinter()
    {
        this(new StringBuffer());
    }

    /**
     * Creates a new object field printer, that will append
     * to the supplied string buffer.
     * @param buf The buffer to append to.
     */
    public FieldPrinter(StringBuffer buf)
    {
        this.buf = buf;
        df = new SimpleDateFormat("yyyyMMMdd HH:mm:ss zz");
        df.setTimeZone(TimeZone.getDefault());
    }

    /**
     * Appends all of the fields of the object to the string buffer.
     * @param o - The object to print.
     */
    public void append(Object o)
    {
        if (o == null)
        {
            buf.append("null");
            return;
        }

        // Write the class name and a square bracket.
        Class c = o.getClass();
        buf.append(c.getName());
        buf.append("[");

        Field[] fields = c.getFields();
        Method[] methods = c.getMethods();

        // Assume first we're going to get publically accessible fields in the object's class.
        boolean usingFields = true;
        int length = fields.length;

        // If there are 0 accessible fields, switch to looking for 'getFoo()' methods
        if (length == 0)
        {
            length = methods.length;
            usingFields = false;
        }

        // tracks how many valid methods we've output so far (to help with knowing when to insert ", " between them)
        int validMethodCount = 0;
        // for methods, only drill in if it's a valid 'getter'
        boolean validAttribute = false;

        // Print out each item.
        for (int i = 0; i < length; i++)
        {
            Class attributeType = null;
            Object attributeValue = null;

            try
            {
                // assign the Class and Object of the attribute from a Field or Method depending on what we're looking at
                if (usingFields)
                {
                    // publically-accessible fields are always valid
                    validAttribute = true;
                    if (i > 0)
                        buf.append(", ");
                    buf.append(fields[i].getName());
                    buf.append("=");

                    attributeType = fields[i].getType();
                    attributeValue = fields[i].get(o);
                }
                // check the method to see if the name starts with 'get'
                else
                {
                    // assume this method not a 'getter'
                    validAttribute = false;
                    // if it begins with 'get' or 'is' & has 0 parameters, use it
                    if (ReflectionUtil.isPropertyGetter(methods[i]))
                    {
                        validAttribute = true;
                        if (validMethodCount > 0)
                            buf.append(", ");
                        validMethodCount++;
                        buf.append(methods[i].getName());
                        buf.append("=");

                        attributeType = methods[i].getReturnType();
                        attributeValue = methods[i].invoke(o, new Object[0]);
                    }
                }

                // If this is a valid attribute, process
                if (validAttribute)
                {
                    appendField(attributeValue, attributeType);
                } // if validAttribute
            }
            catch (IllegalAccessException e)
            {
                log.warn("Unexpected: " + e.getMessage(),e);
                buf.append("<error!>");
            }
            catch (InvocationTargetException e)
            {
                log.warn("Unexpected: " + e.getMessage(),e);
                buf.append("<error!>");
            }
        } // for i = 0 to length
        buf.append("]");
    }

    public static String toString(Object o)
    {
        FieldPrinter fp = new FieldPrinter();
        fp.append(o);
        return fp.toString();
    }

    public String toString()
    {
        return buf.toString();
    }

    private void appendField(Object attributeValue, Class attributeType) throws IllegalAccessException
    {
        // If the value is null, just use 'null'.
        if (attributeValue == null)
            buf.append("null");
        // If the value is a primitive or a number, just append it.
        else if (attributeType.isPrimitive() || Number.class.isAssignableFrom(attributeType))
            buf.append(attributeValue);
        // Use UTC format for dates.
        else if (java.util.Date.class.isAssignableFrom(attributeType))
            buf.append(df.format((java.util.Date) attributeValue));
        // Print strings in quotes.
        else if (String.class.isAssignableFrom(attributeType))
            appendValue(attributeValue, "'", "'", buf);
        // Print arrays in brackets.
        else if (attributeType.isArray())
            appendValue(attributeValue, "{", "}", buf);
        // Print objects nested in vertical bars.
        else
            appendNestedObject(attributeType, attributeValue);
    }

    private void appendNestedObject(Class attributeType, Object attributeValue) throws IllegalAccessException
    {
        String stringValue = null;
        // If the class defines 'toString()', then use that.
        Method m = null;
        try
        {
            m = attributeType.getDeclaredMethod("toString", NO_PARAMETERS);
        }
        catch (NoSuchMethodException ignore)
        {
        }
        if (m != null)
        {
            try
            {
                stringValue = (String) m.invoke(attributeValue, NO_ARGUMENTS);
            }
            catch (InvocationTargetException ite)
            {
                log.warn("Unexpected: " + ite.getMessage(),ite);
                stringValue = null;
            }
        }
        if (stringValue != null)
            appendValue(stringValue, "#", "#", buf);
        // Otherwise, recurse...
        else
        {
            buf.append("|");
            append(attributeValue);
            buf.append("|");
        }
    }

    private static void appendValue(Object fieldValue, String startDelim, String endDelim, StringBuffer buf)
    {
        buf.append(startDelim);
        buf.append(fieldValue);
        buf.append(endDelim);
    }
}

