package org.yajul.micro;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.util.*;

import org.yajul.util.ReflectionUtil;
import org.yajul.jmx.JmxBridge;
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
        
        assertSame(one,two);
    }

    public void testAutoAdd()
    {
        MicroContainer mc = new MicroContainer();
        // MicroContainer will automatically add component implementations.
        List one = mc.getComponent(ArrayList.class);
        List two = mc.getComponent(ArrayList.class);
        assertNotNull(one);
        assertNotNull(two);
        assertSame(one,two);
    }

    public void testBootstrap() throws IOException {
        // MicroContainer can bootstrap itself from properties files.
        MicroContainer mc = new MicroContainer();
        mc.bootstrap("test-bootstrap.properties",Thread.currentThread().getContextClassLoader());
        System.out.println(mc);
        assertEquals(mc.getComponent("magicNumber"),42L);
        assertSame(mc.getComponentAdapter(Set.class).getComponentImplementation(), TreeSet.class);
        assertSame(mc.getComponentAdapter(Collection.class).getComponentImplementation(),HashSet.class);
        assertSame(mc.getComponentAdapter("testconfig").getComponentImplementation(),TestConfig.class);
        TestConfig t = (TestConfig) mc.getComponent("testconfig");
        assertFalse(t.isStarted());
        mc.start();
        assertTrue(t.isStarted());
    }

    public void testSingletonManager() {
        MicroContainer container = SingletonManager.getInstance().getDefaultContainer();
        ComponentAdapter<?> adapter = container.getComponentAdapter(JmxBridge.class);
        assertNotNull(adapter);
    }
    
    public static Test suite() {
        return new TestSuite(MicroContainerTest.class);
    }
}
