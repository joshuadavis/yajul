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

/**
 * Implement this interface on objects that are to be executed by
 * the Scheduler.   The run() method from java.lang.Runnable will
 * perform the work.
 * @see Scheduler
 */
public interface ScheduledTask extends java.lang.Runnable
{    
    /**
     * This method will be called by the Scheduler just before the job thread
     * is created. The return value can be used to control execution.
     * <ul>
     * <li>true - Job will be executed on a new thread</li>
     * <li>false - Job is cancelled, no thread will be created</li>
     * </ul>
     */
    public boolean before(ScheduleEntry entry);
    
    /**
     * This method will be called by the Scheduler when it is shutting down.
     * This canb be used to clean up or signal the run() method to stop.
     */
    public void shutdown(ScheduleEntry entry);
}