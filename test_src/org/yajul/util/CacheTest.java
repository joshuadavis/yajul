/*********************************************************************************
 * $Header$
 * Copyright 2002 pgmjsd, inc.
 **********************************************************************************/

package org.yajul.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import org.yajul.log.Logger;
import org.yajul.junit.LogSupressingSetup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Tests the org.yajul.util.Cache class
 * @author Joshua Davis
 */
public class CacheTest extends TestCase
{
    private static Logger log = Logger.getLogger(CacheTest.class);

    static final int SET_SIZE = 100;
    static final int CACHE_SIZE = 10;
    static final int ITERATIONS = 1000;

    class TestElement
    {
        int id;
        String name;
        boolean active;

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

        public TestActivator(Map map)
        {
            this.map = map;
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

        public void clearStatistics()
        {
            activations = 0;
            passivations = 0;
            finalized = 0;
            stale = 0;
        }
    }

    private Random random = new Random(System.currentTimeMillis());
    private Map map = new HashMap();
    private TestActivator activator;

    public CacheTest(String name)
    {
        super(name);
    }

    protected void setUp()
    {
        log.info("setUp() : ENTER");
        TestElement elem;
        for (int i = 0; i < SET_SIZE; i++)
        {
            elem = new TestElement(i, "TestElement #" + i);
            map.put(new Integer(i), elem);
        }
        log.info("setUp() : LEAVE");
        activator = new TestActivator(map);
    }

    public void readElements(Cache cache, int iterations)
    {
        log.info("readElements() : ENTER");
        int cacheSize = cache.getMaxSize();

        try
        {
            TestElement elem;
            Integer num;
            // HashSet unique = new HashSet();
            for (int i = 0; i < iterations; i++)
            {
                num = new Integer(random.nextInt(SET_SIZE));
                // unique.add(num);
                elem = (TestElement) cache.get(num);
                assertEquals(elem.id, num.intValue());
                assertSame(elem, map.get(num));
            }
            int active = 0;
            int passive = 0;
            Iterator iter = map.values().iterator();
            while (iter.hasNext())
            {
                elem = (TestElement) iter.next();
                if (elem.active)
                    active++;
                else
                    passive++;
            }
            log.info("-- readElements() --");
            log.info("iterations   = " + iterations);
            // log.info("unique keys  = " + unique.size());
            log.info("cacheSize    = " + cacheSize);
            log.info("active       = " + active);
            log.info("passive      = " + passive);
            log.info("total        = " + (active + passive));
            log.info("SET_SIZE     = " + SET_SIZE);

            // assertEquals((float)cacheSize,(float)active,2.0);
            assertTrue(active <= cacheSize);
            assertEquals(active + passive, SET_SIZE);
            assertEquals(cache.getRequests(), iterations);
            if (cacheSize < SET_SIZE)
                assertEquals((float) cacheSize / (float) SET_SIZE, cache.getHitRate(), 0.09);
            printStatistics(cache);
        }
        catch (Exception e)
        {
            log.unexpected(e);
            fail("Unexpected exception: " + e.getMessage());
        }
        log.info("readElements() : LEAVE");
    }

    private void readElements(int cacheSize)
    {
        Cache cache = new Cache(activator, cacheSize);
        readElements(cache, ITERATIONS);
        assertEquals(cache.getTimeouts(), 0);
        assertEquals(cache.getTimeoutRate(), 1.0, 0.01);
    }

    public void testSmall()
    {
        readElements(SET_SIZE / 10);
    }

    public void testMedium()
    {
        readElements(SET_SIZE / 2);
    }

    public void testLarge()
    {
        readElements(SET_SIZE);
    }

    private void printStatistics(Cache cache)
    {
        log.info("--- cache ---");
        log.info("requests     = " + cache.getRequests());
        log.info("activations  = " + cache.getActivations());
        log.info("time outs    = " + cache.getTimeouts());
        log.info("hit rate     = " + cache.getHitRate());
        log.info("timeout rate = " + cache.getTimeoutRate());
        log.info("--- activator ---");
        log.info("activations  = " + activator.getActivations());
        log.info("passivations = " + activator.getPassivations());
        log.info("finalizations= " + activator.getFinalizations());
        log.info("stale        = " + activator.getStale());
        activator.clearStatistics();
    }

    public void testTimeout()
    {
        log.info("testTimeout() : ENTER");
        try
        {
            // Set up a cache that has a timeout of 10ms
            Cache cache = new Cache(activator, SET_SIZE, 10, false, false, new HashMap());
            // Poll the cache for 110ms, and see that one activation has occurred.
            TestElement elem;
            Integer num;
            long start = System.currentTimeMillis();
            long duration = 1000;
            for (int i = 0; System.currentTimeMillis() - start < duration; i++)
            {
                num = new Integer(random.nextInt(SET_SIZE));
                elem = (TestElement) cache.get(num);
                assertEquals(elem.id, num.intValue());
                assertSame(elem, map.get(num));
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException e)
                {
                }
            }
            printStatistics(cache);
            assertTrue(cache.getTimeoutRate() < 0.99);
            assertTrue(cache.getHitRate() < 0.99);
        }
        catch (Exception ex)
        {
            log.unexpected(ex);
            fail("Unexpected exception: " + ex.getMessage());
        }
        log.info("testTimeout() : LEAVE");
    }
}