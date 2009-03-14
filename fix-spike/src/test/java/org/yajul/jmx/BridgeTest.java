package org.yajul.jmx;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Unit test for JmxBridge.
 * <br>User: Joshua Davis
 * Date: Aug 29, 2007
 * Time: 6:26:23 AM
 */
public class BridgeTest extends TestCase {
    public BridgeTest(String n) {
        super(n);
    }

    public void testCreate() throws Exception {
        String className = "org.yajul.jmx.ThisIsBogus";
        JmxBridge bridge = JmxBridge.getInstance();
        bridge.getProxy(className).start();
        ClassNotFoundException ex = null;
        try {
            bridge.initializeProxies();
        } catch (ClassNotFoundException e) {
            ex = e;
        }
        assertNotNull(ex);
        assertNull(bridge.getProxy(className).getImplementation());
        bridge.reset();
        // You would use a string name in real life to avoid dependencies, but we're testing here...
        Proxy p = bridge.getProxy(MyTestImpl.class.getName());
        p.start();
        bridge.initializeProxies();
        MyTestImpl impl = (MyTestImpl) p.getImplementation();
        assertTrue(impl.isStarted());
        p.stop();
        assertFalse(impl.isStarted());
        p.start();
        impl = (MyTestImpl) p.getImplementation();
        assertTrue(impl.isStarted());
        bridge.reset();
        assertFalse(impl.isStarted());
    }

    public static Test suite() {
        return new TestSuite(BridgeTest.class);
    }
}
