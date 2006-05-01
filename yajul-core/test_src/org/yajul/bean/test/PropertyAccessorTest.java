// $Id$
package org.yajul.bean.test;

import java.util.Map;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.yajul.bean.BeanProperties;
import org.yajul.bean.PropertyMap;
import org.yajul.bean.XmlBeanReader;
import org.yajul.io.StringInputStream;
import org.yajul.xml.DOMUtil;

/**
 * TODO: Add class javadoc
 * 
 * @author josh Apr 4, 2004 9:30:45 AM
 */
public class PropertyAccessorTest extends TestCase
{
    public PropertyAccessorTest(String name)
    {
        super(name);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testGettersAndSetters() throws Exception
    {
        BeanProperties accessor = new BeanProperties(TestBean.class);
        TestBean bean = new TestBean();
        assertEquals(3,accessor.values(bean).size());
        accessor.setProperty(bean,"intProp",new Integer(3));
        assertEquals(3,bean.getIntProp());
        assertEquals(new Integer(3),accessor.getProperty(bean,"intProp"));
        accessor.setProperty(bean,"stringProp","test string");
        assertEquals("test string",bean.getStringProp());
        assertEquals("test string",accessor.getProperty(bean,"stringProp"));
        accessor.setProperty(bean,"doubleProp",new Double(3.1415));
        assertEquals(3.1415,bean.getDoubleProp(),0.001);
        assertEquals(3.1415,accessor.getDoubleProperty(bean,"doubleProp"),0.001);
        TestBean copy = new TestBean();
        accessor.copy(bean,copy);
        assertEquals(bean,copy);
    }

    public void testPropertyMap() throws Exception
    {
        TestBean bean = new TestBean();
        bean.setIntProp(42);
        bean.setStringProp("the answer");
        Map map = new PropertyMap(bean);
        assertEquals(3,map.size());
        assertEquals("the answer",map.get("stringProp"));
        TestBean copy = new TestBean();
        Map map2 = new PropertyMap(copy);
        assertNull(map2.get("stringProp"));
        map2.putAll(map);
        assertEquals(3,map2.size());
        assertEquals("the answer",map2.get("stringProp"));
        assertEquals(bean,copy);
    }

    public void testXmlBeanReader() throws Exception
    {
        TestBean bean = new TestBean();

        Document doc = DOMUtil.parse(new StringInputStream("<bean intProp='33' stringProp='the string' doubleProp='3.14159'/>"));
        XmlBeanReader xmlBeanReader = new XmlBeanReader(new BeanProperties(bean.getClass()));
        xmlBeanReader.setPropertiesFromElementAttributes(bean,doc.getDocumentElement());
        assertEquals(33,bean.getIntProp());
        assertEquals("the string",bean.getStringProp());
        assertEquals(3.14159,bean.getDoubleProp(),0.001);
    }

    public static class TestBean
    {
        private int intProp;
        private String stringProp;
        private double doubleProp;

        public int getIntProp()
        {
            return intProp;
        }

        public void setIntProp(int intProp)
        {
            this.intProp = intProp;
        }

        public String getStringProp()
        {
            return stringProp;
        }

        public void setStringProp(String stringProp)
        {
            this.stringProp = stringProp;
        }

        public double getDoubleProp()
        {
            return doubleProp;
        }

        public void setDoubleProp(double doubleProp)
        {
            this.doubleProp = doubleProp;
        }

        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof TestBean)) return false;

            final TestBean testBean = (TestBean) o;

            if (doubleProp != testBean.doubleProp) return false;
            if (intProp != testBean.intProp) return false;
            if (stringProp != null ? !stringProp.equals(testBean.stringProp) : testBean.stringProp != null) return false;

            return true;
        }

        public int hashCode()
        {
            int result;
            long temp;
            result = intProp;
            result = 29 * result + (stringProp != null ? stringProp.hashCode() : 0);
            temp = doubleProp != +0.0d ? Double.doubleToLongBits(doubleProp) : 0l;
            result = 29 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
}
