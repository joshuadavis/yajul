package org.yajul.framework.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.yajul.framework.BeanNotFoundException;
import org.yajul.framework.ServiceLocator;

/**
 * Tests the ServiceLocator class.
 * <br>
 * User: jdavis Date: Feb 25, 2004 Time: 10:37:08 AM
 *
 * @author jdavis
 */
public class ServiceLocatorTest extends TestCase
{
    private static Logger log = Logger.getLogger(ServiceLocatorTest.class);

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
        assertEquals(RESOURCE, instance.getResourceName());
        SimpleBean testBean = (SimpleBean) instance.getBean("testBean");
        assertNotNull(testBean);
        assertEquals("exampleProperty - value", testBean.getExampleProperty());
        assertTrue(instance.containsBean("testBean"));
        assertNotNull(instance.getBean("testBean",SimpleBean.class));
        // Make sure that the same bean is returned a second time.
        SimpleBean testBean2 = (SimpleBean) instance.requireBean("testBean");
        assertSame(testBean, testBean2);
        Exception ex = null;
        try
        {
            instance.requireBean("bogusBeanId");
        }
        catch (BeanNotFoundException bnfe)
        {
            ex = bnfe;
        }
        assertNotNull(ex);
        // Make sure that the same bean is returned after initializing with the same resource.
        instance.initialize(RESOURCE);
        SimpleBean testBean3 = (SimpleBean) instance.getBean("testBean");
        assertSame(testBean, testBean3);
        assertEquals("beanRefContext.xml",instance.getBootContext());
    }

    public void testSystemPropertyConfigurer() throws Exception
    {
        SimpleBean testBean = (SimpleBean) instance.getBean("testBean");
        assertNotNull(testBean);
        assertNotNull(testBean.getUserName());
        assertEquals(System.getProperty("user.name"), testBean.getUserName());
    }

    public void testInitialize() throws Exception
    {
        String bootContext = "test-beanRefContext.xml";
        ServiceLocator locator = ServiceLocator.getInstance(bootContext);
        assertNotSame(locator, instance);
        IllegalArgumentException e = null;
        try
        {
            locator.initialize(null,null);
        }
        catch (IllegalArgumentException iae)
        {
            e = iae;
        }
        assertNotNull(e);
        locator.initialize("unit-test-context2.xml","test-properties.properties");
        assertNotNull(locator.getBean("testBean"));
        assertTrue(locator.isSingleton("testBean"));
        String[] aliases = locator.getAliases("testBean");
        assertNotNull(aliases);
        assertEquals(0,aliases.length);
        locator.initialize("unit-test-context.xml","test-properties.properties");
        assertNotNull(locator.getBean("testBean"));
        Exception ex = null;
        try
        {
            locator.initialize("unit-test-context2.xml","bogus-properties.properties");
            assertNotNull(locator.getBean("testBean"));
        }
        catch (org.springframework.beans.factory.BeanInitializationException bie)
        {
            ex = bie;
        }
        ex = null;
        try
        {
            locator.initialize("bogus-context.xml","test-properties.properties");
            assertNotNull(locator.getBean("testBean"));
        }
        catch (Exception ee)
        {
            ex = ee;
        }
        assertNotNull(ex);
        ServiceLocator locator2 = ServiceLocator.getInstance(bootContext, "serviceLocator2");
        assertTrue(locator2.getMetaBeanFactory().containsBean("serviceLocator2"));
        assertNotSame(locator2, instance);
        assertNotSame(locator2, locator);
        assertEquals("serviceLocator2",locator2.getBeanName());
        locator2.release();
        locator2.release();
        locator.release();
    }

    public void testRelease() throws Exception
    {
        log.info("Finding test locator instance...");
        // Get a *different* service locator.
        String bootContext = "test-beanRefContext.xml";
        ServiceLocator locator = ServiceLocator.getInstance(bootContext);
        assertNotSame(locator, instance);
        locator.release();

        // Test strange case where locator is not initialized properly.
        locator = new ServiceLocator();
        locator.release();
    }

    public void testChildContext()
    {
        String bootContext = "test-beanRefContext.xml";
        ServiceLocator locator = ServiceLocator.getInstance(bootContext);
        BeanFactoryLocator bfl = ServiceLocator.getBeanFactoryLocator(bootContext);
        BeanFactoryReference bfr = bfl.useBeanFactory("testChild");
        ListableBeanFactory bf = (ListableBeanFactory) bfr.getFactory();
        String[] names = bf.getBeanDefinitionNames();
        for (int i = 0; i < names.length; i++)
        {
            log.info(names[i]);
        }
        assertNotNull(bf);
        log.info("testChild = " + bf);
        Object o = bf.getBean("testBean");
        log.info("testBean = " + o.toString());
        Object o2 = bf.getBean("testBean2");
        log.info("testBean2 = " + o2.toString());
        bfr.release();
        locator.release();
    }

/*
    private void showUrls(String resource)
            throws IOException
    {
        log.info("Resource: " + resource);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration enum = cl.getResources(resource);
        int i = 0;
        while (enum.hasMoreElements())
        {
            i++;
            URL url = (URL) enum.nextElement();
            log.info("URL " + i + ": " + url);
        }
        URL url = cl.getResource(resource);
        log.info("getResource URL: " + url);
        InputStream in = cl.getResourceAsStream(resource);
        log.info("asStream: " + ((in == null) ? "unavailable" : "available"));
    }
*/

    /**
     * Constructs a test suite for this test case, providing any required Setup wrappers, or decorators as well.
     *
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
