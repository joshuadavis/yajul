package org.yajul.enum.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import org.yajul.enum.EnumTypeMap;
import org.yajul.enum.EnumType;
import org.yajul.enum.EnumValue;
import org.yajul.enum.EnumValueFilter;
import org.yajul.enum.EnumInitializationException;

/**
 * Test case for the EnumType, EnumTypeMap, and EnumValue objects.
 * User: jdavis
 * Date: Jul 3, 2003
 * Time: 2:41:18 PM
 * @author jdavis
 */
public class EnumTest extends TestCase
{
    private static final String ID_ENUMTYPE1 = "TestEnumType1";
    private static final String ID_ENUMTYPE2 = "TestEnumType2";
    public static final String XML_FILE1 = "test_data/EnumTest1.xml";

    public EnumTest(String s)
    {
        super(s);
    }

    public void testEnumTypeMap1() throws Exception
    {
        EnumTypeMap map = new EnumTypeMap();
        map.loadXML(new FileInputStream(XML_FILE1));

        EnumType enumType = map.findEnumTypeById(ID_ENUMTYPE1);
        assertNotNull(enumType);
        assertEquals(enumType.getId(),ID_ENUMTYPE1);
        EnumValue value = enumType.findValueById(0);
        assertNotNull(value);
        checkValue1(value);
        assertTrue(value instanceof Value1);

        EnumValue value2 = enumType.findValueByText("value one");
        checkValue1(value2);
        assertEquals(value,value2);

        // Make sure the iterator returns things in the right order.
        Iterator iter = enumType.iterator();
        value = (EnumValue)iter.next();
        assertNotNull(value);
        checkValue1(value);

        value = (EnumValue)iter.next();
        assertNotNull(value);
        assertEquals( 1, value.getId());
        assertEquals("two",value.getXmlValue());
        assertEquals("value two",value.getTextValue());

        value = (EnumValue)iter.next();
        assertNotNull(value);
        assertEquals( 2, value.getId());
        assertEquals("three",value.getXmlValue());
        assertEquals("value three",value.getTextValue());

        NoSuchElementException exception = null;
        try
        {
            value = (EnumValue)iter.next();
        }
        catch (NoSuchElementException e)
        {
            exception = e;
        }
        assertNotNull(exception);   // Make sure we got an exception.

        // Make sure the array accessors return the appropriate values.
        String[] textValues = enumType.getTextArray();
        assertEquals(3,textValues.length);
        assertEquals("value one",textValues[0]);
        assertEquals("value two",textValues[1]);
        assertEquals("value three",textValues[2]);
    }

    public void testEnumTypeMap2() throws Exception
    {
        EnumTypeMap map = new EnumTypeMap();
        map.loadXML(new FileInputStream("test_data/EnumTest2.xml"));

        EnumType enumType = map.findEnumTypeById(ID_ENUMTYPE2);
        assertNotNull(enumType);
        assertEquals(ID_ENUMTYPE2,enumType.getId());
        EnumValue value = enumType.findValueById(0);
        assertNull(value);

        // Make sure the iterator returns things in the right order.
        Iterator iter = enumType.iterator();

        value = (EnumValue)iter.next();
        assertNotNull(value);
        assertEquals( -1, value.getId());
        assertEquals("minusOne",value.getXmlValue());
        assertEquals("value minus one",value.getTextValue());

        value = (EnumValue)iter.next();
        assertNotNull(value);
        assertEquals( 7, value.getId());
        assertEquals("seven",value.getXmlValue());
        assertEquals("value seven",value.getTextValue());

        value = (EnumValue)iter.next();
        assertNotNull(value);
        assertEquals( 12, value.getId());
        assertEquals("twelve",value.getXmlValue());
        assertEquals("value twelve",value.getTextValue());

        NoSuchElementException exception = null;
        try
        {
            value = (EnumValue)iter.next();
        }
        catch (NoSuchElementException e)
        {
            exception = e;
        }
        assertNotNull(exception);   // Make sure e got an exception.
    }

    public void testSubset() throws Exception
    {
        EnumTypeMap map = new EnumTypeMap();
        map.loadXML(new FileInputStream(XML_FILE1));
        EnumType type = map.findEnumTypeById("TestEnumType2");
        EnumType subset = type.createSubset("TestSubset",
                new EnumValueFilter()
                {
                    public boolean test(EnumValue value)
                    {
                        return (value.getTextValue().startsWith("value"));
                    }
                });
        assertTrue(type.isContiguous());
        assertTrue(!subset.isContiguous());
        assertTrue(subset.isValid(0));
        assertTrue(subset.isValid(1));
        assertTrue(!subset.isValid(2));
        assertTrue(!subset.isValid(3));
    }

    public void testDefalutValue() throws Exception
    {
        EnumType type = getMap1Type2();
        assertEquals(1,type.getDefaultValueId());
        assertNotNull(type.getDefaultValue());
        assertEquals(1,type.getDefaultValue().getId());
    }

    private EnumType getMap1Type2() throws EnumInitializationException, FileNotFoundException
    {
        EnumTypeMap map = new EnumTypeMap();
        map.loadXML(new FileInputStream(XML_FILE1));
        EnumType type = map.findEnumTypeById("TestEnumType2");
        return type;
    }

    public void testConstantClass() throws Exception
    {
        EnumType type = getMap1Type2();
        assertEquals("ONE",type.findValueById(0).getConstantName());
    }

    private void checkValue1(EnumValue value)
    {
        assertEquals(0,value.getId());
        assertEquals("one",value.getXmlValue());
        assertEquals("value one",value.getTextValue());
    }

    /**
     * An enum value class for testing.
     */
    public static class Value1 extends EnumValue
    {
    }
}
