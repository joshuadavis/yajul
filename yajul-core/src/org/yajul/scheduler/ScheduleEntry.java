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

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Calendar;

/**
 * Represents an individal scheduled ScheduleEntry.  When instances of ScheduleEntry are
 * added to a Scheduler, the scheduler will being starting threads and
 * running the runnable object 1at the scheduled intervals (only if the
 * scheduler has been started).
 * The scheduling behavior can be customized via inheritance.
 */
public class ScheduleEntry
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(ScheduleEntry.class);

    /**
     * Repeat interval value that indicates the job should only execute once.
     */
    public final static int NON_REPEATING = -1;

    /** The name of the ScheduleEntry. **/
    private String name;
    /** The ScheduleEntry scheduler that will invoke the ScheduleEntry. **/
    private Scheduler scheduler;
    /** The action to perform. **/
    private ScheduledTask runnable;
    /** The thread the job is running on (null if not running). **/
    private Thread thread;

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

    /** Static id number for name generation. **/
    private static int nextId = 0;

    /**
     * Internal method that does the work for all of the constructors.
     */
    private void initialize(Scheduler scheduler, String name,
                            Date start, int intervalUnit, int interval,
                            ScheduledTask runnable) throws Exception
    {
        if (scheduler == null)
            throw new NullPointerException("Scheduler is null");

        if (start == null)
            throw new NullPointerException("Start time is null");

        // TODO: Replace this with an ID generator.
        synchronized (this)
        {
            id = nextId++;
        }

        if (name == null)
            name = "ScheduleEntry-" + id;

        this.scheduler = scheduler;
        this.runnable = runnable;
        this.start = start;
        next = start;                // The next execution is at the start time.
        this.name = name;
        rescheduleUnit = this.intervalUnit = intervalUnit;
        reschedule = this.interval = interval;
        count = 0;                    // Not executed yet.
        rescheduleCount = 0;          // No re-schedulings yet.
        overlap = true;               // Allow overlap by default.
        enabled = true;               // Enabled, by default.
    }

    /**
     * Creates a new <b>non-repeating</b> job entry that will execute at the
     * 'start' time, and then remove itself from the list.
     * @param scheduler The Scheduler that will run the job.
     * @param start The start time.
     * @param runnable The runnable job.
     * @exception java.lang.Exception
     */
    public ScheduleEntry(Scheduler scheduler, Date start, ScheduledTask runnable)
            throws Exception
    {
        this.initialize(scheduler, null, start, NON_REPEATING,
                NON_REPEATING, runnable);
    }

    /**
     * Creates a new <b>repeating</b> job entry that will immediately execute
     * the runnable object on a thread at <i>interval</i> second intervals.
     * @param scheduler The Scheduler that will run the job.
     * @param interval The time interval, in seconds.
     * @param runnable The runnable job.
     * @exception java.lang.Exception
     */
    public ScheduleEntry(Scheduler scheduler, int interval, ScheduledTask runnable)
            throws Exception
    {
        this(scheduler, Calendar.SECOND, interval, runnable);
    }

    /**
     * Creates a new <b>repeating</b> job entry that will immediately execute
     * the runnable object on a thread at intervals of <i>interval</i> units,
     * where the time units are specified by <i>intervalUnit</i>.
     * @param scheduler The Scheduler that will run the job.
     * @param intervalUnit Interval unit from java.util.Calendar.
     * @param interval The time interval, in seconds.
     * @param runnable The runnable job.
     * @exception java.lang.Exception
     * @see java.util.Calendar
     */
    public ScheduleEntry(Scheduler scheduler, int intervalUnit,
                    int interval, ScheduledTask runnable)
            throws Exception
    {
        this(scheduler, null, new Date(), intervalUnit, interval, runnable);
    }

    /**
     * Creates a new <b>repeating</b> job entry that will execute the runnable
     * object on a thread starting at the <i>start</i> time, and repeating at
     * intervals of <i>interval</i> units, where the time units are specified
     * by <i>intervalUnit</i>.
     * @param scheduler The Scheduler that will run the job.
     * @param name The name of the ScheduleEntry.
     * @param start The start time for the job.
     * @param intervalUnit Interval unit from java.util.Calendar.
     * @param interval The time interval, in seconds.
     * @param runnable The runnable job.
     * @exception java.lang.Exception
     */
    public ScheduleEntry(Scheduler scheduler, String name,
                    Date start, int intervalUnit, int interval,
                    ScheduledTask runnable) throws Exception
    {
        if (interval <= 0)
            throw new Exception("Interval must be > 0");
        initialize(scheduler, name, start, intervalUnit, interval, runnable);
    }

    /**
     * Returns the name of the ScheduleEntry.
     * @return The name of the ScheduleEntry.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the starting time.
     * @return The starting time of the ScheduleEntry.
     */
    public Date getStartTime()
    {
        return start;
    }

    /**
     * Returns the scheduler for the entry.
     * @return The scheduler that will execute the job.
     */
    public Scheduler getJobScheduler()
    {
        return scheduler;
    }

    /**
     * Returns the target runnable object.
     * @return The runnable object that will be executed.
     */
    public ScheduledTask getJobRunnable()
    {
        return runnable;
    }

    /**
     * Returns the last time the job was executed.
     * @return The last time the job was executed.
     */
    public Date getLastTime()
    {
        return last;
    }

    /**
     * Returns the next scheduled execution time.
     * @return The next scheduled time for the job to be executed.
     */
    public Date getNextTime()
    {
        return next;
    }

    /**
     * The number of times the job has executed.
     * @return The number of times the job has been executed.
     */
    public int getRunCount()
    {
        return count;
    }

    /**
     * TRUE if this job can be run simultaneously
     * @return TRUE if the scheduler is to allow multiple threads to execute
     * simultaneously on this job.
     */
    public boolean allowsOverlap()
    {
        return overlap;
    }

    /**
     * Set to true to allow simultaneous execution, false to disallow.
     * @param overlap Set to true to allow simultaneous execution.
     */
    public void setOverlap(boolean overlap)
    {
        this.overlap = overlap;
    }

    /**
     * Set to true to enable the job, false to disable.
     * @param flag Set to true enable the job, false to disable
     */
    public void enable(boolean flag)
    {
        enabled = flag;
    }

    /**
     * Returns TRUE if the job is enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * The thread that executed the most recent activation of the job.
     * @return The thread that is currently (most recently) executing this job.
     */
    public Thread getThread()
    {
        return thread;
    }

    /**
     * Set the re-scheduling unit and interval.  Rescheduling occurs when a
     * job cancels (from the before() method) or when an non-overlapping job
     * overlaps.
     * @param unit The re-schedle time unit (from Calendar, for
     * example: Calendar.SECOND)
     * @param interval The number of 'units' to add to the current time
     * when re-scheduling
     */
    public void setRescheduling(int unit, int interval)
    {
        rescheduleUnit = unit;
        reschedule = interval;
    }

    /**
     * Returns TRUE if the job is running.
     * @return TRUE if the most recent thread executing this job is still alive.
     */
    public boolean isRunning()
    {
        synchronized (this)
        {
            if (thread == null)
                return false;
            else
                return thread.isAlive();
        }
    }

    /**
     * Returns the number of re-schedulings.
     * @return Returns the number of times this job has been re-scheduled.
     */
    public int getRescheduleCount()
    {
        return rescheduleCount;
    }

    /**
     * Returns true if the job is repeating.
     */
    public boolean isRepeating()
    {
        return interval == NON_REPEATING ? false : true;
    }

    private final static Date calculateNext(int unit, int interval, Date d)
    {
        Date now = new Date();
        Calendar next = Calendar.getInstance();
        next.setTime(d);
        long before = next.getTime().getTime();
        next.add(unit, interval);
        //  Ensure that the date is actually in the future... ;->
        long after = next.getTime().getTime();
        long diff = after - now.getTime();
        if (diff < 0)                                       // If the date is in the past...
        {
            diff = -diff;
            long perInterval = after - before;              // Milliseconds per interval.
            long count = diff / perInterval;                // Number of intervals to get to 'now'.
            if ((diff % perInterval) > 0)                   // Add one if the modulo is not zero
                count++;
            next.add(unit, interval * (int) count);         // Add the number of intervals to get past 'now'.
        }
        return next.getTime();
    }

    /**
     * Invoked by the Scheduler when a job has cancelled.  This implementation
     * will reset the time to the next interval.  Override this method to
     * customize the re-scheduling algorithm.
     */
    public void reschedule()
    {
        synchronized (this)
        {
            next = calculateNext(rescheduleUnit, reschedule, next);
            rescheduleCount++;            // Increment the number of re-schedulings.
        }
    }

    /**
     * Invoked by the Scheduler when a job has overlapped.  This implementation
     * will reset the time to the next interval.  Override this method to
     * customize the re-scheduling algorithm.
     */
    public void overlap()
    {
        synchronized (this)
        {
            reschedule();
        }
    }

    /**
     * Internal method invoked by the Scheduler to start a job.
     */
    public final boolean execute()
    {
        last = next;                                // Set the last execution time to be the scheduled execution time.
        boolean flag = runnable.before(this);       // Tell the runnable object it is about to be executed.
        if (!flag)                                  // Cancel the execution?
        {
            log.info("execute() : " + getName() + " cancelled execution!");
            return false;                           // If the runnable doesn't want to run, notify the scheduler.
        }

        String msg = (isRunning()) ? "ScheduleEntry (overlapped) " : "ScheduleEntry ";

        if (isRepeating())                          // If this is a repeating job,
            next = calculateNext(intervalUnit,      // compute the next execution time.
                    interval, next);
        else                                        // If this is a non-repeating job,
            scheduler.removeJob(this);              // remove it from the schedule.

        count++;                                    // Increment the number of executions.
        thread = new Thread(runnable);              // TODO: Get a thread from a pool inside the scheduler?
        thread.setName(getName() + ":" + thread.getName());
        thread.start();                             // Begin executing the job on the new thread.
        log.info(msg + getName() + " ... Thread " + thread.getName() + " started.");
        return true;
    }
}