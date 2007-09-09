// $Id$
package org.yajul.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Format;
import java.text.ParseException;

/**
 * Encapsulates the getter and setter meta-data for a given property.
 */
public class PropertyAccessors {
    private static final Object[] NO_ARGS = new Object[0];

    private String name;
    private Class type;
    private Method getter;
    private Method setter;
    private BeanProperties beanProperties;

    /**
     * Creates a new group of accessor methods for the given property name.
     *
     * @param name The property name.
     */
    public PropertyAccessors(BeanProperties beanProperties, String name) {
        this.beanProperties = beanProperties;
        this.name = name;
    }

    /**
     * Returns the name of the property.
     *
     * @return the name of the property.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the property.
     *
     * @return the type of the property.
     */
    public Class getType() {
        return type;
    }

    /**
     * Returns the getter method.
     *
     * @return the getter method.
     */
    public Method getGetter() {
        return getter;
    }

    /**
     * Returns the setter method.
     *
     * @return the setter method.
     */
    public Method getSetter() {
        return setter;
    }

    void setType(Class type) {
        if (this.type != null && this.type != type)
            throw new IllegalArgumentException("Types " + this.type.getName() + " and " + type.getName() + " are not compatible!");
        this.type = type;
    }

    void setGetter(Method getter) {
        if (this.getter != null)
            throw new IllegalArgumentException("Getter already defined for property " + name + " in class " + beanProperties.getClassName() + "!");
        this.getter = getter;
    }

    void setSetter(Method setter) {
        if (this.setter != null)
            throw new IllegalArgumentException("Setter already defined for property " + name + " in class " + beanProperties.getClassName() + "!");
        this.setter = setter;
    }

    /**
     * Invokes the setter on the given bean using the given value.
     *
     * @param bean  The bean to set the property on.
     * @param value The new value of the property.
     * @return The new value of the property
     * @throws IllegalAccessException   if the setter cannot be accessed
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the setter cannot be accessed
     * @throws NoSuchMethodException    if the setter cannot be accessed
     * @throws java.text.ParseException if the value needed to be parsed, and could not be
     */
    public Object invokeSetter(Object bean, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
        return invokeSetter(bean, value, null);
    }

    /**
     * Invokes the setter on the given bean using the given value.
     *
     * @param bean   The bean to set the property on.
     * @param value  The new value of the property.
     * @param format The format that will be used to convert the value into the property type.
     * @return The new value of the property
     * @throws IllegalAccessException   if the setter cannot be accessed
     * @throws java.lang.reflect.InvocationTargetException
     *                                  if the setter cannot be accessed
     * @throws NoSuchMethodException    if the setter cannot be accessed
     * @throws java.text.ParseException if the value needed to be parsed, and could not be
     */
    public Object invokeSetter(Object bean, Object value, Format format) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
        if (setter == null)
            throw new NoSuchMethodException("No setter defined for property " + name + " in class " + beanProperties.getClassName() + "!");
        // If the value is a string and needs to be parsed, do so.
        if (value.getClass() != type && value instanceof String && format != null) {
            try {
                value = format.parseObject((String) value);
            }
            catch (ParseException e) {
                throw e;
            }
        }
        try {
            Object rv = setter.invoke(bean, new Object[]{value});
            if (rv != null)
                return rv;
            else
                return value;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e + " for property " + name + " in class " + beanProperties.getClassName());
        }
    }

    /**
     * Invokes the getter on the specified object, returning the property value.
     *
     * @param bean The bean to invoke the getter on.
     * @return The property value.
     * @throws IllegalAccessException if the getter cannot be accessed
     * @throws java.lang.reflect.InvocationTargetException
     *                                if the getter cannot be accessed
     * @throws NoSuchMethodException  if the getter cannot be accessed
     */
    public Object invokeGetter(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (getter == null)
            throw new NoSuchMethodException("No getter defined for property " + name + " in class " + beanProperties.getClassName() + "!");
        return getter.invoke(bean, NO_ARGS);
    }

    public String toString() {
        return FieldPrinter.toString(this);
    }


}
