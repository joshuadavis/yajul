package org.yajul.enum.test;

import java.io.FileInputStream;

import junit.framework.TestCase;
import org.yajul.enum.EnumTypeMap;
import org.yajul.enum.EnumType;
import org.yajul.enum.EnumValue;

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

    public EnumTest(String s)
    {
        super(s);
    }

    public void testEnumTypeMap() throws Exception
    {
        EnumTypeMap map = new EnumTypeMap();
        map.loadXML(new FileInputStream("test_data/EnumTest1.xml"));

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
    }

    private void checkValue1(EnumValue value)
    {
        assertEquals(value.getId(), 0);
        assertEquals(value.getXmlValue(),"one");
        assertEquals(value.getTextValue(),"value one");
    }

    /**
     * An enum value class for testing.
     */
    public static class Value1 extends EnumValue
    {
    }
}
