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
import java.util.Map;
import java.util.Random;

public class PoolTest extends TestCase
{
    private static Logger log = Logger.getLogger(PoolTest.class);

    static final int POOL_SIZE = 100;
    static final int ITERATIONS = 10000;

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

    class TestFactory implements Pool.Factory
    {
        private int activations;
        private int freeCount;
        private int recycleCount;

        public TestFactory()
        {
        }

        public Object create() throws Exception
        {
            // log.debug("activate() : " + key.toString());
            TestElement elem = new TestElement(activations, "TestObject" + activations);
            activations++;
            return elem;
        }

        public void recycle(Object o) throws Exception
        {
            recycleCount++;
        }

        public void free(Object o) throws Exception
        {
            freeCount++;
        }

        public int getActivations()
        {
            return activations;
        }

        public void clearStatistics()
        {
            activations = 0;
            freeCount = 0;
            recycleCount = 0;
        }

        public int getFreeCount()
        {
            return freeCount;
        }

        public int getRecycleCount()
        {
            return recycleCount;
        }
    }

    private Random random = new Random(System.currentTimeMillis());
    private Map map = new HashMap();
    private TestFactory factory;

    public PoolTest(String name)
    {
        super(name);
    }

    protected void setUp()
    {
        log.info("setUp() : ENTER");
        factory = new TestFactory();
        log.info("setUp() : LEAVE");
    }

    private void printStatistics(Pool pool)
    {
        log.info("--- pool ---");
        log.info("allocate calls    = " + pool.getAllocateCount());
        log.info("free calls        = " + pool.getFreeCount());
        log.info("create calls      = " + pool.getCreateCount());
        log.info("free objects      = " + pool.getFree());
        log.info("reserved objects  = " + pool.getReserved());
        /**
         log.info("time outs    = " + pool.getTimeouts());
         log.info("hit rate     = " + pool.getHitRate());
         log.info("timeout rate = " + pool.getTimeoutRate());
         **/
        log.info("--- factory ---");
        log.info("activations   = " + factory.getActivations());
        log.info("free calls    = " + factory.getFreeCount());
        log.info("recycle calls = " + factory.getRecycleCount());
        factory.clearStatistics();
    }

    public void allocate()
    {
        try
        {
            Pool pool = new Pool(factory, POOL_SIZE);
            Object x = pool.allocate();
            assertEquals(1, pool.getReserved());
            Object y = pool.allocate();
            assertEquals(2, pool.getReserved());
            pool.free(y);
            assertEquals(1, pool.getFree());
            assertEquals(1, pool.getReserved());
            pool.free(x);
            assertEquals(2, pool.getFree());
            assertEquals(0, pool.getReserved());
            Object z = pool.allocate();
            assertEquals(1, pool.getFree());
            assertEquals(1, pool.getReserved());
            printStatistics(pool);
        }
        catch (Exception e)
        {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new PoolTest("allocate"));
        return new LogSupressingSetup(suite);
    }

}