package org.yajul.log.test;

import junit.framework.TestCase;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.yajul.log.LogUtil;
import org.yajul.log.ThreadLocalAppender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.LineNumberReader;

/**
 * Tests the thread local log tee.
 * <br>
 * User: jdavis
 * Date: Sep 17, 2006
 * Time: 9:44:18 AM
 */
public class ThreadLocalAppenderTest extends TestCase
{
    private static Logger testlog = Logger.getLogger("test.log");
    private static final int ITERATIONS = 100;

    public void testThreadLocalLogFiles() throws Exception
    {
        Layout layout = new SimpleLayout();
        Thread[] threads = new Thread[3];
        LogWriter[] writers = new LogWriter[threads.length];
        for (int i = 0; i < threads.length; i++)
        {
            String filename = "threadlog-" + i + ".log";
            Appender a = LogUtil.createAsyncFileAppender(layout, filename, false);
            writers[i] = new LogWriter(i, a, ITERATIONS, filename);
            threads[i] = new Thread(writers[i]);
        }

        // Add the tread log appender to the test logger.  This logger will
        // send the log message to the appropriate thread-local appender.
        Appender localAppender = new ThreadLocalAppender();
        testlog.addAppender(localAppender);

        testlog.info("Staring threads...");

        for (int i = 0; i < threads.length; i++)
            threads[i].start();

        testlog.info("Joining threads...");

        for (int i = 0; i < threads.length; i++)
            threads[i].join();

        testlog.info("Reading log files...");
        for (int i = 0; i < writers.length; i++)
        {
            LogWriter writer = writers[i];
            String filename = writer.getFilename();
            // Count the number of lines while reading.
            LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(filename)));
            String line;
            while ((line = reader.readLine()) != null)
            {
                assertTrue("'id=' not found!", line.indexOf("id=" + writer.getId()) >= 0);
            }
            int lastLine = reader.getLineNumber();
            reader.close();
            assertEquals(ITERATIONS, lastLine);
        }
    }

    private static void logsomething(String x)
    {
        testlog.info(x);
    }

    private static class LogWriter implements Runnable
    {
        private int id;
        private int iterations;
        private Appender appender;
        private String filename;

        public LogWriter(int id, Appender appender, int iterations, String filename)
        {
            this.id = id;
            this.appender = appender;
            this.iterations = iterations;
            this.filename = filename;
        }

        public void run()
        {
            // Associate the appender with the thread.
            ThreadLocalAppender.getLocalAppenderAttachable().addAppender(appender);
            try
            {
                for (int i = 0; i < iterations; i++)
                {
                    logsomething("id=" + id + " iteration " + i);
                    //noinspection EmptyCatchBlock
                    try
                    {
                        Thread.sleep(ITERATIONS);
                    }
                    catch (InterruptedException ignore)
                    {
                    }
                }
            }
            finally
            {
                ThreadLocalAppender.getLocalAppenderAttachable().removeAppender(appender);
            }
        }

        public String getFilename()
        {
            return filename;
        }

        public int getId()
        {
            return id;
        }
    }
}
