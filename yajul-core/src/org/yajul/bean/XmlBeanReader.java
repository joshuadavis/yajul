// $Id$
package org.yajul.bean;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.yajul.text.DefaultFormats;

import java.text.Format;
import java.text.ParseException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides utilities for setting bean properties from XML attributes and elements.
 * @author josh Apr 4, 2004 11:47:20 AM
 */
public class XmlBeanReader
{
    private BeanProperties accessor;
    private Map formatByPropertyName = new HashMap();
    private Map formatByValueClass = new HashMap();
    private DefaultFormats defaultFormats;

    /**
     * Constructs a new XmlBeanReader given the property meta-data.
     * @param accessor The property meta-data.
     */
    public XmlBeanReader(BeanProperties accessor)
    {
        this.accessor = accessor;
    }

    /**
     * Sets all properties in the bean from the attributes of the XML element, using the attribute names
     * as property names.
     * @param bean The bean
     * @param element The XML element
     * @throws NoSuchMethodException if a property could not be set
     * @throws IllegalAccessException if a property could not be set
     * @throws ParseException if a property could not be set
     * @throws InvocationTargetException if a property could not be set
     */
    public void setPropertiesFromElementAttributes(Object bean,Element element) throws NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException
    {
        setPropertiesFromElementAttributes(bean,element,null);
    }

    /**
     * Sets all properties in the bean from the attributes of the XML element, using the attribute names
     * as property names.
     * @param bean The bean
     * @param element The XML element
     * @param ignore A set of property names (Strings) to ignore.
     * @throws NoSuchMethodException if a property could not be set
     * @throws IllegalAccessException if a property could not be set
     * @throws ParseException if a property could not be set
     * @throws InvocationTargetException if a property could not be set
     */
    public void setPropertiesFromElementAttributes(Object bean,Element element,Set ignore) throws NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException
    {
        NamedNodeMap attributeMap = element.getAttributes();
        for (int i = 0; i < attributeMap.getLength() ; i++)
        {
            Attr attr = (Attr)attributeMap.item(i);
            if (ignore != null && ignore.contains(attr.getName()))
                continue;
            PropertyAccessors methods = accessor.getAccessorMethods(attr.getName());
            methods.invokeSetter(bean,attr.getValue(),getFormat(attr.getName(),methods.getType()));
        }
    }

    private Format getFormat(String name, Class type)
    {
        if (String.class.equals(type))
            return null;
        Format f = (Format) formatByPropertyName.get(name);
        if (f == null)
            f = (Format) formatByValueClass.get(type);
        if (f == null)
            f = getDefaultFormats().getFormat(type);
        return f;
    }

    private DefaultFormats getDefaultFormats()
    {
        if (defaultFormats == null)
            defaultFormats = new DefaultFormats();
        return defaultFormats;
    }

}
