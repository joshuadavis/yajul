package org.yajul.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.yajul.concurrent.HeartbeatMonitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.yajul.concurrent.HeartbeatMonitor.Status.*;


/**
 * Hearbeat monitor unit test
 * <br>User: Josh
 * Date: Mar 5, 2009
 * Time: 7:50:36 AM
 */
public class HeartbeatMonitorTest extends TestCase
{
    private final static Logger log = Logger.getLogger(HeartbeatMonitorTest.class.getName());

    public HeartbeatMonitorTest(String n)
    {
        super(n);
    }

    public void testMonitor() throws InterruptedException
    {
        ExecutorService exec = Executors.newCachedThreadPool();
        int scanInterval = 100;
        MockObserver observer = new MockObserver();
        HeartbeatMonitor monitor = new HeartbeatMonitor(scanInterval, exec, observer);
        int suspectTimeout = 20 * scanInterval;
        int failureTimeout = 30 * scanInterval;
        int fudgeFactor = 10;   // Slight delay to ensure a complete scan.
        monitor.createMonitor("one", suspectTimeout, failureTimeout);
        assertEquals(CREATED, observer.getStatus("one"));
        Thread.sleep(suspectTimeout + scanInterval + fudgeFactor);
        assertEquals(SUSPECTED, observer.getStatus("one"));
        Thread.sleep((failureTimeout - suspectTimeout) + 2 * scanInterval + fudgeFactor);
        assertEquals(FAILED, observer.getStatus("one"));
        monitor.heartbeat("one");
        assertEquals(RESTORED, observer.getStatus("one"));
        monitor.removeMonitor("one");
        monitor.createMonitor("two", 0, 10 * scanInterval);
        assertEquals(CREATED, observer.getStatus("two"));
        for (int i = 0; i < 20; i++)
        {
            Thread.sleep(scanInterval);
            monitor.heartbeat("two");
            assertEquals(CREATED, observer.getStatus("two"));
        }
        monitor.clear();
        assertEquals(CANCELED, observer.getStatus("two"));
    }

    public void testMonitorRemove()
    {
        // Make sure we don't NPE when removing the last monitor.
        ExecutorService exec = Executors.newCachedThreadPool();
        int scanInterval = 100;
        MockObserver observer = new MockObserver();
        HeartbeatMonitor monitor = new HeartbeatMonitor(scanInterval, exec, observer);
        int suspectTimeout = 20 * scanInterval;
        int failureTimeout = 30 * scanInterval;
        monitor.removeMonitor("one");
        monitor.heartbeat("one");
        monitor.createMonitor("one", suspectTimeout, failureTimeout);
        monitor.removeMonitor("one");
    }

    public static Test suite()
    {
        return new TestSuite(HeartbeatMonitorTest.class);
    }

    private static class MockObserver implements HeartbeatMonitor.HeartbeatObserver
    {
        private Map<String, HeartbeatMonitor.Status> statusById = new HashMap<String, HeartbeatMonitor.Status>();

        public HeartbeatMonitor.Timeouts getDefaultTimeouts(String id)
        {
            return new HeartbeatMonitor.Timeouts(1000, 3000);
        }

        public void onEvent(String id, HeartbeatMonitor.Status status, long lastHeartbeat)
        {
            Date last = new Date(lastHeartbeat);
            DateFormat df = new SimpleDateFormat(DateFormatConstants.ISO8601_DATETIME_FORMAT);
            if (log.isLoggable(Level.FINE))
                log.log(Level.FINE, "onEvent() : id=" + id + " status=" + status + " last=" + df.format(last));
            synchronized (this)
            {
                statusById.put(id, status);
            }
        }

        public HeartbeatMonitor.Status getStatus(String id)
        {
            synchronized (this)
            {
                return statusById.get(id);
            }
        }
    }
}
