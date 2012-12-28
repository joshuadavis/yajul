package org.yajul.util;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Prints the fields of an object into a string buffer.
 * <br>
 * User: jdavis
 * Date: Oct 21, 2003
 * Time: 7:46:24 PM
 *
 * @author jdavis
 */
public class FieldPrinter {
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(FieldPrinter.class.getName());

    private static Class[] NO_PARAMETERS = new Class[0];
    private static Object[] NO_ARGUMENTS = new Object[0];

    private StringBuilder sb;
    private DateFormat df;

    public FieldPrinter() {
        this(new StringBuilder());
    }

    /**
     * Creates a new object field printer, that will append
     * to the supplied string builder.
     *
     * @param sb The StringBuilder to append to.
     */
    public FieldPrinter(StringBuilder sb) {
        this.sb = sb;
        df = new SimpleDateFormat("yyyyMMMdd HH:mm:ss zz");
        df.setTimeZone(TimeZone.getDefault());
    }

    /**
     * Appends all of the fields of the object to the string buffer.
     *
     * @param o - The object to print.
     */
    public void append(Object o) {
        if (o == null) {
            sb.append("null");
            return;
        }

        // Write the class name and a square bracket.
        Class c = o.getClass();
        sb.append(c.getName());
        sb.append("[");

        Field[] fields = c.getFields();
        Method[] methods = c.getMethods();

        // Assume first we're going to get publically accessible fields in the object's class.
        boolean usingFields = true;
        int length = fields.length;

        // If there are 0 accessible fields, switch to looking for 'getFoo()' methods
        if (length == 0) {
            length = methods.length;
            usingFields = false;
        }

        // tracks how many valid methods we've output so far (to help with knowing when to insert ", " between them)
        int validMethodCount = 0;
        // for methods, only drill in if it's a valid 'getter'
        boolean validAttribute;

        // Print out each item.
        for (int i = 0; i < length; i++) {
            Class attributeType = null;
            Object attributeValue = null;

            try {
                // assign the Class and Object of the attribute from a Field or Method depending on what we're looking at
                if (usingFields) {
                    // publically-accessible fields are always valid
                    validAttribute = true;
                    if (i > 0)
                        sb.append(", ");
                    sb.append(fields[i].getName());
                    sb.append("=");

                    attributeType = fields[i].getType();
                    attributeValue = fields[i].get(o);
                }
                // check the method to see if the name starts with 'get'
                else {
                    // assume this method not a 'getter'
                    validAttribute = false;
                    // if it begins with 'get' or 'is' & has 0 parameters, use it
                    if (ReflectionUtil.isPropertyGetter(methods[i])) {
                        validAttribute = true;
                        if (validMethodCount > 0)
                            sb.append(", ");
                        validMethodCount++;
                        sb.append(methods[i].getName());
                        sb.append("=");

                        attributeType = methods[i].getReturnType();
                        attributeValue = methods[i].invoke(o);
                    }
                }

                // If this is a valid attribute, process
                if (validAttribute) {
                    appendField(attributeValue, attributeType);
                } // if validAttribute
            } catch (IllegalAccessException e) {
                log.log(Level.WARNING, "Unexpected: " + e.getMessage(), e);
                sb.append("<error!>");
            } catch (InvocationTargetException e) {
                log.log(Level.WARNING, "Unexpected: " + e.getMessage(), e);
                sb.append("<error!>");
            }
        } // for i = 0 to length
        sb.append("]");
    }

    public static String toString(Object o) {
        FieldPrinter fp = new FieldPrinter();
        fp.append(o);
        return fp.toString();
    }

    public String toString() {
        return sb.toString();
    }

    private void appendField(Object attributeValue, Class attributeType) throws IllegalAccessException {
        // If the value is null, just use 'null'.
        if (attributeValue == null)
            sb.append("null");
            // If the value is a primitive or a number, just append it.
        else if (attributeType.isPrimitive() || Number.class.isAssignableFrom(attributeType))
            sb.append(attributeValue);
            // Use UTC format for dates.
        else if (java.util.Date.class.isAssignableFrom(attributeType))
            sb.append(df.format((java.util.Date) attributeValue));
            // Print strings in quotes.
        else if (String.class.isAssignableFrom(attributeType))
            appendValue(attributeValue, "'", "'", sb);
            // Print arrays in brackets.
        else if (attributeType.isArray())
            appendValue(attributeValue, "{", "}", sb);
            // Print objects nested in vertical bars.
        else
            appendNestedObject(attributeType, attributeValue);
    }

    private void appendNestedObject(Class attributeType, Object attributeValue) throws IllegalAccessException {
        String stringValue = null;
        // If the class defines 'toString()', then use that.
        Method m = null;
        //noinspection EmptyCatchBlock
        try {
            m = attributeType.getDeclaredMethod("toString", NO_PARAMETERS);
        } catch (NoSuchMethodException ignore) {
        }
        if (m != null) {
            try {
                stringValue = (String) m.invoke(attributeValue, NO_ARGUMENTS);
            } catch (InvocationTargetException ite) {
                log.log(Level.WARNING, "Unexpected: " + ite.getMessage(), ite);
                stringValue = null;
            }
        }
        if (stringValue != null)
            appendValue(stringValue, "#", "#", sb);
            // Otherwise, recurse...
        else {
            sb.append("|");
            append(attributeValue);
            sb.append("|");
        }
    }

    private static void appendValue(Object fieldValue, String startDelim, String endDelim, StringBuilder sb) {
        sb.append(startDelim);
        sb.append(fieldValue);
        sb.append(endDelim);
    }
}

