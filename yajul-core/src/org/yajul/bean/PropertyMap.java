// $Id$
package org.yajul.bean;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;
import java.util.AbstractMap;

/**
 * Provides a Map view of a JavaBean's properties.
 * @author josh Apr 4, 2004 10:48:27 AM
 */
public class PropertyMap extends AbstractMap implements Map
{
    /**
     * The meta-data for the bean's properties.
     */
    private BeanProperties properties;
    /**
     * The bean.
     */
    private Object bean;

    /**
     * Constructs a map view of the bean.
     * @param bean the bean to create a map view of.
     */
    public PropertyMap(Object bean)
    {
        this(new BeanProperties(bean.getClass()),bean);
    }

    /**
     * Constructs a map view of the bean, with the given BeanProperties meta-data.
     * @param beanProperties The property properties
     * @param bean the bean to create a map view of.
     */
    private PropertyMap(BeanProperties beanProperties, Object bean)
    {
        this.bean = bean;
        this.properties = beanProperties;
    }

    public void clear()
    {
        throw new UnsupportedOperationException("PropertyMap does not support the clear() method.");
    }

    public Object put(Object key, Object value)
    {
        String propertyName = (key instanceof String) ? (String) key : key.toString();
        return properties.setProperty(bean,propertyName,value);
    }

    public Set entrySet()
    {
        // Create a hash set of entries.
        Set set = new HashSet();
        Iterator iter = properties.accessorMethods();
        while (iter.hasNext())
        {
            PropertyAccessors accessorMethods = (PropertyAccessors) iter.next();
            Entry entry = new Entry(properties,bean,accessorMethods.getName());
            set.add(entry);
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     * Represents a property name / value pair in the Map view of a bean.
     */
    public static class Entry implements Map.Entry
    {
        private BeanProperties accessor;
        private Object bean;
        private String propertyName;

        /**
         * Creates a new name / value pair in the map view of a bean.
         * @param accessor The property properties (from the bean's class).
         * @param bean The bean
         * @param propertyName The name of the property.
         */
        public Entry(BeanProperties accessor, Object bean, String propertyName)
        {
            this.accessor = accessor;
            this.bean = bean;
            this.propertyName = propertyName;
        }

        public Object getKey()
        {
            return propertyName;
        }

        public Object getValue()
        {
            return accessor.getProperty(bean,propertyName);
        }

        public Object setValue(Object value)
        {
            return accessor.setProperty(bean,propertyName,value);
        }

        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Entry)) return false;

            final Entry entry = (Entry) o;

            if (!propertyName.equals(entry.propertyName)) return false;

            return true;
        }

        public int hashCode()
        {
            return propertyName.hashCode();
        }

        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            buf.append("[propertyName=");
            buf.append(propertyName);
            buf.append(", value=");
            buf.append(getValue());
            buf.append("]");
            return buf.toString();
        }
    }
}
