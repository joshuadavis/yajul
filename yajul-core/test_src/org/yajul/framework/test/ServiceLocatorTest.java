package org.yajul.framework.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.yajul.framework.ServiceLocator;

/**
 * Tests the ServiceLocator class.
 * User: jdavis
 * Date: Feb 25, 2004
 * Time: 10:37:08 AM
 * @author jdavis
 */
public class ServiceLocatorTest extends TestCase
{
    public static final String RESOURCE = "unit-test-context.xml";

    private ServiceLocator instance;

    public ServiceLocatorTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        instance = ServiceLocator.getInstance();
        instance.initialize(RESOURCE);
    }

    protected void tearDown() throws Exception
    {
        instance.destroy();
        super.tearDown();
    }

    /**
     * Tests configuring and locating a simple bean.
     */
    public void testSimpleBean() throws Exception
    {
        assertEquals(RESOURCE,instance.getResourceName());
        SimpleBean testBean = (SimpleBean) instance.getBean("testBean");
        assertNotNull(testBean);
        assertEquals("exampleProperty - value",testBean.getExampleProperty());
        // Make sure that the same bean is returned a second time.
        SimpleBean testBean2 = (SimpleBean) instance.getBean("testBean");
        assertSame(testBean,testBean2);
        // Make sure that the same bean is returned after initializing with the same resource.
        instance.initialize(RESOURCE);
        SimpleBean testBean3 = (SimpleBean) instance.getBean("testBean");
        assertSame(testBean,testBean3);
    }


    public void testSystemPropertyConfigurer() throws Exception
    {
        SimpleBean testBean = (SimpleBean) instance.getBean("testBean");
        assertNotNull(testBean);
        assertNotNull(testBean.getUserName());
        assertEquals(System.getProperty("user.name"),testBean.getUserName());
    }

    /**
     * Constructs a test suite for this test case, providing any required
     * Setup wrappers, or decorators as well.
     * @return Test - The test suite.
     */
    public static Test suite()
    {
        // Return the default test suite: No setup, all public methods with
        // no return value, no parameters, and names that begin with 'test'
        // are added to the suite.
        return new TestSuite(ServiceLocatorTest.class);
    }
}
