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
package org.yajul.scheduler;

import java.util.Date;

/**
 * TODO: Add class javadoc
 * User: josh
 * Date: Nov 25, 2003
 * Time: 8:53:17 AM
 */
public class SimpleScheduleEntryAttributes implements ScheduleEntryAttributes
{
    /** The name of the ScheduleEntry. **/
    private String name;
    /** True if the job is enabled. **/
    private boolean enabled;
    /** True if execution overlap is allowed. **/
    private boolean overlap;
    /**
     * ScheduleEntry execution will not begin until this date.
     * The time of day in this date will be used for daily execution.
     */
    private Date start;
    /** Unit for incrementing the execution time (Calendar.{unit}) **/
    private int intervalUnit;
    /** # of units to increment.  -1 for non-repeating tasks. **/
    private int interval;
    /** Unit for incrementing the execution time (Calendar.{unit}) for 're-scheduling'. **/
    private int rescheduleUnit;
    /** # of units to increment for rescheduling. **/
    private int reschedule;
    /** The next time this ScheduleEntry will execute. **/
    private Date next;
    /** The last time this ScheduleEntry was executed. **/
    private Date last;
    /** Number of times this ScheduleEntry has executed. **/
    private int count;
    /** Number of times this ScheduleEntry has been re-scheduled. **/
    private int rescheduleCount;
    /** Id number for the job entry. **/
    private int id;

    /**
     * Returns the name of the schedule entry.
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the start time of the schedule entry.
     * @return
     */
    public Date getStartTime()
    {
        return start;
    }

    /**
     * Returns the last time the schedule entry was executed.
     * @return
     */
    public Date getLastTime()
    {
        return last;
    }

    /**
     * Returns the next time the schedule entry will be executed.
     * @return
     */
    public Date getNextTime()
    {
        return next;
    }

    public int getRunCount()
    {
        return count;
    }

    /**
     * Returns true if overlapping threads for the same task are allowed.
     * @return
     */
    public boolean allowsOverlap()
    {
        return overlap;
    }

    public void setOverlap(boolean overlap)
    {
        this.overlap = overlap;
    }

    public void enable(boolean flag)
    {
        this.enabled = flag;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setRescheduling(int unit, int interval)
    {
        rescheduleUnit = unit;
        reschedule = interval;
    }

    public int getRescheduleCount()
    {
        return rescheduleCount;
    }

    public int getInterval()
    {
        return interval;
    }

    public boolean isRepeating()
    {
        return getInterval() == NON_REPEATING ? false : true;
    }

    public void incrementRunCount(int i)
    {
        count += i;
    }

    public void setLastTime(Date last)
    {
        this.last = last;
    }

    public void incrementRescheduleCount(int i)
    {
        this.rescheduleCount += i;
    }

    public void setNextTime(Date date)
    {
        this.next = date;
    }

    public int getRescheduleUnit()
    {
        return rescheduleUnit;
    }

    public int getReschedule()
    {
        return reschedule;
    }

    public void setStartTime(Date start)
    {
        this.start = start;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setRescheduleUnit(int unit)
    {
        this.rescheduleUnit = unit;
    }

    public void setIntervalUnit(int unit)
    {
        this.intervalUnit = unit;
    }

    public void setReschedule(int reschedule)
    {
        this.reschedule = reschedule;
    }

    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    public int getIntervalUnit()
    {
        return intervalUnit;
    }

}
