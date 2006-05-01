package org.yajul.spi;

import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.yajul.spi.test.ExampleSPI;
import org.yajul.util.ObjectFactory;

/**
 * Tests finding the default implementation.
 * NOTE: Don't forget to include test_data/resources in the classpath!
 * User: jdavis
 * Date: Oct 31, 2003
 * Time: 2:03:32 PM
 * @author jdavis
 */
public class ServiceProviderFactoryTest extends TestCase
{
    private static final String EXAMPLE_DEFAULT_IMPL = "org.yajul.spi.test.ExampleSPIDefaultImpl";

    public ServiceProviderFactoryTest(String name)
    {
        super(name);
    }

    /**
     * Performs any set up that is required by the test,
     * such as initializing instance variables of the test
     * case class, etc. Invoked before every test method.
     */
    protected void setUp()
    {
    }

    /**
     * Cleans up any state that needs to be undone after
     * the test has completed.
     */
    protected void tearDown()
    {
    }

    /**
     * Test the basic functionality of ServiceProviderFactory
     */
    public void testFind() throws Exception
    {
        String key = ExampleSPI.class.getName();
        ServiceProviderFactory spf = ServiceProviderFactory.findServiceProviderFactory(
                key, null, null);
        assertNotNull(spf);
        assertEquals(EXAMPLE_DEFAULT_IMPL,spf.getImplementationClassName());
        assertEquals(ObjectFactory.getCurrentClassLoader(),spf.getClassLoader());
        assertEquals(key,spf.getKey());
        assertNotNull(spf.getDescriptorURL());
        assertTrue(spf.implementationClassIsSpecified());
        assertNotNull(spf.getServiceResourceName());
        assertNotNull(spf.getDescriptorLocation());
        Object provider = spf.createInstance();
        assertNotNull(provider);
        Object o = ServiceProviderFactory.findServiceProvider(key,null,null);
        assertNotNull(o);
        assertEquals(provider.getClass().getName(),o.getClass().getName());
        assertEquals(EXAMPLE_DEFAULT_IMPL,o.getClass().getName());
    }

    /**
     * Test the basic functionality of ServiceProviderFactory
     */
    public void testList() throws Exception
    {
        Iterator iter = new ServiceProviderFactoryIterator(ExampleSPI.class,null);
        int i = 0;
        Object o = null;
        while (iter.hasNext())
        {
            o = iter.next();
            i++;
        }
        assertNotNull("The default test implementation was not found, is {yajul}/test_data/resources in the classpath?",
                o);
        assertEquals(1,i);
        assertEquals(EXAMPLE_DEFAULT_IMPL,((ServiceProviderFactory)o).getImplementationClassName());
    }

    /**
     * List the implementations of the JAXP api.
     */
    public void testListJAXPImplementations()
    {
        Iterator iter = new ServiceProviderFactoryIterator(DocumentBuilderFactory.class,null);
        int i = 0;
        Object o = null;
        while (iter.hasNext())
        {
            o = iter.next();
            System.out.println(o);
            i++;
        }
        assertNotNull(o);
        assertEquals(true,i > 0);
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
        return new TestSuite(ServiceProviderFactoryTest.class);
    }
}
