package org.yajul.fix.util;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;

/**
 * Helper methods for java.util.concurrent classes.
 * <br>
 * User: josh
 * Date: Jul 10, 2009
 * Time: 5:43:15 PM
 */
public class LockHelper {
    public static void await(ReentrantLock lock, Condition condition, int time, TimeUnit timeUnit) throws InterruptedException {
        lock.lock();
        try {
            condition.await(time, timeUnit);
        } finally {
            lock.unlock();
        }
    }

    public static void signal(ReentrantLock lock, Condition condition) {

        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }

    }
}
