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

import junit.framework.TestCase;
import org.yajul.scheduler.Scheduler;
import org.yajul.scheduler.ScheduleEntry;
import org.yajul.scheduler.ScheduledShellTask;

import java.util.Calendar;
import java.io.ByteArrayOutputStream;

/**
 * Tests the Scheduler.
 */
public class SchedulerTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * @param name The name of the test case.
     */
    public SchedulerTest(String name)
    {
        super(name);
    }

    /**
     * Tests the basic functionality of the scheduler.
     */
    public void testBasicScheduling() throws Exception
    {
        System.out.println("---> Scheduler test.");

        Scheduler ts = new Scheduler();

        System.out.println("---> Starting scheduler...");
        ts.start();

        Calendar when = Calendar.getInstance();
        when.add(Calendar.SECOND, 5);

        ScheduleEntry t1 = new ScheduleEntry(ts, null,
                when.getTime(), Calendar.SECOND, 3,
                new TestJobRunnable());
        System.out.println("---> Add JobEntry1");
        ts.addJob(t1);

        when.add(Calendar.SECOND, 1);
        ScheduleEntry t2 = new ScheduleEntry(ts, null,
                when.getTime(), Calendar.SECOND, 2,
                new TestJobRunnable());
        System.out.println("---> Add JobEntry2");
        ts.addJob(t2);

        System.out.println("---> Sleeping (1) ...");
        Thread.sleep(15000);
        System.out.println("---> Remove JobEntry1");
        ts.removeJob(t1);
        System.out.println("---> Sleeping (2) ...");
        Thread.sleep(5000);
        System.out.println("---> Remove JobEntry2");
        ts.removeJob(t2);

        System.out.println("---> Sleeping (3) ...");
        Thread.sleep(5000);
        System.out.println("---> Add JobEntry2 (no overlap)");
        t2.setOverlap(false);
        ts.addJob(t2);
        System.out.println("---> Sleeping (4) ...");
        Thread.sleep(5000);
        System.out.println("---> Shutdown");
        ts.shutdown();
        t2.setOverlap(true);

        System.out.println("---> Restart");
        ts.start();
        System.out.println("---> Sleeping (5) ...");
        Thread.sleep(5000);
        System.out.println("---> Shutdown");
        ts.shutdown();
        System.out.println("---> Scheduler test completed.");
    }

    public void testScheduledShellTask() throws Exception
    {
        runShellTask("java -?");
        runShellTask("java -version");
    }

    private void runShellTask(String command)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        ScheduledShellTask sst = new ScheduledShellTask(command,out,err);
        sst.run();
        System.out.println("\n--out--\n" + out.toString());
        System.out.println("\n--err--\n" + err.toString());
    }
}
