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
import org.yajul.util.ReflectionUtil;
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
    private static Logger log = Logger.getLogger(EnumType.class.getName());

    /** The enum value id returned when there is an error. **/
    public static final int UNDEFINED = Integer.MIN_VALUE;

    /** The class of the values for this enumerated type. **/
    private Class enumValueClass;
    /** The class or interface that defines Java constants for this enum. **/
    private Class constantClass;
    /** The id of this enumerated type. **/
    private String id;
    /** The minimum value id. **/
    private int minId;
    /** The maximum value id. **/
    private int maxId;
    /** True if the ids are a contiguous sequence of integers. **/
    private boolean contiguous;

    /**
     * An array of all values, where the index is the id.
     * This will be null, if the value ids are not sequential (ish).
     */
    private EnumValue[] valueArrayById;

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

    /** Map of values by constant name. **/
    private Map valuesByConstantName;

    /** The values of the enumerated type, in array form. */
    private EnumValue[] valueArray;

    /** Array of all ids, in order. **/
    private int[] idArray;
    /** The array of all text values, in ID order. **/
    private String[] textArray;

    /** The enum type has been constructed, but no values have been added. **/
    private static final int STATE_INITIAL = 0;
    /** Ready to add values. **/
    private static final int STATE_ADD_VALUES = 1;
    /** All values have been added, fully initialized. **/
    private static final int STATE_INITIALIZED = 2;

    /** The state of the enum type (STATE_xxx). **/
    private int state = STATE_INITIAL;

    /**
     * Creates a new EnumType object.
     */
    protected EnumType()
    {
        valueByXML = new HashMap();
        valueByText = new HashMap();
        valueById = new TreeMap();
    }

    // --- Accessors ---

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
     * Returns an array of all values in ID order.
     * @return EnumValue[] - An array of all values, in ID order.
     * @see EnumValue
     */
    public EnumValue[] getValueArray()
    {
        synchronized (this)
        {
            if (valueArray == null)
            {
                ArrayList enumValues = new ArrayList();
                Iterator iterator = iterator();
                while (iterator.hasNext())
                {
                    enumValues.add(iterator.next());
                }
                valueArray = (EnumValue[]) enumValues.toArray(
                        new EnumValue[enumValues.size()]);
            }
        }
        return valueArray;
    }

    /**
     * Returns the ids of all values in an array.
     * @return int[] - The array of all value ids.
     */
    public int[] getIdArray()
    {
        synchronized (this)
        {
            if (idArray == null)
            {
                EnumValue[] values = getValueArray();
                idArray = new int[values.length];
                for (int i = 0; i < values.length; i++)
                {
                    idArray[i] = values[i].getId();
                }
            }
        }
        return idArray;
    }

    /**
     * Returns the text of all values in an array in 'id' order.
     * @return String[] - The array of all value text.
     */
    public String[] getTextArray()
    {
        synchronized (this)
        {
            if (textArray == null)
            {
                EnumValue[] values = getValueArray();
                textArray = new String[values.length];
                for (int i = 0; i < values.length; i++)
                {
                    textArray[i] = values[i].getTextValue();
                }
            }
        }
        return textArray;
    }

    /**
     * Translate each id into it's text value, and return a parallel
     * array of strings.
     * @param ids A subset of the ids to get the text values for.
     * @return String[] - The text values for each id.
     */
    public String[] getTextArray(int[] ids)
    {
        String[] text = new String[ids.length];
        for(int i = 0; i < ids.length ; i++)
            text[i] = valueIdToText(ids[i]);
        return text;
    }

    /**
     * Returns the minimum value id.
     * @return int - The minimum value id.
     */
    public int getMinId()
    {
        return minId;
    }

    /**
     * Returns the maximum value id.
     * @return int - The maximum value id.
     */
    public int getMaxId()
    {
        return maxId;
    }

    /**
     * Returns true if the ids are a contiguous sequence of integers.
     * @return boolean - True if the ids are a contiguous sequence of integers.
     */
    public boolean isContiguous()
    {
        synchronized (this)
        {
            return contiguous;
        }
    }

    /**
     * Returns the number of values in the enumerated type.
     * @return int - The number of values in the type.
     */
    public int size()
    {
        return valueById.size();
    }

    // --- Public Methods ---

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
     * Returns the value, given it's id.
     * @param id The id to look for.
     * @return EnumValue - The enumerated value.
     * @throws EnumValueNotFoundException if the required value was not found.
     */
    public EnumValue requireValueById(int id)
            throws EnumValueNotFoundException
    {
        EnumValue value = findValueById(id);
        if (value == null)
            throw new EnumValueNotFoundException(getId(), Integer.toString(id));
        return value;
    }

    /**
     * Shortcut method: Returns the text for a value, given it's id.
     * @param id The value id.
     * @return String - The text for the value.
     */
    public String findTextById(int id)
    {
        try
        {
            EnumValue value = requireValueById(id);
            return value.getTextValue();
        }
        catch (EnumValueNotFoundException e)
        {
            String message = "Unable to find value by id due to: " + e.getMessage();
            log.error(message,e);
            throw new IllegalArgumentException(message);
        }
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
     * Creates a subset of this enum type by passing the values
     * through a filter.  EnumValue elements will not be cloned.
     * @param subsetId The enum type id of the subset.
     * @param filter The value filter.
     * @return EnumType - The subset enum-type.
     */
    public EnumType createSubset(String subsetId,EnumValueFilter filter)
    {
        EnumType subset = new EnumType();
        Iterator iter = iterator();
        while (iter.hasNext())
        {
            EnumValue value = (EnumValue) iter.next();
            if (filter.test(value))
                subset.addValue(value);
        }
        subset.afterAddingValues();
        return subset;
    }

    // --- java.lang.Object methods --

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * @return  a string representation of the object.
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("[ EnumType id=").append(id);
        buf.append(" isContiguous=").append(isContiguous());
        Iterator iter = iterator();
        while (iter.hasNext())
        {
            EnumValue value = (EnumValue) iter.next();
            buf.append("\n  ").append(value.toString());
        }
        buf.append("\n]");
        return buf.toString();
    }

    // --- Implementation Methods (package and private) ---

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
    void loadFromElement(EnumTypeMap map, Element elem)
            throws EnumInitializationException
    {
        // Synchronize, so that other threads will not retrieve incorrect
        // values.
        synchronized (this)
        {
            setFromAttributes(elem);

            // Look for the 'enum-value' elements inside this 'enum-type' element.
            Element[] valueElements = DOMUtil.getChildElements(elem, "enum-value");
            beforeAddingValues();
            loadValuesFromElements(valueElements, map);
            afterAddingValues();
        } // synchronized
    }

    private void loadValuesFromElements(Element[] valueElements,
                                        EnumTypeMap map)
            throws EnumInitializationException
    {

        // Parse each element, adding the parsed elements to the index maps and
        // updating the minimum and maximum id fields.
        for (int i = 0; i < valueElements.length; i++)
        {
            Element valueElement = valueElements[i];

            EnumValue value = loadValueFromElement(map, valueElement);

            addValue(value);

        } // for (i)
    }

    private void beforeAddingValues()
    {
        if (state != STATE_INITIAL)
            throw new IllegalStateException(
                    "Unable to prepare for adding values, state = "
                    + state);
        // Set the minimum and maximum index values to their limits, so the
        // loop can set them as the elements are parsed.
        maxId = Integer.MIN_VALUE;
        minId = Integer.MAX_VALUE;
        state = STATE_ADD_VALUES;
    }

    private void addValue(EnumValue value)
    {
        int id;
        id = value.getId();

        if (id > maxId)
            maxId = id;
        if (id < minId)
            minId = id;
        // Set up the index maps.
        valueById.put(value.getIdInteger(), value);
        valueByXML.put(value.getXmlValue(), value);
        valueByText.put(value.getTextValue(), value);
    }

    private void afterAddingValues()
    {
        initializeIdMap();
        initializeConstantNames();
        state = STATE_INITIALIZED;
    }

    private void initializeConstantNames()
    {
        // If the constantClass was specified, build the map of constants.
        if (constantClass != null)
        {
            Map namesById = ReflectionUtil.getConstantNameMap(constantClass);
            // Iterate through the constant values found in the class...
            Set entries = namesById.entrySet();
            for (Iterator iterator = entries.iterator(); iterator.hasNext();)
            {
                Map.Entry entry = (Map.Entry) iterator.next();
                Integer id = (Integer) entry.getKey();
                EnumValue value = findValueById(id.intValue());
                if (value == null)
                {
                    if (log.isDebugEnabled())
                        log.debug(
                            "afterAddingValues() : No EnumValue found for "
                            + id + " in class " + constantClass.getName());
                    continue;
                }
                value.setConstantName((String)entry.getValue());
            }
        }
    }

    private void initializeIdMap()
    {
        // Determine whether the ids are a contiguous sequence of integers.
        contiguous = ((maxId - minId) == (valueById.size() - 1));

        // If ids will fit in an array, use an array
        // for the main element index.
        if (contiguous)
        {
            if (log.isDebugEnabled())
                log.debug("afterAddingValues() : Using an array for the id map");
            Set entries = valueById.entrySet();
            ArrayList list = new ArrayList(valueById.size());
            for (Iterator iterator = entries.iterator(); iterator.hasNext();)
            {
                Map.Entry entry = (Map.Entry) iterator.next();
                Integer key = (Integer) entry.getKey();
                EnumValue value = (EnumValue) entry.getValue();
                int index = key.intValue() - minId;
                list.add(index, value);
            } // for
            // Put the values into the array.  Now, the find methods will
            // use this array for lookup.
            valueArrayById = (EnumValue[])
                    list.toArray(new EnumValue[list.size()]);
            // If the ids are contiguous *AND* the minimum id is zero, then
            // the array representation can be used for getValueArray() as well.
            if (minId == 0)
                valueArray = valueArrayById;
            // Otherwise, the value array will need to be copied from
            // the iterator.
        } // if contiguous
        else if (log.isDebugEnabled())
            log.debug("afterAddingValues() : Using a TreeMap for the id map");
    }

    private EnumValue loadValueFromElement(EnumTypeMap map,
                                           Element valueElement)
            throws EnumInitializationException
    {
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
        return value;
    }

    private void setFromAttributes(Element elem)
            throws EnumInitializationException
    {
        id = elem.getAttribute("id");
        if (StringUtil.isEmpty(id))
            throw new EnumInitializationException(
                    "Expected 'id' attribute in enum-type!");

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

        try
        {
            String constantClassName = elem.getAttribute("constantClass");
            if (!StringUtil.isEmpty(constantClassName))
                constantClass = Class.forName(constantClassName);
            else
                constantClass = null;
        }
        catch (ClassNotFoundException e)
        {
            throw new EnumInitializationException(
                    "Unable to find value class! " + e.getMessage(), e);
        }
    }

    private void createLowerCaseTextMap()
    {
        // Create a map using the lower case text.
        valuesByLowerCaseText = new HashMap();
        Map.Entry entry = null;
        String key = null;
        EnumValue value = null;
        for (Iterator iterator = valuesByLowerCaseText.entrySet().iterator();
             iterator.hasNext();)
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
