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

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Reflection utilities.
 * User: josh
 * Date: Nov 16, 2003
 * Time: 4:45:36 PM
 */
public class ReflectionUtil
{
    /**
     * Returns a map of (Integer->String) from the values of
     * any static integer constants in the class.
     * @param c The class to get the constants from.
     * @return Map - A map of the constant integer values to their names.
     */
    public static Map getConstantNameMap(Class c)
    {
        Field[] fields = c.getFields();
        Map map = new HashMap();
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            if (Modifier.isStatic(field.getModifiers()))
            {
                Object value = null;
                try
                {
                    value = field.get(null);
                    if (value instanceof Integer)
                    {
                        Integer integer = (Integer) value;
                        map.put(integer,field.getName());
                    }
                }
                catch (IllegalArgumentException ignore)
                {
                }
                catch (IllegalAccessException ignore)
                {
                }
            }
        } // for
        return map;
    }
}
