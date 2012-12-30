package org.yajul.util;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.yajul.juli.LogHelper.unexpected;

/**
 * Monitors services using heartbeats, notifies observers of any failure to send a heartbeat within
 * a given amount of time.
 * <br>User: Josh
 * Date: Mar 5, 2009
 * Time: 6:44:58 AM
 */
public class HeartbeatMonitor {
    private final static Logger log = Logger.getLogger(HeartbeatMonitor.class.getName());

    /**
     * Used to name threads.
     */
    private final static AtomicInteger serialNumber = new AtomicInteger();

    /**
     * All the things being monitored, by id.
     */
    private Map<String, Monitor> monitorsById = CollectionUtil.newHashMap();

    /**
     * The observer that will be notified when things happen.
     */
    private HeartbeatObserver observer;

    /**
     * The timer thread.
     */
    private Timer timer;

    /**
     * The scan interval.
     */
    private long scanInterval;

    /**
     * An executor so that observers don't interfere with the timer thread.
     */
    private ExecutorService executor;

    public HeartbeatMonitor(long scanInterval,
                            ExecutorService executor,
                            HeartbeatObserver observer) {
        if (executor == null)
            throw new IllegalArgumentException("ExecutorService cannot be null!");
        if (observer == null)
            throw new IllegalArgumentException("HeartbeatObserver cannot be null!");
        this.observer = observer;
        this.executor = executor;
        this.scanInterval = scanInterval;
    }

    /**
     * Creates a new monitor for a given unique id.
     *
     * @param id             the unique id for the object to be monitored
     * @param suspectTimeout the suspect timeout
     * @param failureTimeout the failure timeout
     */
    public void createMonitor(String id,
                              long suspectTimeout,
                              long failureTimeout) {
        if (suspectTimeout > failureTimeout)
            throw new IllegalArgumentException("Suspect timeout must be <= failure timeout!");
        synchronized (this) {
            Monitor monitor = monitorsById.get(id);
            if (monitor != null)
                throw new IllegalArgumentException("Monitor for " + id + " already exists!");
            doCreate(id, new Timeouts(suspectTimeout, failureTimeout));
        }
    }

    private void doCreate(String id, Timeouts timeouts) {
        // If there is no timer thread, make one.
        if (timer == null) {
            timer = new Timer("HBMon-" + serialNumber.incrementAndGet(), true);
            ScannerTask task = new ScannerTask();
            timer.schedule(task, scanInterval, scanInterval);
        }
        Monitor monitor = new Monitor(id, timeouts);
        monitorsById.put(id, monitor);
    }

    /**
     * Stop monitoring
     *
     * @param id the id of the monitor
     */
    public void removeMonitor(String id) {
        Monitor monitor;
        synchronized (this) {
            monitor = monitorsById.remove(id);
            // If there's nothing left to monitor, stop the timer thread.
            if (monitorsById.isEmpty()) {
                if (log.isLoggable(Level.FINE))
                   log.log(Level.FINE, "removeMonitor() : Nothing left to monitor, canceling timer...");
                cancelTimer();
            }
        }
        if (monitor != null)
            monitor.cancel();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            if (log.isLoggable(Level.FINE))
                log.log(Level.FINE, "cancelTimer() : Scanning timer cancelled.");
        }
        timer = null;
    }

    /**
     * Stop all monitoring, remove all observers.
     */
    public void clear() {
        synchronized (this) {
            cancelTimer();
            for (Monitor monitor : monitorsById.values()) {
                monitor.cancel();
            }
            monitorsById.clear();
        }
    }

    /**
     * A heartbeat from a particular service or object
     *
     * @param id     the unique id for that object
     * @param create If true, create a monitor if none exists.  Ask the observer for the defaults.
     */
    public void heartbeat(String id, boolean create) {
        Monitor monitor;
        synchronized (this) {
            monitor = monitorsById.get(id);
            if (monitor == null && create)
                doCreate(id, observer.getDefaultTimeouts(id));
        }
        if (monitor != null) {
            monitor.heartbeat();
        }
    }

    /**
     * A heartbeat from a particular service or object
     *
     * @param id     the unique id for that object
     */
    public void heartbeat(String id) {
        heartbeat(id,false);
    }

    private void notifyObserver(String id, Status status, HeartbeatObserver observer, long lastHeartbeat) {
        try {
            observer.onEvent(id, status, lastHeartbeat);
        }
        catch (Exception e) {
            unexpected(log, e);
        }
    }

    /**
     * The events that observers will receive.
     */
    public static enum Status {
        /**
         * The monitor was created
         */
        CREATED,
        /**
         * The monitor had been suspected or failed, but has now been restored by a heartbeat
         */
        RESTORED,
        /**
         * The monitor is now suspect (about to fail)
         */
        SUSPECTED,
        /**
         * The monitor has not received a heartbeat, it has failed.
         */
        FAILED,
        /**
         * The monitor was cancelled.
         */
        CANCELED;

        public static EnumSet<Status> ALIVE = EnumSet.of(CREATED, RESTORED);
    }

    /**
     * The callback interface.
     */
    public static interface HeartbeatObserver {

        /**
         * Called when creating a monitor implicitly.
         * @param id the monitor id
         * @return the default timeout values
         */
        Timeouts getDefaultTimeouts(String id);

        /**
         * Called when something happens to a monitor
         *
         * @param id            the id of the monitor
         * @param status        what happened to the monitor
         * @param lastHeartbeat millis when the last heartbeat happened
         */
        void onEvent(String id, Status status, long lastHeartbeat);
    }

    public static class Timeouts {
        private long suspectTimeout;
        private long failureTimeout;

        public Timeouts(long suspectTimeout, long failureTimeout) {
            this.suspectTimeout = suspectTimeout;
            this.failureTimeout = failureTimeout;
        }

        public long getSuspectTimeout() {
            return suspectTimeout;
        }

        public long getFailureTimeout() {
            return failureTimeout;
        }
    }

    private class Monitor {
        // Immutable values
        private String id;
        private final Timeouts timeouts;
        // Mutable values
        private Status status;
        private long lastHeartbeat;

        private Monitor(String id, Timeouts timeouts) {
            this.id = id;
            this.timeouts = timeouts;
            this.lastHeartbeat = System.currentTimeMillis();
            this.status = Status.CREATED;
            notifyObserver(id, status, observer, lastHeartbeat);
        }

        private void heartbeat() {
            boolean restored;
            synchronized (this) {
                this.lastHeartbeat = System.currentTimeMillis();
                restored = status == Status.FAILED || status == Status.SUSPECTED;
            }
            // If this heartbeat restored the monitor, then notify.
            if (restored)
                setStatus(Status.RESTORED);
        }

        private void cancel() {
            setStatus(Status.CANCELED);
        }

        private void setStatus(Status status) {
            long last;
            synchronized (this) {
                if (this.status == status)
                    return;
                this.status = status;
                last = lastHeartbeat;   // Mutable, so copy it in a sync block.
            }
            notifyObserver(id, status, observer, last);
        }

        public void scan() {
            long now = System.currentTimeMillis();
            long last;
            synchronized (this) {
                // Copy mutable values in a sync block.
                last = lastHeartbeat;
            }
            long elapsed = now - last;
            if (elapsed > timeouts.getFailureTimeout()) {
                setStatus(Status.FAILED);
            }
            // Hasn't failed yet, maybe it's suspect.
            else if (timeouts.getSuspectTimeout() > 0 && elapsed > timeouts.getSuspectTimeout() &&
                    timeouts.getSuspectTimeout() < timeouts.getFailureTimeout()) {
                setStatus(Status.SUSPECTED);
            }
            // Neither failed nor suspect.  No change.
        }
    } // Monitor

    private class ScannerTask extends TimerTask {
        public void run() {
            // Don't run the scan on the timer thread, use an ExecutorService.
            executor.submit(new Runnable() {
                public void run() {
                    scan();
                }
            });
        }
    }

    private void scan() {
        Monitor[] monitors;
        synchronized (this) {
            monitors = monitorsById.values().toArray(new Monitor[monitorsById.size()]);
        }
        for (Monitor monitor : monitors) {
            monitor.scan();
        }
    }
}
