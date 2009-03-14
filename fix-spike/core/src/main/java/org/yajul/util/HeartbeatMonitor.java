package org.yajul.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Monitors services using heartbeats, notifies observers of any failure to send a heartbeat within
 * a given amount of time.
 * <br>User: Josh
 * Date: Mar 5, 2009
 * Time: 6:44:58 AM
 */
public class HeartbeatMonitor {
    private final static Logger log = LoggerFactory.getLogger(HeartbeatMonitor.class);

    /**
     * Used to name threads.
     */
    private final static AtomicInteger serialNumber = new AtomicInteger();

    /**
     * All the things being monitored, by id.
     */
    private Map<String, Observable> observablesById = new HashMap<String, Observable>();

    /**
     * The observers that will be notified when things happen.
     */
    private List<HeartbeatObserver> observers  = new ArrayList<HeartbeatObserver>();

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

    public HeartbeatMonitor(long scanInterval, ExecutorService executor) {
        if (executor == null)
            throw new IllegalArgumentException("ExecutorService cannot be null!");
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
            Observable observable = observablesById.get(id);
            if (observable != null)
                throw new IllegalArgumentException("Monitor for " + id + " already exists!");
            // If there is no timer thread, make one.
            if (timer == null) {
                timer = new Timer("HBMon-" + serialNumber.incrementAndGet(), true);
                ScannerTask task = new ScannerTask();
                timer.schedule(task, scanInterval, scanInterval);
            }
            observable = new Observable(id, suspectTimeout, failureTimeout);
            observablesById.put(id, observable);
        }
    }

    /**
     * Stop monitoring
     *
     * @param id the id of the monitor
     */
    public void removeMonitor(String id) {
        Observable observable;
        synchronized (this) {
            observable = observablesById.remove(id);
            // If there's nothing left to monitor, stop the timer thread.
            if (observablesById.isEmpty()) {
                timer.cancel();
                timer = null;
            }
        }
        if (observable != null)
            observable.cancel();
    }

    /**
     * Stop all monitoring, remove all observers.
     */
    public void clear() {
        synchronized (this) {
            for (Observable observable : observablesById.values()) {
                observable.cancel();
            }
            observablesById.clear();
            observers.clear();
        }
    }

    /**
     * A heartbeat from a particular service or object
     *
     * @param id the unique id for that object
     */
    public void heartbeat(String id) {
        Observable observable = getObservable(id);
        observable.heartbeat();
    }

    /**
     * Add an observer, the observer will be notified of changes in any of the monitors.
     *
     * @param observer the observer
     */
    public void addObserver(HeartbeatObserver observer) {
        synchronized (this) {
            observers.add(observer);
        }
    }

    /**
     * Removes all observers.
     */
    public void deleteObservers() {
        synchronized (this) {
            observers.clear();
        }
    }

    private void notifyObservers(String id, Status status, long lastHeartbeat) {
        HeartbeatObserver[] array;
        synchronized (this) {
            array = observers.toArray(new HeartbeatObserver[observers.size()]);
        }
        for (HeartbeatObserver observer : array) {
            notifyObserver(id, status, observer, lastHeartbeat);
        }
    }

    private void notifyObserver(String id, Status status, HeartbeatObserver observer, long lastHeartbeat) {
        try {
            observer.onEvent(id, status, lastHeartbeat);
        } catch (Exception e) {
            log.error("Unexpected Exception: " + e.getMessage(), e);
        }
    }

    private Observable getObservable(String id) {
        Observable observable;
        synchronized (this) {
            observable = observablesById.get(id);
        }
        if (observable == null) {
            throw new IllegalArgumentException("Monitor for " + id + " does not exist!");
        }
        return observable;
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
         * Called when something happens to a monitor
         *
         * @param id            the id of the monitor
         * @param status        what happened to the monitor
         * @param lastHeartbeat millis when the last heartbeat happened
         */
        void onEvent(String id, Status status, long lastHeartbeat);
    }

    private class Observable {
        // Immutable values
        private String id;
        private long suspectTimeout;
        private long failureTimeout;
        // Mutable values
        private Status status;
        private long lastHeartbeat;

        private Observable(String id, long suspectTimeout, long failureTimeout) {
            this.id = id;
            this.lastHeartbeat = System.currentTimeMillis();
            this.suspectTimeout = suspectTimeout;
            this.failureTimeout = failureTimeout;
            this.status = Status.CREATED;
            HeartbeatMonitor.this.notifyObservers(id, status, lastHeartbeat);
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
            HeartbeatMonitor.this.notifyObservers(id, status, last);
        }

        public void scan() {
            long now = System.currentTimeMillis();
            long last;
            synchronized (this) {
                // Copy mutable values in a sync block.
                last = lastHeartbeat;
            }
            long elapsed = now - last;
            if (elapsed > failureTimeout) {
                setStatus(Status.FAILED);
            }
            // Hasn't failed yet, maybe it's suspect.
            else if (suspectTimeout > 0 && elapsed > suspectTimeout && suspectTimeout < failureTimeout) {
                setStatus(Status.SUSPECTED);
            }
            // Neither failed nor suspect.  No change.
        }
    } // Observable

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
        Observable[] observables;
        synchronized (this) {
            observables = observablesById.values().toArray(new Observable[observablesById.size()]);
        }
        for (Observable observable : observables) {
            observable.scan();
        }
    }
}
