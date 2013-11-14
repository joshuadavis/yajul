package org.yajul.date;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Helper methods for Calendar and Date.  Useful when you don't want / cannot use joda-time.
 * <br>
 * User: josh
 * Date: Aug 31, 2010
 * Time: 11:02:58 AM
 */
public class DateHelper
{
    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    /** ISO 8601 date format */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /** American style date format */
    public static final String MM_DD_YYYY = "MM/dd/yyyy";

    private static final long[] MILLIS = {MILLIS_PER_HOUR, MILLIS_PER_MINUTE, MILLIS_PER_SECOND, 1L};
    private static final Time MIDNIGHT = Time.valueOf("00:00:00");
    public static final long MIDNIGHT_MILLIS = MIDNIGHT.getTime();
    private static int[] CALENDAR_FIELDS = {
            Calendar.HOUR_OF_DAY,
            Calendar.MINUTE,
            Calendar.SECOND,
            Calendar.MILLISECOND,
    };

    /**
     * Make a time given an array of ints: hours, minutes, seconds millis.
     *
     * @param args hours, hours/minutes, etc.
     * @return the time in millis.
     */
    public static long makeTimeMillis(int... args)
    {
        long millis = MIDNIGHT_MILLIS;
        if (args == null || args.length < 1)
            throw new IllegalArgumentException("Not enough arguments!");
        if (args.length > MILLIS.length)
            throw new IllegalArgumentException("Too many arguments!");
        for (int i = 0; i < args.length; i++)
            millis += MILLIS[i] * args[i];
        return millis;
    }

    /**
     * Creates a date in the default time zone based on the given year month and day.
     * Takes the place of the deprecated java.util.Date constructor.
     *
     * @param year  the year
     * @param month the month (one based)
     * @param day   the day of the month
     * @return the date, time is midnight
     */
    public static Date ymdToDate(int year, int month, int day)
    {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        setMidnight(c);
        return c.getTime();
    }

    /**
     * Sets the time fields of the calendar to midnight.
     *
     * @param c the calendar
     */
    public static void setMidnight(Calendar c)
    {
        for (int field : CALENDAR_FIELDS)
            c.set(field, 0);
    }

    /**
     * Get the year in the century (e.g. 2008 -> 8).
     *
     * @param year the year
     * @return the year in the century
     */
    public static int yearOfCentury(int year)
    {
        int century = year / 100;
        return year - (century * 100);
    }

    /**
     * Converts a Date into a Calendar.
     *
     * @param date the date
     * @return a calendar.
     */
    public static Calendar asCalendar(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    /**
     * Converts a Date into a GregorianCalendar.
     *
     * @param date the date
     * @return a calendar.
     */
    public static GregorianCalendar asGregorianCalendar(Date date)
    {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return c;
    }

    /**
     * Format a date in ISO 8601 (YYYY-MM-DD) format, returns an empty string if
     * the argument is null.
     * @param date the date to format
     * @return the formatted date
     */
    public static String formatNullableDate(Date date)
    {
        return date == null ? "" : formatDate(date);
    }

    /**
     * Format a date in ISO 8601 (YYYY-MM-DD) format.
     * @param date the date
     * @return YYYY-MM-DD formatted date
     */
    public static String formatDate(Date date)
    {
        return formatDate(date, YYYY_MM_DD);
    }

    /**
     * Format a date using the given SimpleDateFormat format.
     * @param date the date
     * @param format the format string
     * @return the formatted date
     */
    public static String formatDate(Date date,String format)
    {
        if (date == null)
            throw new IllegalArgumentException("date cannot be null!");
        if (format == null)
            throw new IllegalArgumentException("format cannot be null!");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * Format a date using the given SimpleDateFormat format.
     * Returns an empty string if the date is null.
     * @param date the date
     * @param format the format string
     * @return the formatted date ir an empty string if the date was null
     */
    public static String formatNullableDate(Date date,String format)
    {
        return date == null ? "" : formatDate(date,format);
    }

    public static Date floorToDayStart(Date d)
    {
        Calendar c = asCalendar(d);
        setMidnight(c);
        return c.getTime();
    }

    /**
     * @param d something that might be a subclass of java.util.Date
     * @return an instance of java.util.Date
     */
    public static Date cleanDate(Date d)
    {
        if (d == null)
            return null;
        else
            return d.getClass() != Date.class ? new Date(d.getTime()) : d;
    }

    /**
     * @param d1 date
     * @param d2 date
     * @return d2 if d2 is later than d1, otherwise, d1
     */
    public static Date max(Date d1, Date d2)
    {
        return d2.after(d1) ? d2 : d1;
    }

    /**
     * @param c a Calendar
     * @return the one-based month of the year
     */
    public static int getMonth(Calendar c)
    {
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * @param c a Calendar
     * @return the year
     */
    public static int getYear(Calendar c)
    {
        return c.get(Calendar.YEAR);
    }
}
