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
import org.yajul.io.StreamCopier;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A command line shell job class for use with Scheduler.
 * @see Scheduler
 */
public class ScheduledShellTask implements ScheduledTask
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(ScheduledShellTask.class);

    private OutputStream out;

    private OutputStream err;

    /**
     * The OS-specific command line that will be executed.
     */
    protected String commandLine;
    /**
     * The child process that the command line is executing in.
     * This will be null if the command line is not currently executing.
     */
    protected Process process;
    /**
     * The status code of the last execution.
     */
    protected int status = 0;

    /**
     * Returns the command line string.
     */
    public String getCommandLine()
    {
        return commandLine;
    }

    /**
     * Returns the child process that the command line is executing in.
     */
    public Process getProcess()
    {
        return process;
    }

    /**
     * The status code of the last execution.
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Creates a new command line task.
     * @param commandLine The (system-specific) command line to execute.
     * @param out Optional OutputStream that will capture the 'standard output'
     * of the child process.
     * @param err Optional OutputStream that will capture the 'standard error'
     * stream of the child process.
     */
    public ScheduledShellTask(String commandLine,
                              OutputStream out,
                              OutputStream err)
    {
        this.out = out;
        this.err = err;
        this.commandLine = commandLine;
        process = null;
        // Use System.out if no output sream was specified.
        if (this.out == null)
            this.out = System.out;
        if (this.err == null)
            this.err = System.err;
    }

    /**
     * Creates a new command line task.  The standard output will be piped into System.out.
     * @param commandLine The (system-specific) command line to execute.
     */
    public ScheduledShellTask(String commandLine)
    {
        this(commandLine, null, null);
    }

    public boolean before(ScheduleEntry entry)
    {
        return true;
    }

    public void run()
    {
        Runtime rt = Runtime.getRuntime();

        try
        {
            log.info("Starting '" + commandLine + "'");
            process = rt.exec(commandLine);
            InputStream in = process.getInputStream();
            // Copy stderr on another thread.
            StreamCopier errCopier = new StreamCopier(process.getErrorStream(),err);
            Thread errThread = new Thread(errCopier);
            errThread.start();
            // Copy stdout on this thread.
            StreamCopier.unsyncCopy(in,out,StreamCopier.DEFAULT_BUFFER_SIZE);
            // Wait for the process to complete.
            status = process.waitFor();
            // Wait for the error stream thread to complete.
            errThread.join();
            log.info("Completed '" + commandLine + "', status = " + status);
        }
        catch (java.lang.Exception ex)
        {
            log.error("run() : Exception from '" + commandLine + "'", ex);
        }
    }

    public void shutdown(ScheduleEntry entry)
    {
    }

    // --- Implementation methods --
    private void copyStdout() throws java.io.IOException
    {
    }

}