/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
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

// JDK
import java.util.TimeZone;
import java.util.Date;
import java.util.Locale;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Provides a thread safe wrapper for a DateFormat (date formats are not thread
 * safe).  Synchronizes calls and delegates them to an inner DateFormat object.
 * (This is an old trick I learned when implementing XML-RPC and the XSL Servlet
 * framework [jsd])
 * <br>... and YES, DateFormat is not thread safe (sez Sun)!<br>
 * The following is an example of a common exception thrown when DateFormat is
 * used by multiple threads in JDK 1.3:
 * <pre>
 * java.lang.IllegalArgumentException
 *     at java.util.SimpleTimeZone.getOffset(SimpleTimeZone.java:427)
 *     at java.util.GregorianCalendar.computeFields(GregorianCalendar.java:1173)
 *     at java.util.Calendar.setTimeInMillis(Calendar.java:903)
 *     at java.util.Calendar.setTime(Calendar.java:882)
 *     at java.text.SimpleDateFormat.format(SimpleDateFormat.java:400)
 *     at java.text.DateFormat.format(DateFormat.java:305)
 * </pre>
 *
 * @author Joshua Davis
 * @version 0.1
 **/
public class DateFormatter
{
    /**
     * A formatting string for java.text.SimpleDateFormat that
     * will allow parsing and formatting of ISO 8601 date strings
     * with year, month, and day specified.
     **/
    public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * A formatting string for java.text.SimpleDateFormat that
     * will allow parsing and formatting of ISO 8601 date strings
     * with all UTC fields specified.
     **/
    public static final String ISO8601_UTC_FORMAT =
            "yyyy-MM-dd'T'hh:mm:ss,SSS'Z'";

    /**
     * Gets an instance of GMT Calendar.
     * @return a new <code>Calendar</code> object
     **/
    public static final Calendar getGMTCalendar()
    {
        return GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.US);
    }
    // --- Internal state ---

    /** The formatter delegate. **/
    private DateFormat f;

    /** Creates a new date formater given a simple date format string.
     * @param simpleFormat - The simple date format string.
     * @param timeZone - The time zone symbol for the format.
     * @param loc - The locale of the format.
     **/
    public DateFormatter(String simpleFormat,String timeZone,Locale loc)
    {
        this(new SimpleDateFormat(simpleFormat,loc));
        setTimeZone(timeZone);
    }

    /** Creates a new date formater given a simple date format string.
     * @param simpleFormat - The simple date format string.
     * @param timeZone - The time zone symbol for the format.
     **/
    public DateFormatter(String simpleFormat,String timeZone)
    {
        this(new SimpleDateFormat(simpleFormat));
        setTimeZone(timeZone);
    }

    /** Creates a new date formater given a DateFormat object.
     * @param fmt - The date format to wrap.
     **/
    public DateFormatter(DateFormat fmt)
    {
        f = fmt;
    }

    /** Sets the time zone for the date formatter.
     * @param timeZone - The time zone name to set for the formatter.
     **/
    public void setTimeZone(String timeZone)
    {
        synchronized (this)
        {
            setTimeZone(TimeZone.getTimeZone(timeZone));
        }
    }

    /** Sets the time zone for the date formatter.
     * @param tz - The time zone to set for the formatter.
     **/
    public void setTimeZone(TimeZone tz)
    {
        synchronized (this)
        {
            f.setTimeZone(tz);
        }
    }
    
    /** Format a date and return the result as a string.  Synchronized
     * for thread-safety.
     * @param d - The date to format.
     * @return String - The formatted date string.
     **/
    public String format(Date d)
    {
        synchronized (this)
        {
            return f.format(d);
        }
    }

    /** Parse a date string and return the result as a date.  Synchronized
     * for thread safety.
     * @param s - The string to parse.
     * @return Date - The parsed date.
     * @throws ParseException - When the 's' fails to parse.
     **/
    public Date parse (String s) throws ParseException
    {
        synchronized (this)
        {
            return f.parse(s);
        }
    }    
    
    /**
     * Parse a date/time string according to the given parse position.
     * @param text - The date/time string to be parsed
     * @param pos - On input, the position at which to start parsing; on output,
     * the position at which parsing terminated, or the start position if the
     * parse failed.
     * @return A Date, or null if the input could not be parsed
     * @see setLenient(boolean)
     * @see java.text.DateFormat.parse(java.lang.String,java.text.ParsePosition)
     **/
    public Date parse(String text, ParsePosition pos)
    {
        synchronized (this)
        {
            return f.parse(text, pos);
        }
    }
}