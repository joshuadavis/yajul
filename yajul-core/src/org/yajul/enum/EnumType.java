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

package org.yajul.enum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.yajul.log.LogUtil;
import org.yajul.util.ArrayIterator;
import org.yajul.util.StringUtil;
import org.yajul.xml.DOMUtil;

/**
 * Provides a collection of enumerated values, otherwise known as an enumerated
 * type.  Each value can be found by it's id, it's XML text
 * value, or it's UI text value.
 * @author Joshua Davis
 */
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
     * This will be null, if the value ids are not sequential (ish).
     */
    private EnumValue[] valueArrayById;

    /**
     * The values of the enumerated type, in array form.
     */
    private EnumValue[] valueArray;

    /**
     * A map of values, by their id.
     */
    private SortedMap valueById;

    /** Map of values by XML string. **/
    private Map valueByXML;

    /** Map of values by text string. **/
    private Map valueByText;

    /** Map of values by text string (case insensitive). **/
    private Map valuesByLowerCaseText;

    /**
     * Creates a new EnumType object.
     */
    protected EnumType()
    {
        valueByXML = new HashMap();
        valueByText = new HashMap();
        valueById = new TreeMap();
    }

    /**
     * Returns the id of this enumerated type in the EnumTypeMap that
     * it is contained by.
     * @return String - The unique id of this enumerated type.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the class that will be used to represent the values of the
     * enumerated type.  This class must extend EnumValue.
     * @return Class - The class of the enum values.
     */
    public Class getEnumValueClass()
    {
        return enumValueClass;
    }

    /**
     * Returns true if the id is valid, false if not.
     * @param id The enum value id.
     * @return boolean - True if 'id' is valid.
     */
    public boolean isValid(int id)
    {
        return (findValueById(id) != null);
    }

    /**
     * Returns the value, given it's id.
     * @param id The value id to look for.
     * @return EnumValue - The enumerated value, or null if the id is not valid.
     */
    public EnumValue findValueById(int id)
    {
        // If the values are in the 'index' array, use it.
        if (valueArrayById != null)
        {
            // Subtract the minimum id, so the index of the first
            // element is zero.
            int index = id - minId;
//            if (log.isDebugEnabled())
//                log.debug("findValueById() : id = "
//                        + id + " index = " + index );
            if (index < 0 || index >= valueArrayById.length)
                return null;
            else
                return valueArrayById[index];
        }
        // Otherwise, use the valueById map.
        else
            return (EnumValue) valueById.get(new Integer(id));
    }

    /**
     * Returns the value, given it's XML text.
     * @param xml The XML text to look for.
     * @return EnumValue - The enumerated value.
     */
    public EnumValue findValueByXml(String xml)
    {
        return (EnumValue) valueByXML.get(xml);
    }

    /**
     * Returns the value, given it's XML text.
     * @param xml The XML text to look for.
     * @return EnumValue - The enumerated value.
     * @throws EnumValueNotFoundException if the required value was not found.
     */
    public EnumValue requireValueByXml(String xml)
            throws EnumValueNotFoundException
    {
        EnumValue value = (EnumValue) valueByXML.get(xml);
        if (value == null)
            throw new EnumValueNotFoundException(id, xml);
        return value;
    }

    /**
     * Returns the value, given it's UI textual representation.
     * @param text The text to look for.
     * @return EnumValue - The enumerated value.
     */
    public EnumValue findValueByText(String text)
    {
        return (EnumValue) valueByText.get(text);
    }

    /**
     * Returns the value, given it's UI textual representation, ignoring
     * case.
     * @param text The text to look for.
     * @return EnumValue - The enumerated value.
     */
    public EnumValue findValueByTextIgnoreCase(String text)
    {
        if (valuesByLowerCaseText == null)
            createLowerCaseTextMap();
        return (EnumValue) valuesByLowerCaseText.get(text.toLowerCase());
    }

    /**
     * Converts an XML text value into an id for this map.  Returns
     * UNDEFINED (-1) if the XML text was not valid.
     * @param xml The XML text to look for.
     * @return int - The enum value id, or UNDEFINED if the XML text was not
     * valid.
     */
    public int xmlToValueId(String xml)
    {
        EnumValue value = findValueByXml(xml);
        return (value == null) ? UNDEFINED : value.getId();
    }

    /**
     * Converts a text value into an id for this map.  Returns
     * UNDEFINED (-1) if the text was not valid.
     * @param text The text to look for.
     * @return int - The enum value id, or UNDEFINED if the text was not
     * valid.
     */
    public int textToValueId(String text)
    {
        EnumValue value = findValueByText(text);
        return (value == null) ? UNDEFINED : value.getId();
    }

    /**
     * Converts an id into a XML text value for this map.  Returns
     * null if the id was not valid.
     * @param id - The enum value id.
     * @return String - The XML text, or null if the id was invalid.
     */
    public String valueIdToXml(int id)
    {
        EnumValue value = findValueById(id);
        if (value == null)
            return null;
        else
            return value.getXmlValue();
    }

    /**
     * Converts an id into a UI text value for this map.  Returns
     * null if the id was not valid.
     * @param id - The enum value id.
     * @return String - The UI text, or null if the id was invalid.
     */
    public String valueIdToText(int id)
    {
        EnumValue value = findValueById(id);
        if (value == null)
            return null;
        else
            return value.getTextValue();
    }

    /**
     * Returns an iterator that will produce all of the values of this type
     * in ID order.
     * @return Iterator - An iterator of all the enumerated values.
     */
    public Iterator iterator()
    {
        if (valueArrayById != null)
            return new ValueIterator();
        else
            return valueById.values().iterator();
    }

    /**
     * Sets the class that will be used for the enumerated values.
     * @param enumValueClass The enumerated value class.
     */
    void setEnumValueClass(java.lang.Class enumValueClass)
    {
        this.enumValueClass = enumValueClass;
    }

    /**
     * Sets the id of this enumerated type.
     * @param id The id of this enumerated type.
     */
    void setId(String id)
    {
        this.id = id;
    }

    /**
     * Loads the enumerated type from a DOM element.
     * @param elem The DOM element.
     * @throws EnumInitializationException If the enumerated type could not
     * be created from the DOM element.
     */
    void loadFromElement(EnumTypeMap map, Element elem) throws EnumInitializationException
    {
        id = elem.getAttribute("id");
        try
        {
            String valueClassName = elem.getAttribute("valueClass");
            if (!StringUtil.isEmpty(valueClassName))
                enumValueClass = Class.forName(valueClassName);
            else
                enumValueClass = EnumValue.class;
        }
        catch (ClassNotFoundException e)
        {
            throw new EnumInitializationException(
                    "Unable to find value class! " + e.getMessage(), e);
        }

        // Look for the 'enum-value' elements inside this 'enum-type' element.
        Element[] valueElements = DOMUtil.getChildElements(elem, "enum-value");

        // Set the minimum and maximum index values to their limits, so the
        // loop can set them as the elements are parsed.
        maxId = Integer.MIN_VALUE;
        minId = Integer.MAX_VALUE;
        int id = 0;

        // Scan the elements to find the minimum and maximum ids.

        // Parse each element, adding the parsed elements to the index maps and
        // updating the minimum and maximum id fields.
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
                throw new EnumInitializationException(
                        "Unable to instantiate value class due to: "
                        + e.getMessage(), e);
            }
            catch (IllegalAccessException e)
            {
                throw new EnumInitializationException(
                        "Unable to instantiate value class due to: "
                        + e.getMessage(), e);
            }

            try
            {
                value.loadFromElement(map, this, valueElement);
            }
            catch (Exception e)
            {
                throw new EnumInitializationException(
                        "Unable to load value instance due to: "
                        + e.getMessage(), e);
            }

            id = value.getId();

            if (id > maxId)
                maxId = id;
            if (id < minId)
                minId = id;
            // Set up the index maps.
            valueById.put(value.getIdInteger(), value);
            valueByXML.put(value.getXmlValue(), value);
            valueByText.put(value.getTextValue(), value);
        } // for (i)

        // If ids will fit in an array, use an array
        // for the main element index.
        if (maxId - minId == (valueById.size() - 1))
        {
            if (log.isDebugEnabled())
                log.debug("loadFromElement() : Using an array for the id map");
            Set entries = valueById.entrySet();
            ArrayList list = new ArrayList(valueById.size());
            for (Iterator iterator = entries.iterator(); iterator.hasNext();)
            {
                Map.Entry entry = (Map.Entry) iterator.next();
                Integer key = (Integer) entry.getKey();
                EnumValue value = (EnumValue) entry.getValue();
                int index = key.intValue() - minId;
//                if (log.isDebugEnabled())
//                    log.debug(
//                            "loadFromElement() : " +
//                            "valueArray[" + index + "], id = " + key);
                list.add(index, value);
            }
            valueArrayById = (EnumValue[]) list.toArray(new EnumValue[list.size()]);
        }
        else if (log.isDebugEnabled())
            log.debug("loadFromElement() : Using a TreeMap for the id map");
    }

    /**
     * Returns an array of all values in ID order.
     * @return EnumValue[] - An array of all values, in ID order.
     * @see EnumValue
     */
    public EnumValue[] getValueArray()
    {
        // TODO: Use the valueArrayById, IFF the IDs are contiguous and start with zero.
        if (valueArray == null)
        {
            ArrayList enumValues = new ArrayList();
            Iterator iterator = iterator();
            while (iterator.hasNext())
            {
                enumValues.add(iterator.next());
            }
            valueArray = (EnumValue[]) enumValues.toArray(new EnumValue[enumValues.size()]);
        }
        return valueArray;
    }

    private void createLowerCaseTextMap()
    {
        // Create a map using the lower case text.
        valuesByLowerCaseText = new HashMap();
        Map.Entry entry = null;
        String key = null;
        EnumValue value = null;
        for (Iterator iterator = valuesByLowerCaseText.entrySet().iterator(); iterator.hasNext();)
        {
            entry = (Map.Entry) iterator.next();
            key = (String) entry.getKey();
            value = (EnumValue) entry.getValue();
            valuesByLowerCaseText.put(key.toLowerCase(), value);
        } // for
    }

    /** Iterates the values of the enumerated type. **/
    private class ValueIterator extends ArrayIterator
    {
        /**
         * Creates a new iterator for the specified array.
         */
        public ValueIterator()
        {
            super(valueArrayById, true);
        }
    }
}
