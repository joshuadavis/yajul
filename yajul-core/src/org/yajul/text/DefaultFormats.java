// $Id$
package org.yajul.text;

import org.yajul.util.DateFormatConstants;

import java.text.NumberFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.Format;
import java.text.ParsePosition;
import java.util.TimeZone;
import java.util.Date;

/**
 * Default formats for non-string bean values.
 * 
 * @author josh Apr 4, 2004 12:16:43 PM
 */
public class DefaultFormats
{
    private NumberFormat    integerFormat;
    private NumberFormat    doubleFormat;
    private DateFormat      dateFormat;

    public DefaultFormats()
    {
        integerFormat = new IntegerDecimalFormat("#");
        doubleFormat = new DecimalFormat("#.#");
        dateFormat = new SimpleDateFormat(DateFormatConstants.ISO8601_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public NumberFormat getIntegerFormat()
    {
        return integerFormat;
    }

    public NumberFormat getDoubleFormat()
    {
        return doubleFormat;
    }

    public DateFormat getDateFormat()
    {
        return dateFormat;
    }

    public Format getFormat(Class type)
    {
        if (Integer.class.equals(type) || Integer.TYPE.equals(type))
            return integerFormat;
        else if (
                Double.class.equals(type) ||
                Double.TYPE.equals(type) ||
                Float.class.equals(type) ||
                Float.TYPE.equals(type))
            return doubleFormat;
        else if (Date.class.equals(type))
            return dateFormat;
        return null;
    }
}
