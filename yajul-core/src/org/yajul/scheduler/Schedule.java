package org.yajul.scheduler;

import java.util.Iterator;

/**
 * An absract list of schedule entries for the scheduler.
 */
public interface Schedule
{
    /**
     * Invoked by the scheduler whenever the scheduler is started.
     */
    public void start(Scheduler scheduler);
    /**
     * Invoked by the scheduler whenever the scheduler is being
     * shut down.
     */
    public void stop(Scheduler scheduler);
    /**
     * Invoked by the scheduler whenever a new job is to be added to the list.
     */
    public void add(ScheduleEntry entry);
    /**
     * Invoked by the scheduler whenver a job is to be removed from the list.
     */
    public void remove(ScheduleEntry entry);
    /**
     * Invoked by the scheduler to retrieve a complete list of the job
     * entries.
     */
    public Iterator iterator();
}