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

import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Internal class that ScheduleEntry uses to launch runnable iterator.
 */
class Monitor extends Thread
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(Monitor.class);

    static final int ONE_SECOND = 1000;
    /** The maximum wait time. **/
    static final int MAXIMUM_WAIT = ONE_SECOND * 15;
    /** The minimum wait time. **/
    static final int MINIMUM_WAIT = 10;

    private Scheduler scheduler;
    private boolean notified;

    Monitor(Scheduler scheduler)
    {
        this.scheduler = scheduler;
        notified = false;
        super.start();
    }


    /**
     * Internal method that the scheduler uses to un-block this thread.
     */
    synchronized void stateHasChanged()
    {
        notified = true;
        notify();
    }

    /**
     * Starts any iterator that need to be run.  Returns the minimum delay until the next
     * job needs to run.
     */
    private long startJobs()
    {
        // Look for the next ScheduleEntry execution in the list.
        Iterator iter = scheduler.iterator();
        long now = System.currentTimeMillis();
        long minimum = MAXIMUM_WAIT;     // The minimum wait time.


        while (iter.hasNext())              // Iterate through all schedule entries...
        {
            ScheduleEntry entry = (ScheduleEntry) iter.next();
            String name = entry.getAttributes().getName();

            boolean enabled = entry.getAttributes().isEnabled();
            if (!enabled)                   // Skip disabled iterator.
                continue;

            // Calculate the wait time for the entry.
            long waitTime = entry.getAttributes().getNextTime().getTime() - now;
            // If the job should be running now, then try to start it...
            if (waitTime <= 0)              // Should job be executing now?
            {
                boolean overlap = entry.getAttributes().allowsOverlap();
                if (!overlap &&             // If the job does not allow overlap,
                        entry.isRunning())  // and the job is running....
                {
                    // Re-schedule this job at the next interval.
                    log.info("Overlap detected, re-scheduling " + name);
                    entry.overlap();
                }
                else
                {
                    // The job should start now.
                    // This will set the next execution time.
                    boolean launched = entry.execute();
                    // If the job cancelled execution, reschedule it for later.
                    if (!launched)
                        entry.reschedule();
                }
                // Re calculate the wait time based on the new schedule.
                waitTime = entry.getAttributes().getNextTime().getTime() - now;
            }

            if (minimum > waitTime)                 // New minimum?
                minimum = waitTime;                 // Remember it for later.
        }

        // Make sure a negative wait time is not returned.
        if (minimum < 0)
            minimum = 0;

        return minimum;                             // Return the minimum wait time.
    }

    public void run()
    {
        try
        {
            // The main scheduler loop.
            log.info("run() : Job scheduler started.");
            boolean loop = true;
            while (loop)
            {
                // Start any pending iterator and get the minimum wait time.
                long waitTime = startJobs();

                // If the wait time is very small, go back to the top of the loop.
                if (waitTime < MINIMUM_WAIT)
                    continue;

                synchronized (this)
                {
                    if (log.isDebugEnabled())
                        log.debug("run() Waiting for "+waitTime+"ms ...");
                    // Wait for the specified minimum wait time, or for any new iterator.
                    notified = false;
                    // Give derived classes a chance to do something before waiting.
                    scheduler.idle(waitTime);
                    try
                    {
                        wait(waitTime);
                    }
                    catch (InterruptedException ie)
                    {
                        log.error("run() : Job scheduler loop interrupted!", ie);
                        break;
                    }

                    if (notified)
                    {
                        log.info("Scheduler state changed!");
                        notified = false;
                    }

                    // Check for a shutdown request.
                    if (scheduler.isShuttingDown())
                    {
                        log.info("Shutdown request received.");
                        loop = false;
                    }
                } // synchronized block
                // Otherwise, continue looping.
            } // while
            log.info("Job scheduler stopping...");
        }
        catch (Exception e)
        {
            log.error(
                    "An unexpected exception was thrown in the Scheduler loop!",
                    e);
        }
        finally
        {
            // Notify all entries that a shutdown is happening.
            try
            {
                Iterator iter = scheduler.iterator();
                while (iter.hasNext())
                {
                    ScheduleEntry entry = (ScheduleEntry) iter.next();
                    entry.getScheduledTask().shutdown(entry);
                }
            }
            catch (Exception ex)
            {
                log.error("Unexpected exception thrown while shutting down iterator!",ex);
            }
            log.info("Job scheduler stopped.");
        }
    }

}