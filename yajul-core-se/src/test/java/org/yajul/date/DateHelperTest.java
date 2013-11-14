package org.yajul.date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Test DateHelper functions.
 *
 * Uses joda-time as a reference.
 *
 * <br>
 * User: josh
 * Date: 9/28/11
 * Time: 10:59 AM
 */
public class DateHelperTest
{
    @Test
    public void testMakeTimeMillis()
    {
        long midnightMillis = DateHelper.MIDNIGHT_MILLIS;
        DateTime base = new DateTime(midnightMillis);
        DateTime expected = base.withTime(1, 23, 45, 67);
        long x = expected.getMillis();
        long y = DateHelper.makeTimeMillis(1,23,45,67);
        assertEquals(x, y);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooManyArgsForMakeTime()
    {
        DateHelper.makeTimeMillis(1, 23, 45, 67, 666);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooFewArgsForMakeTime()
    {
        DateHelper.makeTimeMillis();
    }

    @Test
    public void testGregorianCalendar()
    {
        DateTime m = new DateTime(2008, 8, 31, 23, 21, 34, 134);
        GregorianCalendar c = DateHelper.asGregorianCalendar(m.toDate());
        assertEquals(c.get(Calendar.YEAR),2008);
        assertEquals(c.get(Calendar.MONTH),Calendar.AUGUST);
        assertEquals(c.get(Calendar.DAY_OF_MONTH),31);
        assertEquals(c.get(Calendar.HOUR_OF_DAY),23);
        assertEquals(c.get(Calendar.MINUTE),21);
        assertEquals(c.get(Calendar.SECOND),34);
        assertEquals(c.get(Calendar.MILLISECOND),134);
    }

    @Test
    public void testMax()
    {
        DateTime m = new DateTime(2008, 8, 31, 23, 21, 34, 134);
        DateTime n = m.minusSeconds(4);
        assertEquals(DateHelper.max(m.toDate(),n.toDate()),m.toDate());
    }

    @Test
    public void testFormat()
    {
        DateTime m = new DateTime(2008, 8, 31, 23, 21, 34, 134, DateTimeZone.UTC);
        assertEquals(DateHelper.formatDate(m.toDate()), "2008-08-31");
        assertEquals(DateHelper.formatNullableDate(null),"");
        assertEquals(DateHelper.formatDate(m.toDate(), DateHelper.MM_DD_YYYY), "08/31/2008");
        assertEquals(DateHelper.formatNullableDate(null, DateHelper.YYYY_MM_DD),"");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testFormatNullDate()
    {
        DateHelper.formatDate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormatNullFormat()
    {
        DateHelper.formatDate(new Date(),null);
    }

    @Test
    public void testCleanDate()
    {
        DateTime m = new DateTime(2008, 8, 31, 23, 21, 34, 134);
        Timestamp t = new Timestamp(m.getMillis());
        Date d = m.toDate();
        // Make sure the JDK is messed up... (Timestamp != Date, but Date == Timestamp)
        assertTrue(d.equals(t));
        assertFalse(t.equals(d));

        assertEquals(DateHelper.cleanDate(d), d);
        assertEquals(DateHelper.cleanDate(t), d);
        assertEquals(DateHelper.cleanDate(t).getClass(), Date.class);
        assertNull(DateHelper.cleanDate(null));
    }


    @Test
    public void testFloorToDayStart()
    {
        DateTime m = new DateTime(2008, 8, 31, 23, 21, 34, 134);
        DateTime d = new DateTime(2008, 8, 31, 0, 0, 0, 0);
        final Date actual = DateHelper.floorToDayStart(m.toDate());
        assertEquals(actual, d.toDate());
        // final Date expected = DateUtil.floorToDayStart(m.toDate());
        // Replaced with hard coded value [jsd]
        final Date expected = new Date(1220155200000L);
        assertEquals(actual, expected);
    }
}
