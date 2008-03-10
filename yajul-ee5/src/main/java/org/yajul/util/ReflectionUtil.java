/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/
package org.yajul.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Reflection utilities.
 * User: josh
 * Date: Nov 16, 2003
 * Time: 4:45:36 PM
 */
public class ReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * Returns a map of (Integer->String) from the values of
     * any static integer constants in the class.
     *
     * @param c The class to get the constants from.
     * @return Map - A map of the constant integer values to their names.
     * @noinspection EmptyCatchBlock
     */
    public static Map<Integer, String> getConstantNameMap(Class c) {
        Field[] fields = c.getFields();
        Map<Integer, String> map = new HashMap<Integer, String>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (Modifier.isStatic(field.getModifiers())) {
                Object value;
                try {
                    value = field.get(null);
                    if (value instanceof Integer) {
                        Integer integer = (Integer) value;
                        map.put(integer, field.getName());
                    }
                }
                catch (IllegalArgumentException ignore) {
                }
                catch (IllegalAccessException ignore) {
                }
            }
        } // for
        return map;
    }

    /**
     * Returns true if the method is a property getter.
     *
     * @param method The method.
     * @return true if the method is a property getter.
     */
    public static boolean isPropertyGetter(Method method) {
        return getterPropertyName(method) != null;
    }

    /**
     * Returns the name of the property IFF the method is a property getter, or null if the
     * method is not a getter.
     *
     * @param method The method.
     * @return the name of the property IFF the method is a property getter, or null if the
     *         method is not a getter.
     */
    public static String getterPropertyName(Method method) {
        // It can't be a getter if it has a void return type.
        Class returnType = method.getReturnType();
        if (returnType.equals(Void.TYPE))
            return null;
        // It can't be a getter if it has parameters.
        if (method.getParameterTypes().length != 0)
            return null;
        // It can't be a getter if it's static.
        if (Modifier.isStatic(method.getModifiers()))
            return null;
        String name = method.getName();
        if (name.startsWith("get") && (!name.equals("getClass")))
            return methodNameAsPropertyName(name, 3);
        else if (name.startsWith("is") && (returnType.equals(Boolean.TYPE) || returnType.equals(Boolean.class)))
            return methodNameAsPropertyName(name, 2);
        else
            return null;
    }

    /**
     * Returns the name of the property IFF the method is a property setter, or null if the
     * method is not a setter.
     *
     * @param method The method.
     * @return the name of the property IFF the method is a property setter, or null if the
     *         method is not a setter.
     */
    public static String setterPropertyName(Method method) {
        // It can't be a setter if it has no parameters.
        if (method.getParameterTypes().length == 0)
            return null;
        // It can't be a setter if it's static.
        if (Modifier.isStatic(method.getModifiers()))
            return null;
        String name = method.getName();
        if (name.startsWith("set"))
            return methodNameAsPropertyName(name, 3);
        else
            return null;
    }

    private static String methodNameAsPropertyName(String name, int prefixLength) {
        return Character.toLowerCase(name.charAt(prefixLength)) + name.substring(prefixLength + 1);
    }

    public static ClassLoader getCurrentClassLoader() {

        // lets get a class loader. By using the Thread's class loader, we allow
        // for more flexability.
        ClassLoader classLoader;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (SecurityException se) {
            classLoader = ReflectionUtil.class.getClassLoader();
            if (log.isDebugEnabled())
                log.debug("Unable to use context class loader, using system ClassLoader " + classLoader);
        }

        return classLoader;
    }

    /**
     * Creates a new instance of the class using the specified loader.
     *
     * @param className The class to instatiate - if null a default will be used
     * @param loader    The class loader to use when obtaining the instance
     * @return An Object which is an instance of the class
     */
    public static Object createInstance(String className, ClassLoader loader) {
        try {
            Class c;

            if (loader == null)
                loader = getCurrentClassLoader();

            c = loader.loadClass(className);

            return c.newInstance();
        }
        catch (ClassNotFoundException x) {
            throw new InitializationError("Class " + className
                    + " not found - " + "please check your classpath", x);
        }
        catch (ExceptionInInitializerError eiie) {
            throw new InitializationError("Class " + className
                    + " failed to initialize due to: " + eiie.getMessage(), eiie);
        }
        catch (InstantiationException ie) {
            throw new InitializationError("Class " + className
                    + " could not be instantiated - "
                    + "is it abstract, an interface, an array, or does it not have an "
                    + "empty constructor?", ie);
        }
        catch (IllegalAccessException iae) {
            throw new InitializationError("Class " + className
                    + " could not be accessed - "
                    + "is it private or is the empty constructor private?",
                    iae);
        }
    }

    /**
     * Creates a new instance of the class, throwing an initialization error, if there
     * was a problem.
     *
     * @param className The class name
     * @return Object - The instance.
     */
    public static Object createInstance(String className) {
        return createInstance(className, getCurrentClassLoader());
    }

    /**
     * Type checked version of create instance using the default class loader.
     * @param className the class name
     * @param componentType the type of the instance (e.g. an interface)
     * @return an instance of the object cast to 'componentType'
     */
    public static <T> T createInstance(String className,Class<T> componentType) {
        return componentType.cast(createInstance(className));
    }

    /**
     * Returns the resource name for the specified class.
     *
     * @param c The class.
     * @return String - The resource name for the class.
     */
    public static String getClassResourceName(Class c) {
        String s = c.getName();
        return s.replace('.', '/') + ".class";
    }

    /**
     * Returns the URL where the specified class is located in the current classpath.
     *
     * @param c The class to look for.
     * @return URL - The URL where the class was found, using the current class loader.
     */
    public static URL findClassURL(Class c) {
        String resourceName = getClassResourceName(c);
        ClassLoader loader = getCurrentClassLoader();
        return loader.getResource(resourceName);
    }

    /**
     * Turns a class file name into a class name.
     * @param filename the file name
     * @return the class name.
     */
    public static String filenameToClassname(String filename)
    {
       return filename.substring( 0, filename.lastIndexOf(".class") )
             .replace('/', '.').replace('\\', '.');
    }
}
