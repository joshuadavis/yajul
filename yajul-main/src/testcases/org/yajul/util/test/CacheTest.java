/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
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

package org.yajul.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.yajul.junit.LogSupressingSetup;
import org.apache.log4j.Logger;
import org.yajul.util.Cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

/**
 * Tests the org.yajul.util.Cache class
 * @author Joshua Davis
 */
public class CacheTest extends TestCase
{
    private static Logger log = Logger.getLogger(CacheTest.class);

    static final int FACTOR = 5;
    static final int SET_SIZE = 100 * FACTOR;
    static final int CACHE_SIZE = 10 * FACTOR;
    static final int ITERATIONS = 1000 * FACTOR;
    static final int THREAD_COUNT = 8;
    static final long SEED = 31;

    class TestElement
    {
        int id;
        String name;
        volatile boolean active;

        TestElement(int id, String name)
        {
            this.id = id;
            this.name = name;
            this.active = false;
        }
    }

    class TestActivator implements Cache.Activator
    {
        private Map map;
        private int activations;
        private int passivations;
        private int finalized;
        private int stale;

        public TestActivator()
        {
            map = new HashMap();
            TestElement elem;

            for (int i = 0; i < SET_SIZE; i++)
            {
                elem = new TestElement(i, "TestElement #" + i);
                map.put(new Integer(i), elem);
            }
        }


        /**
         * Activates the object associated with the specified information (for example, read the object
         * from persistent storage).
         * @param key - The key object that has enough information for the activator to create a new object.
         * @return The newly activated object.
         */
        public Object activate(Object key) throws Exception
        {
            // log.debug("activate() : " + key.toString());
            TestElement elem = (TestElement) map.get(key);
            if (elem.active)
            {
                // log.error("Element is already active! " + elem.id);
            }

            elem.active = true;
            activations++;
            return elem;
        }

        /**
         * Passivates the object, indicating that it is no longer in the cache.  The object may want to
         * release resources at this point.
         * @param key - The key object that has enough information for the activator to create a new object.
         * @param obj - The object that is being removed from the cache.
         * @param reason - The reason the object is being passivated (PASSIVATE_xxx values).
         */
        public void passivate(Object key, Object obj, int reason) throws Exception
        {
            TestElement elem = (TestElement) obj;
//            log.debug("passivating " + key + " reason = " + reason);
            if (!elem.active)
            {
                throw new Exception("Element is already passive! " + elem.id);
            }

            elem.active = false;
            switch (reason)
            {
                case PASSIVATE_FINALIZED:
                    finalized++;
                    break;
                case PASSIVATE_TIMEOUT:
                    stale++;
                    break;
                default:
                    passivations++;
                    break;
            } // switch
        }

        public int getActivations()
        {
            return activations;
        }

        public int getPassivations()
        {
            return passivations;
        }

        public int getFinalizations()
        {
            return finalized;
        }

        public int getStale()
        {
            return stale;
        }

        public Iterator iterator()
        {
            return map.values().iterator();
        }

        public TestElement get(Object key)
        {
            return (TestElement)map.get(key);
        }

        public void clearStatistics()
        {
            activations = 0;
            passivations = 0;
            finalized = 0;
            stale = 0;
            Iterator iter = map.values().iterator();
            TestElement e = null;
            while (iter.hasNext())
            {
                e = (TestElement) iter.next();
                e.active = false;
            }
        }
    }

    class CacheReader implements Runnable
    {
        private Cache cache;
        private int iterations;

        public CacheReader(Cache cache, int iterations)
        {
            this.cache = cache;
            this.iterations = iterations;
        }

        public void run()
        {
            readElements(cache, iterations, new Random(SEED));
        }
    }


    public CacheTest(String name)
    {
        super(name);
    }

    private void readElements(Cache cache, int iterations, Random random)
    {
        try
        {
            TestElement elem;
            Integer num;

            int cacheSize = cache.getMaxSize();

            for (int i = 0; i < iterations; i++)
            {
                num = new Integer(random.nextInt(SET_SIZE));
                // unique.add(num);
                elem = (TestElement) cache.get(num);
                assertEquals(elem.id, num.intValue());
                assertSame(elem, ((TestActivator)cache.getActivator()).get(num));
            }
            int active = 0;
            int passive = 0;
            Iterator iter = ((TestActivator)cache.getActivator()).iterator();
            while (iter.hasNext())
            {
                elem = (TestElement) iter.next();
                if (elem.active)
                    active++;
                else
                    passive++;
            }
//            log.info("-- readElements() --");
//            log.info("iterations   = " + iterations);
//            log.info("unique keys  = " + unique.size());
//            log.info("cacheSize    = " + cacheSize);
//            log.info("active       = " + active);
//            log.info("passive      = " + passive);
//            log.info("total        = " + (active + passive));
//            log.info("SET_SIZE     = " + SET_SIZE);

            // assertEquals((float)cacheSize,(float)active,2.0);
            if (active > cacheSize)
                log.error("active > cacheSize! active = " + active +
                        " cacheSize = " + cache.getMaxSize() +
                        " SET_SIZE = " + SET_SIZE +
                        " parallelActivation=" + cache.allowsParallelActivation());

            assertTrue(active <= cacheSize);
            assertEquals(active + passive, SET_SIZE);
            if (cacheSize < SET_SIZE)
                assertEquals((float) cacheSize / (float) SET_SIZE, cache.getHitRate(), 0.09);
//            printStatistics(cache);
            ((TestActivator)cache.getActivator()).clearStatistics();
        }
        catch (Exception e)
        {
            log.error("Unexpected: " + e.getMessage(),e);
            fail("Unexpected exception: " + e.getMessage());
        }
        log.info("readElements() : LEAVE");
    }

    private void readElements(int cacheSize) throws Exception
    {
        readElements(cacheSize, true);
        readElements(cacheSize, false);
    }

    private void readElements(int cacheSize, boolean parallelActivation)
        throws Exception
    {
        Cache cache = new Cache(new TestActivator(), cacheSize,
                0,                  // No timeout.
                parallelActivation, // Allow parallel activation.
                false,              // Don't keep statistics.
                new HashMap(cacheSize));
        readElements(cache, ITERATIONS, new Random(SEED));
        assertEquals(0, cache.getTimeouts());
        assertEquals(1.0, cache.getTimeoutRate(), 0.01);
        assertEquals(ITERATIONS, cache.getRequests());
        cache.clear();  // Make sure that the activator doesn't get messed up.
        ((TestActivator)cache.getActivator()).clearStatistics();
    }

    public void testSequence() throws Exception
    {

        int cacheSize = 10;

        readSequence(cacheSize, false);
        readSequence(cacheSize, true);
    }

    private void readSequence(int cacheSize, boolean parallelActivation) throws Exception
    {
        log.debug("readSequence() : ENTER");
        // Create the cache.
        Cache cache = new Cache(new TestActivator(), cacheSize,
                0,                  // No timeout.
                parallelActivation, // Allow parallel activation.
                true,               // Keep statistics.
                new HashMap(cacheSize));

        // Read the first 'cacheSize' elements, make sure that there are no
        // passivations.
        TestActivator activator = ((TestActivator)cache.getActivator());
        activator.clearStatistics();
        Iterator iter = activator.iterator();
        TestElement e = null;
        for (int i = 0; i < cacheSize; i++)
        {
            e = (TestElement)iter.next();
            cache.get(new Integer(e.id));
            assertEquals(0,activator.getPassivations());
            assertEquals(i+1,activator.getActivations());
        }

        // Get the next 'cache size' elements, there should be one passivation.

        for (int i = 0; i < cacheSize; i++)
        {
            e = (TestElement)iter.next();
            cache.get(new Integer(e.id));
            assertEquals(1 + i,activator.getPassivations());
            assertEquals(cacheSize + (i + 1), activator.getActivations());
        }
        // Make sure that the finalizer doesn't mess up any of the statistics
        // for the activator.   This call ensures that *all* elements are
        // passivated.
        cache.clear();
        log.debug("readSequence() : LEAVE");
    }

    public void testSmall() throws Exception
    {
        readElements(SET_SIZE / 20);
    }

    public void testMedium()  throws Exception
    {
        readElements(SET_SIZE / 2);
    }

    public void testLarge() throws Exception
    {
        readElements(SET_SIZE);
    }

    public void testMultiThreaded()
    {
        int cacheSize = SET_SIZE;
        doMultiThreadedTest(cacheSize, false);
        doMultiThreadedTest(cacheSize, true);
    }

    private void doMultiThreadedTest(int cacheSize, boolean parallelActivation)
    {
        Cache cache = new Cache(new TestActivator(), cacheSize,
                0, // No timeout.
                parallelActivation, // Allow parallel activation.
                false, // Don't keep statistics.
                new HashMap(cacheSize));

        Thread[] thread = new Thread[THREAD_COUNT];

        for (int i = 0; i < thread.length; i++)
        {
            thread[i] = new Thread(new CacheReader(cache, ITERATIONS / THREAD_COUNT));
            thread[i].start();
        }

        for (int i = 0; i < thread.length; i++)
        {
            try
            {
                thread[i].join();
            }
            catch (InterruptedException e)
            {
                // Ignore.
            }
        }

        assertEquals(cache.getTimeouts(), 0);
        assertEquals(cache.getTimeoutRate(), 1.0, 0.01);
    }

    private void printStatistics(Cache cache)
    {
        log.debug("--- cache ---");
        log.debug("size         = " + cache.getCurrentSize());
        log.debug("requests     = " + cache.getRequests());
        log.debug("activations  = " + cache.getActivations());
        log.debug("time outs    = " + cache.getTimeouts());
        log.debug("hit rate     = " + cache.getHitRate());
        log.debug("timeout rate = " + cache.getTimeoutRate());
        log.debug("--- activator ---");
        TestActivator activator = ((TestActivator)cache.getActivator());
        log.debug("activations  = " + activator.getActivations());
        log.debug("passivations = " + activator.getPassivations());
        log.debug("finalizations= " + activator.getFinalizations());
        log.debug("stale        = " + activator.getStale());
    }

    public void testTimeout()
    {
        log.info("testTimeout() : ENTER");
        try
        {
            // Set up a cache that has a timeout of 10ms
            Cache cache = new Cache(new TestActivator(), SET_SIZE, 10, false, false, new HashMap());
            // Poll the cache for 110ms, and see that one activation has occurred.
            TestElement elem;
            Integer num;
            long start = System.currentTimeMillis();
            long duration = 1000;
            Random random = new Random(SEED);

            for (int i = 0; System.currentTimeMillis() - start < duration; i++)
            {
                num = new Integer(random.nextInt(SET_SIZE));
                elem = (TestElement) cache.get(num);
                assertEquals(elem.id, num.intValue());
                assertSame(elem, ((TestActivator)cache.getActivator()).get(num));
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException e)
                {
                }
            }
//            printStatistics(cache);
            assertTrue(cache.getTimeoutRate() < 0.99);
            assertTrue(cache.getHitRate() < 0.99);
        }
        catch (Exception ex)
        {
            log.error("Unexpected: " + ex.getMessage(),ex);
            fail("Unexpected exception: " + ex.getMessage());
        }
        log.info("testTimeout() : LEAVE");
    }

    public void xtestWeakMap()
    {
        log.info("testWeakMap() : ENTER");
        try
        {
            // Set up a cache that has a timeout of 10ms
            Cache cache = new Cache(new TestActivator(), SET_SIZE, 0, false, false, new WeakHashMap());
            // Poll the cache for 110ms, and see that one activation has occurred.
            TestElement elem;
            Integer num;
            long start = System.currentTimeMillis();
            long duration = 1000;
            Random random = new Random(SEED);
            TestActivator activator = ((TestActivator)cache.getActivator());
            for (int i = 0; System.currentTimeMillis() - start < duration; i++)
            {
                num = new Integer(random.nextInt(SET_SIZE));
                elem = (TestElement) cache.get(num);
                assertEquals(elem.id, num.intValue());
                assertSame(elem, activator.get(num));
            }
            log.debug("Before GC...");
            printStatistics(cache);
            Runtime.getRuntime().runFinalization();
            System.gc();
            try
            {
                Thread.yield();
                Thread.sleep(200);
            }
            catch (InterruptedException e)
            {
            }
            log.debug("After GC...");
            printStatistics(cache);
            activator.clearStatistics();
        }
        catch (Exception ex)
        {
            log.error("Unexpected: " + ex.getMessage(),ex);
            fail("Unexpected exception: " + ex.getMessage());
        }
        log.info("testWeakMap() : LEAVE");
    }

    public static Test suite()
    {
        return new LogSupressingSetup(new TestSuite(CacheTest.class));
    }
}