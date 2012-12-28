package org.yajul.util;

/**
 * A task that returns a result and may throw a throwable.
 *
 * @see java.util.concurrent.Callable
 * @param <V> the result type of method <tt>call</tt>
 */
public interface Callable<V> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Throwable if unable to compute a result
     */
    V call() throws Throwable;
}
