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
import org.yajul.util.DateFormatConstants;

import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Represents an individal scheduled ScheduleEntry.  When instances of ScheduleEntry are
 * added to a Scheduler, the scheduler will being starting threads and
 * running the task object 1at the scheduled intervals (only if the
 * scheduler has been started).
 * The scheduling behavior can be customized via inheritance.
 */
public class ScheduleEntry
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(ScheduleEntry.class);

    /** The ScheduleEntry scheduler that will invoke the ScheduleEntry. **/
    private Scheduler scheduler;
    /** The action to perform. **/
    private ScheduledTask task;

    private int runningTaskCount = 0;

    private ScheduleEntryAttributes attributes;

    /** Format for displaying timestamps. **/
    private DateFormat timeFormat;

    /**
     * Internal method that does the work for all of the constructors.
     */
    private void initialize(Scheduler scheduler, String name,
                            Date start, int intervalUnit, int interval,
                            ScheduledTask runnable)
    {
        if (scheduler == null)
            throw new NullPointerException("Scheduler is null");

        if (start == null)
            throw new NullPointerException("Start time is null");

        long id = scheduler.nextId();

        timeFormat = new SimpleDateFormat(
                DateFormatConstants.ISO8601_DATETIME_FORMAT);

        if (name == null)
            name = "ScheduleEntry-" + id;

        this.scheduler = scheduler;
        this.task = runnable;
        this.attributes = new SimpleScheduleEntryAttributes();
        attributes.setStartTime(start);
        attributes.setNextTime(start);
        attributes.setName(name);
        attributes.setRescheduleUnit(intervalUnit);
        attributes.setIntervalUnit(intervalUnit);
        attributes.setReschedule(interval);
        attributes.setInterval(interval);
        attributes.setOverlap(true);               // Allow overlap by default.
        attributes.enable(true);               // Enabled, by default.
    }

    /**
     * Creates a new <b>non-repeating</b> job entry that will execute at the
     * 'start' time, and then remove itself from the list.
     * @param scheduler The Scheduler that will run the job.
     * @param start The start time.
     * @param runnable The task job.
     * @exception java.lang.Exception
     */
    public ScheduleEntry(Scheduler scheduler, Date start, ScheduledTask runnable)
            throws Exception
    {
        this.initialize(scheduler, null, start, ScheduleEntryAttributes.NON_REPEATING,
                ScheduleEntryAttributes.NON_REPEATING, runnable);
    }

    /**
     * Creates a new <b>repeating</b> job entry that will immediately execute
     * the task object on a thread at <i>interval</i> second intervals.
     * @param scheduler The Scheduler that will run the job.
     * @param interval The time interval, in seconds.
     * @param runnable The task job.
     * @exception java.lang.Exception
     */
    public ScheduleEntry(Scheduler scheduler, int interval, ScheduledTask runnable)
            throws Exception
    {
        this(scheduler, Calendar.SECOND, interval, runnable);
    }

    /**
     * Creates a new <b>repeating</b> job entry that will immediately execute
     * the task object on a thread at intervals of <i>interval</i> units,
     * where the time units are specified by <i>intervalUnit</i>.
     * @param scheduler The Scheduler that will run the job.
     * @param intervalUnit Interval unit from java.util.Calendar.
     * @param interval The time interval, in seconds.
     * @param runnable The task job.
     * @see java.util.Calendar
     */
    public ScheduleEntry(Scheduler scheduler, int intervalUnit,
                    int interval, ScheduledTask runnable)
    {
        this(scheduler, null, new Date(), intervalUnit, interval, runnable);
    }

    /**
     * Creates a new <b>repeating</b> job entry that will execute the task
     * object on a thread starting at the <i>start</i> time, and repeating at
     * intervals of <i>interval</i> units, where the time units are specified
     * by <i>intervalUnit</i>.
     * @param scheduler The Scheduler that will run the job.
     * @param name The name of the ScheduleEntry.
     * @param start The start time for the job.
     * @param intervalUnit Interval unit from java.util.Calendar.
     * @param interval The time interval, in seconds.
     * @param runnable The task job.
     */
    public ScheduleEntry(Scheduler scheduler, String name,
                    Date start, int intervalUnit, int interval,
                    ScheduledTask runnable)
    {
        if (interval <= 0)
            throw new IllegalArgumentException("Interval must be > 0");
        initialize(scheduler, name, start, intervalUnit, interval, runnable);
    }

    public ScheduleEntryAttributes getAttributes()
    {
        return attributes;
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
     * Returns the target task object.
     * @return The task object that will be executed.
     */
    public ScheduledTask getScheduledTask()
    {
        return task;
    }

    /**
     * Returns TRUE if the job is running.
     * @return TRUE if the most recent thread executing this job is still alive.
     */
    public boolean isRunning()
    {
        synchronized (this)
        {
            return runningTaskCount > 0;
        }
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
            attributes.setNextTime(
                    calculateNext(
                            attributes.getRescheduleUnit(),
                            attributes.getReschedule(),
                            attributes.getNextTime()));
            // Increment the number of re-schedulings.
            attributes.incrementRescheduleCount(1);
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
        attributes.setLastTime(
                attributes.getNextTime());          // Set the last execution time to be the scheduled execution time.
        boolean flag = task.before(this);           // Tell the task object it is about to be executed.
        if (!flag)                                  // Cancel the execution?
        {
            log.info("execute() : " + attributes.getName() + " cancelled execution!");
            return false;                           // If the task doesn't want to run, notify the scheduler.
        }

        String msg = (isRunning()) ? "ScheduleEntry (overlapped) " : "ScheduleEntry ";

        if (attributes.isRepeating())               // If this is a repeating job,
        {
            attributes.setNextTime(
                calculateNext(                      // compute the next execution time.
                    attributes.getIntervalUnit(),
                    attributes.getInterval(),
                    attributes.getNextTime()));
            if (log.isDebugEnabled())
                log.debug("execute() : " + msg + " next time: "
                        + timeFormat.format(attributes.getNextTime()));
        }
        else                                        // If this is a non-repeating job,
            scheduler.remove(this);                 // remove it from the schedule.

        startTask();

        return true;
    }

    void incrementRunningTaskCount()
    {
        synchronized (this)
        {
            runningTaskCount++;
        }
    }

    void decrementRunningTaskCount()
    {
        synchronized (this)
        {
            runningTaskCount--;
        }
    }

    private void startTask()
    {
        attributes.incrementRunCount(1);            // Increment the number of executions.

        // Create a new task runner.
        TaskRunner runner = new TaskRunner(this);
        // If there is no thread pool, start the task on a new thread.
        if (scheduler.getThreadPool() == null)
        {
            Thread thread = new Thread(runner);
            thread.setName(attributes.getName() + ":" + thread.getName());
            thread.start();
            if (log.isDebugEnabled())
                log.debug("startTask() : " + attributes.getName() + " started.");
        }
        else
        {
            scheduler.getThreadPool().add(runner);
            if (log.isDebugEnabled())
                log.debug("startTask() : " + attributes.getName()
                        + " added to thread pool.");
        }
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
}