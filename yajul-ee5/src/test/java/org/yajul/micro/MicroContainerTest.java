package org.yajul.micro;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import java.util.List;
import java.util.ArrayList;

import org.yajul.util.ReflectionUtil;

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

    public void testBootstrap()
    {
        // MicroContainer can bootstrap itself from a class.
    }
    public static Test suite() {
        return new TestSuite(MicroContainerTest.class);
    }
}
