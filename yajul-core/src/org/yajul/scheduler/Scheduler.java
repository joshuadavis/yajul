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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 * This class executes a set of jobs on muliple threads according to a
 * time table.  Each entry in the time table is specified as an instance
 * of ScheduleEntry.
 * <ul>
 * <li>Each job entry can be set to execute at a specific time.</li>
 * <li>Jobs can run on a single thread, or on multiple threads (overlap).</li>
 * <li>Jobs can be added or removed while the scheduler is running.</li>
 * <li>The scheduler can be shut down or re-started at any time.</li>
 * </ul>
 *
 * A test harness is provided by the static main() function of this class.
 *
 * <h2>Examples:</h2>
 * <h3>Creating a Scheduler</h3>
 * <pre>
 *    Scheduler aScheduler = new Scheduler();
 *    aScheduler.start();
 * </pre>
 * <h3>Adding a new scheduled task</h3>
 * <pre>
 *       Calendar when = Calendar.getInstance();
 *       when.add(Calendar.SECOND,5);               // Execute five seconds from now.
 *
 *       ScheduleEntry e = new ScheduleEntry(aScheduler,null,
 *           when.getTime(),Calendar.SECOND,3,      // Execute every 3 seconds after 5 seconds from now.
 *           someTask);
 *       aScheduler.addJob(e);
 * </pre>
 */
public class Scheduler
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(Scheduler.class);

    /** The full list of schedule entries. **/
    private Schedule schedule;
    /** Scheduler shutdown flag. **/
    private boolean shutdown;
    /** The main thread that schedules the tasks. **/
    private Monitor monitor;
    /** Format for displaying timestamps. **/
    private DateFormat timeFormat;

    /**
     * Creates a new Scheduler using the specified log file.
     */
    public Scheduler()
    {
        this(new SimpleSchedule());
    }

    /**
     * Creates a new scheduler for the given schedule.
     * @param schedule The schedule.
     */
    public Scheduler(Schedule schedule)
    {
        setSchedule(schedule);
        shutdown = false;
        timeFormat = new SimpleDateFormat(
                DateFormatConstants.ISO8601_DATETIME_FORMAT);
        log.info("Scheduler created.");
    }
    /**
     * Starts up the job scheduling thread.  Jobs will begin executing.
     */
    public void start()
            throws Exception
    {
        // TODO: Initialization goes here...
        log.info("Job scheduler starting...");
        shutdown = false;
        monitor = new Monitor(this);
    }

    /**
     * Refreshes the current job schedule thread.  Invoke this
     * whenever the current Schedule changes to cause the scheduler
     * to refresh it's execution plan.
     */
    public void refresh()
    {
        if (monitor != null)
            monitor.stateHasChanged();       // Un-block the main loop thread.
    }

    /**
     * Adds a new schedule entry to the schedulers list of jobs.  If the
     * scheduler is starting.
     * @param jobEntry New job entry for the scheduler.
     * @exception java.lang.Exception An exception is thrown when a duplicate job entry is added.
     */
    public void addJob(ScheduleEntry jobEntry) throws Exception
    {
        synchronized (this)
        {
            log.info("Adding " + jobEntry.getName() + " at "
                    + timeFormat.format(jobEntry.getNextTime()));
            schedule.add(jobEntry);
        }
    }

    /**
     * Removes a scheduled job entry from the list.
     * @param jobEntry
     */
    public void removeJob(ScheduleEntry jobEntry)
    {
        synchronized (this)
        {
            log.info("Removing " + jobEntry.getName() + " at " + timeFormat.format(jobEntry.getNextTime()));
            schedule.remove(jobEntry);
        }
    }

    /**
     * Shuts down the main scheduler thread.  Any executing job threads will still run to
     * completion.
     */
    public void shutdown()
    {
        synchronized (this)
        {
            log.info("Shutdown requested.");
            shutdown = true;
            schedule.stop(this);
            refresh();
        }
    }

    /**
     * @return TRUE if the scheduler is being shut down.
     */
    public boolean isShuttingDown()
    {
        synchronized (this)
        {
            return shutdown;
        }
    }

    /**
     * @return The enumeration of schedule entries currently known to the
     * scheduler.
     */
    public Iterator jobs()
    {
        if (schedule != null)
            return schedule.iterator();
        else
            return null;
    }

    /**
     * Called by the scheduling thread when it is about to wait.
     */
    public void idle(long waitTime)
    {
        // Does nothing by default.
    }

    /**
     * Sets the list of jobs for the scheduler.
     */
    public void setSchedule(Schedule list)
    {
        if (schedule != null)
            schedule.stop(this);
        schedule = list;
        refresh();
    }
}