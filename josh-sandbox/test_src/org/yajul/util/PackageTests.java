/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 13, 2002
 * Time: 5:02:45 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.util;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.yajul.junit.LogSupressingSetup;

/**
 * The master test suite.
 */
public class PackageTests extends TestCase
{
    public PackageTests(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(CacheTest.class);
        suite.addTestSuite(DateFormatterTest.class);
        suite.addTestSuite(PoolTest.class);
        suite.addTestSuite(SimpleQueueTest.class);
        suite.addTestSuite(SimpleTopicTest.class);
        suite.addTestSuite(ThreadPoolTest.class);
        return new LogSupressingSetup(suite);
    }
}