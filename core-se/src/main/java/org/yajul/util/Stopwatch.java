package org.yajul.util;

/**
 * A stop watch.  Good for timing things.
 * <br>User: Joshua Davis
 * Date: Nov 23, 2007
 * Time: 5:21:17 PM
 */
public class Stopwatch {
    private final Clock clock;
    private long elapsedTotal;
    private long startTime;
    private boolean running;

    /**
     * Creates a new stopwatch with the system default clock.
     */
    public Stopwatch() {
        this(DefaultClock.INSTANCE);
    }

    /**
     * Creates a new Stopwatch with the specified clock.
     *
     * @param clock the clock interface.
     */
    public Stopwatch(Clock clock) {
        if (clock == null) throw new IllegalArgumentException("clock cannot be null!");
        this.clock = clock;
        reset();
    }

    /**
     * Starts the stopwatch.
     *
     * @return the stopwatch object, in started state
     */
    public Stopwatch start() {
        if (!running) {
            running = true;
            startTime = clock.currentTimeMillis();
        }
        return this;
    }

    /**
     * Stops the stopwatch, time no longer accumulates.
     */
    public void stop() {
        if (running) {
            running = false;
            elapsedTotal += getElapsedSinceStart();
        }
    }

    /**
     * Stops and resets the elapsed time to zero.
     */
    public void reset() {
        running = false;
        elapsedTotal = 0;
    }

    /**
     * Returns the elapsed time, in milliseconds.
     *
     * @return the elapsed time.
     */
    public long getElapsed() {
        if (running)
            return elapsedTotal + getElapsedSinceStart();
        else
            return elapsedTotal;
    }

    private long getElapsedSinceStart() {
        return clock.currentTimeMillis() - startTime;
    }
}
