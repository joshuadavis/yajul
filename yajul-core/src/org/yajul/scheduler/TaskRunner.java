package org.yajul.scheduler;

import org.apache.log4j.Logger;

/**
 * Runs the schedule entry task, and notifies the schedule entry of the
 * status.
 * User: jdavis
 * Date: Nov 25, 2003
 * Time: 4:32:11 PM
 * @author jdavis
 */
class TaskRunner implements Runnable
{
    /**
     * The logger for this class.
     */
    private static Logger log = Logger.getLogger(TaskRunner.class.getName());

    private ScheduleEntry entry;

    TaskRunner(ScheduleEntry scheduleEntry)
    {
        this.entry = scheduleEntry;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     Thread#run()
     */
    public void run()
    {
        log.info("Started.");
        try
        {
            entry.incrementRunningTaskCount();
            entry.getScheduledTask().run();
        }
        catch (Exception e)
        {
            log.error(e,e);
        }
        finally
        {
            entry.decrementRunningTaskCount();
        }
        log.info("Finished.");
    }
}
