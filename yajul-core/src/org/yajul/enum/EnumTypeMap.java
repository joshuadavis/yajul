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

import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;

import org.yajul.xml.DOMUtil;
import org.yajul.log.LogUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;

/**
 * Provides a map of enumerated types (EnumType and associated EnumValues),
 * loaded from an XML file.
 * User: jdavis
 * Date: Jul 3, 2003
 * Time: 2:18:23 PM
 * @author jdavis
 * @see EnumType
 * @see EnumValue
 */
public class EnumTypeMap
{
    /**
     * The logger for this class.
     */
    private static Logger log = LogUtil.getLogger(EnumTypeMap.class.getName());

    /**
     * The map of enumerated types (String{id}->EnumType).
     */
    private Map types;

    public EnumTypeMap()
    {
        this.types = new HashMap();
    }

    /**
     * Loads the map of EnumTypes and their values from the XML input stream.
     * @param is The XML input stream.
     * @throws EnumInitializationException If there was a problem loading the
     * types and their values.
     */
    public void loadXML(InputStream is) throws EnumInitializationException
    {
        Document document = null;
        try
        {
            document = DOMUtil.parse(is);
        }
        catch (Exception e)
        {
            EnumInitializationException x =  new EnumInitializationException(
                    "Unable parse 'enum-types.xml' due to: "
                    + e.getMessage(),e);
            log.error(x);
            throw x;
        }

        if (document == null)
            throw new EnumInitializationException(
                    "No document: 'enum-types.xml'");

        // Iterate through the 'enum-type' elements, and create an instance of
        // EnumType for each one.
        Element[] enumTypeElems = DOMUtil.getChildElements(document,
                "enum-type");
        if (log.isDebugEnabled())
            log.debug("loadXML() : " + enumTypeElems.length
                    + " enum-type elements found.");
        for (int i = 0; i < enumTypeElems.length; i++)
        {
            Element type = enumTypeElems[i];
            EnumType enumType = new EnumType();
            try
            {
                enumType.loadFromElement(type);
                if (types.get(enumType.getId()) != null)
                    log.warn("loadXML() : Multiple definitions encountered " +
                            "for enum-type '" + enumType.getId() + "'");
                types.put(enumType.getId(),enumType);
            }
            catch (Exception e)
            {
                EnumInitializationException x = new EnumInitializationException(
                        "Unable to initialize " + type.getAttribute("id")
                        + " due to: " + e.getMessage(), e);
                log.error(x);
                throw x;
            }
        } // for i
        log.info("loadXML() : " + types.size() + " EnumTypes loaded.");
    }

    /**
     * Returns the EnumType given an enum type id string, or null if there is
     * no type with the specified id.
     * @param enumTypeId The id to look for.
     * @return
     */
    public EnumType findEnumTypeById(String enumTypeId)
    {
        return (EnumType)types.get(enumTypeId);
    }

}
