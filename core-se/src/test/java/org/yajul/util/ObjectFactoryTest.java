// $Id$

package org.yajul.util;

import junit.framework.TestCase;

import java.net.URL;
import java.util.Properties;

/**
 * tests the basic object factory methods
 */
public class ObjectFactoryTest extends TestCase
{
    private static final String THING_CLASS_NAME = Thing.class.getName();

    /**
     * Standard JUnit test case constructor.
     *
     * @param name The name of the test case.
     */
    public ObjectFactoryTest(String name)
    {
        super(name);
    }

    public void testTestObjects()
    {
        Thing t = new Thing();
        t.setValue(128);
        t.getValue();
        new NoConstructor(129);
    }

    public void testObjectFactory() throws Exception
    {
        String s = ObjectFactory.getClasspathRoot(Thing.class);
        assertNotNull(s);

        Thing t = (Thing) ObjectFactory.createInstanceFromPropertiesResource("objectfactory-test.properties","thingproperty",
                null,true);
        assertEquals(42,t.getValue());

        t = (Thing) ObjectFactory.createInstanceFromPropertiesResource("bogus.properties","thingproperty",
                THING_CLASS_NAME,false);
        assertEquals(42,t.getValue());

        t = (Thing) ObjectFactory.createInstanceFromPropertiesResource("objectfactory-test.properties","bogusproperty",
                THING_CLASS_NAME,false);
        assertEquals(42,t.getValue());

        Properties properties = new Properties();
        properties.setProperty("classname",InitializeableThing.class.getName());
        InitializeableThing it = (InitializeableThing)ObjectFactory.createInstanceFromProperties(properties,"classname",
                null,true);
        assertTrue(it.isInitialized());

        InitializationException iex = null;
        try
        {
            ObjectFactory.createInstanceFromPropertiesResource("bogus.properties","thingproperty",null,true);
        }
        catch (InitializationException e)
        {
            iex = e;
        }
        TestCase.assertNotNull(iex);

        iex = null;
        try
        {
            ObjectFactory.createInstanceFromProperties(null,"thingproperty",null,true);
        }
        catch (InitializationException e)
        {
            iex = e;
        }
        TestCase.assertNotNull(iex);

        iex = null;
        try
        {
            ObjectFactory.createInstanceFromPropertiesResource("objectfactory-test.properties","bogusproperty",null,true);
        }
        catch (InitializationException e)
        {
            iex = e;
        }
        TestCase.assertNotNull(iex);

        InitializationError ie = null;
        try
        {
            ReflectionUtil.createInstance("org.yajul.util.test.ObjectFactoryTest$DoesNotExist");
        }
        catch (InitializationError e)
        {
            ie = e;
        }
        TestCase.assertNotNull(ie);

        ie = null;
        try
        {
            ReflectionUtil.createInstance("java.io.Serializable");
        }
        catch (InitializationError e)
        {
            ie = e;
        }
        TestCase.assertNotNull(ie);
    }

    public static class Thing
    {
        private int value = 42;

        public int getValue()
        {
            return value;
        }

        public void setValue(int value)
        {
            this.value = value;
        }
    }

    public static class InitializeableThing extends Thing implements Initializeable
    {
        private boolean initialized = false;

        public void initialize(Properties properties) throws InitializationException
        {
            initialized = true;
        }

        public boolean isInitialized()
        {
            return initialized;
        }

    }

    public static class NoConstructor extends Thing
    {
        @SuppressWarnings({"UnusedDeclaration"})
        private NoConstructor(int foo)
        {
        }
    }
}
