/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
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

package org.yajul.enum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.yajul.log.LogUtil;
import org.yajul.util.ArrayIterator;
import org.yajul.xml.DOMUtil;

public class EnumType
{
    /**
     * The logger for this class.
     */
    private static Logger log = LogUtil.getLogger(EnumType.class.getName());

    /** The enum value id returned when there is an error. **/
    public static final int UNDEFINED = Integer.MIN_VALUE;

    /** The class of the values for this enumerated type. **/
    private Class enumValueClass;
    /** The id of this enumerated type. **/
    private String id;
    /** The minimum value id. **/
    private int minId;
    /** The maximum value id. **/
    private int maxId;

    /**
     * An array of all values, where the index is the id.
     */
    private EnumValue[] valueArray;

    /** Map of values by XML string. **/
    private Map valueByXML;

    /** Map of values by text string. **/
    private Map valueByText;

    protected EnumType()
    {
        valueByXML = new HashMap();
        valueByText = new HashMap();
    }

    public String getId()
    {
        return id;
    }

    public Class getEnumValueClass()
    {
        return enumValueClass;
    }

    public EnumValue findValueById(int id)
    {
        return valueArray[id];
    }

    public EnumValue findValueByXml(String xml)
    {
        return (EnumValue) valueByXML.get(xml);
    }

    public EnumValue findValueByText(String text)
    {
        return (EnumValue) valueByText.get(text);
    }

    public int xmlToValueId(String xml)
    {
        EnumValue value = findValueByXml(xml);
        return (value == null) ? UNDEFINED : value.getId();
    }

    public Iterator iterator()
    {
        return new ValueIterator();
    }

    void setEnumValueClass(java.lang.Class enumValueClass)
    {
        this.enumValueClass = enumValueClass;
    }

    void setId(String id)
    {
        this.id = id;
    }

    void loadFromElement(Element elem) throws EnumInitializationException
    {
        id = elem.getAttribute("id");
        try
        {
            enumValueClass = Class.forName(elem.getAttribute("valueClass"));
        }
        catch (ClassNotFoundException e)
        {
            throw new EnumInitializationException("Unable to find value class! " + e.getMessage(),e);
        }

        // Look for the 'enum-value' elements inside this 'enum-type' element.
        Element[] valueElements = DOMUtil.getChildElements(elem, "enum-value");
        // Create an array-list if values to ultimately create the 'values by id' array.
        ArrayList valuesById = new ArrayList(valueElements.length);

        // Set the minimum and maximum index values to their limits, so the loop can set them as the
        // elements are parsed.
        maxId = Integer.MIN_VALUE;
        minId = Integer.MAX_VALUE;
        int id = 0;
        // Parse each element, adding the parsed elements to the index maps and updating the minimum and maximum
        // id fields.
        for (int i = 0; i < valueElements.length; i++)
        {
            Element valueElement = valueElements[i];
            EnumValue value = null;
            try
            {
                value = (EnumValue) enumValueClass.newInstance();
            }
            catch (InstantiationException e)
            {
                throw new EnumInitializationException("Unable to instantiate value class due to: " + e.getMessage(),e);
            }
            catch (IllegalAccessException e)
            {
                throw new EnumInitializationException("Unable to instantiate value class due to: " + e.getMessage(),e);
            }
            value.loadFromElement(this, valueElement);
            id = value.getId();
            if (id < 0)
                throw new EnumInitializationException("Illegal enum id: " + id);

            if (id < valuesById.size() && valuesById.get(id) != null)
                log.warn("Value '" + id + " already defined in EnumType " + getId());

            valuesById.add(id, value);
            if (id > maxId)
                maxId = id;
            if (id < minId)
                minId = id;
            // Set up the index maps (by xml value and by text value).
            valueByXML.put(value.getXmlValue(), value);
            valueByText.put(value.getTextValue(), value);
        }

        valueArray = (EnumValue[]) valuesById.toArray(new EnumValue[valuesById.size()]);
        log.info("loadFromElement() : valueArray.length = " + valueArray.length);
    }

    private class ValueIterator extends ArrayIterator
    {
        /**
         * Creates a new iterator for the specified array.
         */
        public ValueIterator()
        {
            super(valueArray,true);
        }
    }
}
