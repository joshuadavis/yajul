package org.yajul.util;

import junit.framework.TestCase;

import java.util.Properties;

/**
 * Test case for PropetiesHelper
 * <br>
 * User: Josh
 * Date: Nov 14, 2009
 * Time: 7:42:40 AM
 */
public class PropertiesHelperTest extends TestCase {
   public void testAccessors() {
      Properties properties = new Properties();
      properties.put("should.be.true","true");
      assertTrue(PropertiesHelper.getBoolean(properties,"should.be.true"));
      assertTrue(PropertiesHelper.getBoolean(properties,"does.not.exist",true));
      assertFalse(PropertiesHelper.getBoolean(properties,"does.not.exist",false));
      properties.put("an.integer","1234");
      assertEquals(1234,PropertiesHelper.getInt(properties,"an.integer",-1));
      assertEquals(999,PropertiesHelper.getInt(properties,"does.not.exist",999));
      assertEquals(1234L,PropertiesHelper.getLong(properties,"an.integer",-1L));
      assertEquals(999L,PropertiesHelper.getLong(properties,"does.not.exist",999L));
      assertEquals(new Integer(1234),PropertiesHelper.getInteger(properties,"an.integer"));
      assertNull(PropertiesHelper.getInteger(properties,"does.not.exist"));
      properties.put("a.double","1234.56");
      assertEquals(1234.56,PropertiesHelper.getDouble(properties,"a.double",-1));
      assertEquals(9.99,PropertiesHelper.getDouble(properties,"does.not.exist",9.99));
   }

   public void testLoaders() {
      Properties props = PropertiesHelper.loadFromResource("test-properties.properties",null,this.getClass());
      assertNotNull(props.get("this"));
      assertNotNull(props.get("that"));
   }
}
