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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The default implementation of a job list for the Scheduler.
 */
public class SimpleSchedule implements Schedule
{
    private ArrayList entries = new ArrayList();
    private Scheduler scheduler;
        
    SimpleSchedule() {}

    public void start(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }
        
    public void stop(Scheduler scheduler)
    {
        scheduler = null;
    }
        
    public void add(ScheduleEntry scheduleEntry)
    {
        if (entries.contains(scheduleEntry))
            throw new RuntimeException("Duplicate ScheduleEntry");
        entries.add(scheduleEntry);
        notifyScheduler();
    }

    private void notifyScheduler()
    {
        // Notify the scheduler that the schedule has changed.
        if (scheduler != null)
            scheduler.refresh();
    }

    public void remove(ScheduleEntry scheduleEntry)
    {
        entries.remove(scheduleEntry);
        notifyScheduler();
    }

    public Iterator iterator()
    {
        return entries.iterator();
    }
}
    
