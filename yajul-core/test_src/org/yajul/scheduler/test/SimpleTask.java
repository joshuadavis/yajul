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
package org.yajul.scheduler.test;

import org.apache.log4j.Logger;
import org.yajul.scheduler.ScheduledTask;
import org.yajul.scheduler.ScheduleEntryAttributes;
import org.yajul.scheduler.ScheduleEntry;

/**
 * Internal class for the test harness.
 */
class SimpleTask implements ScheduledTask
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(SimpleTask.class);

    /** The amount of time to 'run', in milliseconds. **/
    private long sleepTime;

    public SimpleTask(long sleepTime)
    {
        this.sleepTime = sleepTime;
    }

    public boolean before(ScheduleEntry entry)
    {
        log.info(entry.getAttributes().getName() + " before");
        return true;    // Okay to execute!
    }

    public void run()
    {
        String name = Thread.currentThread().getName();
        log.info("<" + name + "> running");
        try
        {
            Thread.sleep(sleepTime);
        }
        catch (Exception e)
        {
            log.error("Unexpected exception thrown from sleep()", e);
        }
        log.info("<" + name + "> completed");
    }

    public void shutdown(ScheduleEntry entry)
    {
        log.info(entry.getAttributes().getName() + " shutdown!");
    }
}
