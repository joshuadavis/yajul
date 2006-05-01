// $Id$

package org.yajul.util.test;

import junit.framework.TestCase;

import org.yajul.util.StringArrayBuilder;

/**
 * Test StringArrayBuilder
 */
public class StringArrayBuilderTest extends TestCase
{
    /**
     * Standard JUnit test case constructor.
     * @param name The name of the test case.
     */
    public StringArrayBuilderTest(String name)
    {
        super(name);
    }

    /**
     * Test StringArrayBuilder
     */
    public void testStringArrayBuilder() throws Exception
    {
        StringArrayBuilder builder = new StringArrayBuilder(10);
        assertEquals(0,builder.getIndex());
        builder.append("one");
        builder.append(new String[] { "two", "three" });
        assertEquals(3,builder.getIndex());
        builder.append("four");
        builder.append("five");
        builder.append("six");
        builder.append("seven");
        builder.append("eight");
        builder.append("nine");
        builder.append("ten");
        String[] array = builder.toStringArray();
        assertEquals("one",array[0]);
        assertEquals("ten",array[9]);
    }
}
