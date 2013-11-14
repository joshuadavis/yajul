package org.yajul.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test Stopwatch.
 * <br>
 * User: josh
 * Date: 9/28/12
 * Time: 4:02 PM
 */

public class StopwatchTest
{
    @Test
    public void testStopwatch() throws InterruptedException
    {
        MutableClock mutableClock = new MutableClock();
        mutableClock.setCurrentTimeMillis(DefaultClock.INSTANCE.currentTimeMillis());
        Stopwatch s = new Stopwatch(mutableClock);
        assertEquals(0,s.getElapsed());
        s.start();
        mutableClock.increment(100);
        assertEquals(100,s.getElapsed());
        s.stop();
        assertEquals(100,s.getElapsed());
        mutableClock.increment(10);
        assertEquals(100,s.getElapsed());
        s.start();
        mutableClock.increment(10);
        assertEquals(110,s.getElapsed());
        s.stop();
        assertEquals(110,s.getElapsed());
        s.reset();
        assertEquals(0,s.getElapsed());
    }
}
