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

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.yajul.log.LogUtil;

/**
 * Provides basic value behavior for an enumerated type, including an id
 * an 'XML value' (i.e. the value used for XML encoding/decoding), and a 'text value' (i.e.
 * a value that might be used in a user interface).
 * @author Joshua Davis
 */
public class EnumValue implements Serializable, Comparable
{
    /**
     * The logger for this class.
     */
    private static Logger log = LogUtil.getLogger(EnumValue.class.getName());

    private int id;
    private Integer idInteger;
    private String xmlValue;
    private String textValue;
    private String typeId;

    /**
     * Returns the id of the enumerated value.
     * @return int - The id of the enumerated value.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the id of the type that this enumerated value belongs to.
     * @return String - The id of the type that this enumerated type is in.
     */
    public String getTypeId()
    {
        return typeId;
    }

    /**
     * Returns the id of this value as an Integer object.
     * @return Integer - The id of the enumerated value.
     */
    public Integer getIdInteger()
    {
        return idInteger;
    }

    /**
     * Returns the application specific XML text representation of this enumerated value.
     * @return String - The XML representation of this value.
     */
    public String getXmlValue()
    {
        return xmlValue;
    }

    /**
     * Returns the application specific plain text (UI text) for this value.
     * @return String - The plain text representation of this value.
     */
    public String getTextValue()
    {
        return textValue;
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
     * @return  a hash code value for this object.
     * @see     Object#equals(Object)
     */
    public int hashCode()
    {
        return id;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * @param   obj   the reference object with which to compare.
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     * @see     Boolean#hashCode()
     */
    public boolean equals(Object obj)
    {
        EnumValue other = (EnumValue)obj;
        return id == other.id;
    }

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
        buf.append("[ EnumValue id=").append(id).
                append(" xmlValue='").append(xmlValue).
                append("' textValue='").append(textValue).
                append("']");
        return buf.toString();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     * <br>throws ClassCastException if the specified object's type prevents it
     * from being compared to this Object.
     */
    public int compareTo(Object o)
    {
        EnumValue other = (EnumValue)o;
        return idInteger.compareTo(other.idInteger);
    }

    /**
     * Sets the plain text value.
     * @param textValue The new plain text value.
     */
    protected void setTextValue(String textValue)
    {
        this.textValue = textValue;
    }

    /**
     * Sets the XML textual value.
     * @param xmlValue The new XML value.
     */
    protected void setXmlValue(String xmlValue)
    {
        this.xmlValue = xmlValue;
    }

    /**
     * Sets the id of this value.
     * @param id The new id.
     */
    protected void setId(int id)
    {
        this.id = id;
        this.idInteger = new Integer(id);
    }

    /**
     * Loads this instance from the XML element.  Sub-classes should override
     * this to load in specialized properties.
     * @param map The EnumTypeMap currently being loaded.
     * @param type The type that this value will belong to.
     * @param elem The DOM element to load the attributes from.
     * @throws Exception If there was a problem creating the value from the
     * DOM element.
     */
    protected void loadFromElement(EnumTypeMap map,EnumType type,Element elem) throws Exception
    {
        typeId = type.getId();
        String idString = elem.getAttribute("id");
        if (log.isDebugEnabled())
            log.debug("loadFromElement() : loading value "
                    + idString + ", type " + typeId);
        setId(Integer.parseInt(idString));
        setTextValue(elem.getAttribute("textValue"));
        setXmlValue(elem.getAttribute("xmlValue"));
    }
}
