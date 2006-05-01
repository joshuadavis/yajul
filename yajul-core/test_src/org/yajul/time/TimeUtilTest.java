/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
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
package org.yajul.time;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * Tests TimeUtil and other low-level time and calendar functions.
 */
public class TimeUtilTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * @param name The name of the test case.
     */
    public TimeUtilTest(String name)
    {
        super(name);
    }

    /**
     * Test TimeUtil calculations.
     */
    public void testTimeUtilCalculations() throws Exception
    {
        Date date = new Date();
        long dateMillis = date.getTime();
        long timeOfDay = TimeUtil.timeOfDayGMT(dateMillis);
        long startOfDay = TimeUtil.startOfDayGMT(dateMillis);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        assertEquals(dateMillis,cal.getTime().getTime());
        // Floor the calendar to the current day and check it.
        TimeUtil.floorCalendar(cal,Calendar.DAY_OF_YEAR);
        assertEquals(0,cal.get(Calendar.HOUR));
        assertEquals(0,cal.get(Calendar.MINUTE));
        assertEquals(0,cal.get(Calendar.SECOND));
        assertEquals(0,cal.get(Calendar.MILLISECOND));
        // The calendar should be the same as 'startOfDay'.
        assertEquals(cal.getTime().getTime(),startOfDay);

        // Set the year month and day to 1970, Jan, 1.  Leave time of day alone.
        cal.setTime(date);
        cal.set(1970,Calendar.JANUARY,1);
        assertEquals(cal.getTime().getTime(),timeOfDay);

        // Calculate the number of 24 hour days between yesterday and today.
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR,-1);
        long days = TimeUtil.daysBetween(cal.getTime().getTime(),dateMillis);
        assertEquals(1,days);
    }

    public void testStartGMT()
    {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        long now = c.getTimeInMillis();
        TimeUtil.floorCalendar(c,Calendar.DAY_OF_YEAR);
        long startOfDay = c.getTimeInMillis();
        long s = TimeUtil.startOfDayGMT(now);
        assertEquals(startOfDay,s);
    }
}
