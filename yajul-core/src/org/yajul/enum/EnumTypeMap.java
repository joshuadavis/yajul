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
import org.yajul.util.InitializationError;
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
    private static Logger log = Logger.getLogger(EnumTypeMap.class.getName());

    /**
     * The map of enumerated types (String{id}->EnumType).
     */
    private Map types;

    /**
     * Creates a type map from a resource (in the classpath) using the current
     * thread's class loader.
     * @param resourceName The name of the resource to load.
     * @return EnumTypeMap - The type map.
     * @throws EnumInitializationException If there was a problem loading
     * the resource.
     */
    public static EnumTypeMap createTypeMapFromResource(String resourceName)
            throws EnumInitializationException
    {

        ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();
        return createTypeMapFromResource(resourceName, classLoader);
    }

    /**
     * Creates a type map from a resource (in the classpath) using the current
     * thread's class loader.
     * @param resourceName The name of the resource to load.
     * @return EnumTypeMap - The type map.
     * @throws InitializationError If there was a problem loading
     * the resource.
     */
    public static EnumTypeMap loadTypeMapFromResource(String resourceName)
            throws InitializationError
    {

        try
        {
            ClassLoader classLoader =
                    Thread.currentThread().getContextClassLoader();
            return createTypeMapFromResource(resourceName, classLoader);
        }
        catch (Exception e)
        {
            String message = "Unable to load enumeration resource due to: "
                    + e.getMessage();
            log.fatal(message);
            throw new InitializationError(message,e);
        }
    }

    /**
     * Creates a type map from a resource (in the classpath).
     * @param resourceName The name of the resource to load.
     * @param classLoader The class loader to use.
     * @return EnumTypeMap - The type map.
     * @throws EnumInitializationException If there was a problem loading
     * the resource.
     */
    public static EnumTypeMap createTypeMapFromResource(
            String resourceName,
            ClassLoader classLoader)
            throws EnumInitializationException
    {
        if (log.isDebugEnabled())
            log.debug("createTypeMapFromResource() : Creating new type map...");

        EnumTypeMap typeMap = new EnumTypeMap();

        if (log.isDebugEnabled())
            log.debug("createTypeMapFromResource() : Loading "
                    + resourceName + "...");
        InputStream is =
                classLoader.getResourceAsStream(
                        resourceName);
        if (is == null)
        {
            EnumInitializationException ie = new EnumInitializationException(
                    "Unable to find " + resourceName);
            String message = "UNABLE TO LOAD ENUMERATED TYPES DUE TO: " +
                    ie.getMessage();
            typeMap = null;
            log.fatal(message, ie);
            throw ie;
        }

        try
        {
            if (log.isDebugEnabled())
                log.debug("getTypeMap() : Parsing XML...");
            typeMap.loadXML(is);
        }
        catch (EnumInitializationException e)
        {
            String message = "UNABLE TO LOAD ENUMERATED TYPES DUE TO: " +
                    e.getMessage();
            log.fatal(message, e);
            typeMap = null;
            throw e;
        }

        if (log.isDebugEnabled())
            log.debug("createTypeMapFromResource() : "
                    + "Type map created successfully.");
        return typeMap;
    }

    /**
     * Creates a new map of enumerated types.
     */
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
            EnumInitializationException x = new EnumInitializationException(
                    "Unable parse XML input due to: "
                    + e.getMessage(), e);
            log.error(x);
            throw x;
        }

        if (document == null)
            throw new EnumInitializationException(
                    "No document");

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
                enumType.loadFromElement(this, type);
                if (types.get(enumType.getId()) != null)
                    log.warn("loadXML() : Multiple definitions encountered " +
                            "for enum-type '" + enumType.getId() + "'");
                addType(enumType);
            }
            catch (Exception e)
            {
                String id = (type == null) ? "<null>" : type.getAttribute("id");
                EnumInitializationException x = new EnumInitializationException(
                        "Unable to initialize " + id
                        + " due to: " + e.getMessage(), e);
                log.error(x);
                throw x;
            }
        } // for i
        log.info("loadXML() : " + types.size() + " EnumTypes loaded.");
    }

    /**
     * Creates a subset enum type using the given superset and filter.  The
     * subset will have the supplied subset id.
     * @param supersetId The enum type id of the superset.
     * @param subsetId The enum type id of the new subset.
     * @param filter The filter that will be used on the values.
     * @return EnumType - The new subset type.
     */
    public EnumType createSubset(String supersetId,String subsetId,
                                 EnumValueFilter filter)
    {
        EnumType superset = findEnumTypeById(supersetId);
        if (superset == null)
            throw new IllegalArgumentException(
                    "Unable to find superset id " + supersetId);
        EnumType subset = superset.createSubset(subsetId,filter);
        addType(subset);
        return subset;
    }

    /**
     * Returns the EnumType given an enum type id string, or null if there is
     * no type with the specified id.
     * @param enumTypeId The id to look for.
     * @return EnumType - The enum type with the ID, or null.
     */
    public EnumType findEnumTypeById(String enumTypeId)
    {
        return (EnumType) types.get(enumTypeId);
    }

    // --- Implementation methods ---

    private void addType(EnumType enumType)
    {
        types.put(enumType.getId(), enumType);
    }


}
