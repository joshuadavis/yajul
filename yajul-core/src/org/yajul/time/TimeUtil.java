package org.yajul.time;

import java.util.Date;
import java.util.Calendar;

/**
 * Utility functions for date and time manipulation.
 * User: jdavis
 * Date: Nov 26, 2003
 * Time: 10:34:14 AM
 * @author jdavis
 */
public class TimeUtil
{
    /**
     * Returns the current date, at midnight (default time zone).
     * @return A java.util.Date, with the hours, minutes, seconds, and milliseconds
     * set to zero.
     */
    public static Date today()
    {
        Calendar c = Calendar.getInstance();
        floorCalendar(c,Calendar.DAY_OF_YEAR);
        return c.getTime();
    }

    /**
     * Similar to the <code>Math.floor</code> method, but for
     * <code>java.util.Calendar</code> objects.
     * Precision is specified with a <code>Calendar</code> field.  For example,
     * Jan 5, 2001 4:33:05.25 pm with <code>calendarField=Calendar.MONTH</code>
     * will be <i>rounded down</i> to Jan 1, 2001 12:00:00.00 am<br>
     * Throws IllegalArgumentException if <code>calendarField</code> is not one
     *         of the supported <code>Calendar</code> constants.
     * @param cal A calendar
     * @param precision The precision (e.g. <code>Calendar.YEAR</code>).<br>
     * Must be one of the following Calendar constants:<br>
     * MILLISECOND,SECOND,HOUR,HOUR_OF_DAY,DAY_OF_YEAR,DAY_OF_MONTH,MONTH,YEAR
     * @see java.lang.Math#floor
     */
    public static final void floorCalendar(Calendar cal, int precision)
    {
        switch (precision)
        {
            case Calendar.YEAR:
                cal.set(Calendar.MONTH, 0);
                // Flow through to set day, etc.
            case Calendar.MONTH:
                cal.set(Calendar.DAY_OF_MONTH, 1);
                // Flow through to set the time, etc.
            case Calendar.DAY_OF_MONTH:
            case Calendar.DAY_OF_YEAR:
                cal.set(Calendar.HOUR_OF_DAY, 0);
                // Flow through to set the minutes, etc.
            case Calendar.HOUR_OF_DAY:
            case Calendar.HOUR:
                cal.set(Calendar.MINUTE, 0);
                // Flow through to set the seconds, etc.
            case Calendar.MINUTE:
                cal.set(Calendar.SECOND, 0);
                // Flow through to set the milliseconds, etc.
            case Calendar.MILLISECOND:
            case Calendar.SECOND:
                cal.set(Calendar.MILLISECOND, 0);
                break;
            default:
                throw new IllegalArgumentException(
                        "Only following Calendar constants are supported: " +
                        "MILLISECOND,SECOND,HOUR,HOUR_OF_DAY,DAY_OF_YEAR,DAY_OF_MONTH,MONTH,YEAR");
        }
    }

}
