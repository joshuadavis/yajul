package org.yajul.micro;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.net.URL;

import org.picocontainer.ComponentAdapter;

/**
 * Test microcontainer behavior.
 * <br>User: Joshua Davis
 * Date: Mar 6, 2008
 * Time: 6:34:05 AM
 */
public class MicroContainerTest extends TestCase {
    public MicroContainerTest(String n) {
        super(n);
    }

    public void testCacheing()
    {
        MicroContainer mc = new MicroContainer();
        mc.addComponent(List.class, ArrayList.class);

        List one = mc.getComponent(List.class);
        List two = mc.getComponent(List.class);

        Assert.assertSame(one,two);
    }

    public void testAutoAdd()
    {
        MicroContainer mc = new MicroContainer();
        // MicroContainer will automatically add component implementations.
        List one = mc.getComponent(ArrayList.class);
        List two = mc.getComponent(ArrayList.class);
        Assert.assertNotNull(one);
        Assert.assertNotNull(two);
        Assert.assertSame(one,two);
    }

    public void testBootstrap() throws IOException {
        // MicroContainer can bootstrap itself from properties files.
        MicroContainer mc = new MicroContainer();
        mc.bootstrap("test-bootstrap.properties",Thread.currentThread().getContextClassLoader());
        System.out.println(mc);
        assertEquals(mc.getComponent("magicNumber"),42L);
        Assert.assertSame(mc.getComponentAdapter(Set.class).getComponentImplementation(), TreeSet.class);
        Assert.assertSame(mc.getComponentAdapter(Collection.class).getComponentImplementation(),HashSet.class);
        Assert.assertSame(mc.getComponentAdapter("testconfig").getComponentImplementation(),TestConfig.class);
        TestConfig t = (TestConfig) mc.getComponent("testconfig");
        Assert.assertFalse(t.isStarted());
        mc.start();
        Assert.assertTrue(t.isStarted());
    }

    public void testSingletonManager() {
        MicroContainer container = SingletonManager.getInstance().getDefaultContainer();
    }

    public static Test suite() {
        return new TestSuite(MicroContainerTest.class);
    }
}
