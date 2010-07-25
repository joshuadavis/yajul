/*******************************************************************************
 * Copyright (c) 2005,2007 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.wikimodel.wem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a default implementation of the {@link IWikiParams} interface.
 * 
 * @author kotelnikov
 */
public class WikiParameters {

    /**
     * An empty parameter list
     */
    public final static WikiParameters EMPTY = new WikiParameters();

    /**
     *
     */
    private static final long serialVersionUID = 1253393289284318413L;

    /**
     * @param array from this array of bytes the next token will be returned
     * @param pos the current position in the array of bytes
     * @param buf to this buffer the extracted token value will be appended
     * @return the new position in the array after extracting of a new token
     */
    protected static int getNextToken(char[] array, int pos, StringBuffer buf) {
        buf.delete(0, buf.length());
        boolean escaped = false;
        if (pos < array.length && (array[pos] == '\'' || array[pos] == '"')) {
            char endChar = array[pos];
            pos++;
            for (; pos < array.length && (escaped || array[pos] != endChar); pos++) {
                escaped = array[pos] == '\\';
                if (!escaped)
                    buf.append(array[pos]);
            }
            if (pos < array.length)
                pos++;
        } else {
            for (; pos < array.length; pos++) {
                if (array[pos] == '=' || Character.isSpaceChar(array[pos]))
                    break;
                if (!escaped && (array[pos] == '\'' || array[pos] == '"'))
                    break;
                escaped = array[pos] == '\\';
                if (!escaped)
                    buf.append(array[pos]);
            }
        }
        return pos;
    }

    /**
     * Moves forward the current position in the array until the first not empty
     * character is found.
     * 
     * @param array the array of characters where the spaces are searched
     * @param pos the current position in the array; starting from this position
     *        the spaces will be searched
     * @param buf to this buffer all not empty characters will be added
     * @return the new position int the array of characters
     */
    protected static int removeSpaces(char[] array, int pos, StringBuffer buf) {
        buf.delete(0, buf.length());
        for (; pos < array.length
            && (array[pos] == '=' || Character.isSpaceChar(array[pos])); pos++) {
            if (array[pos] == '=')
                buf.append(array[pos]);
        }
        return pos;
    }

    /**
     * Splits the given string into a set of key-value pairs; all extracted
     * values will be added to the given list
     * 
     * @param str the string to split
     * @param list to this list all extracted values will be added
     */
    protected static void splitToPairs(String str, List<WikiParameter> list) {
        if (str == null)
            return;
        char[] array = str.toCharArray();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < array.length;) {
            String key = null;
            String value = null;
            i = removeSpaces(array, i, buf);
            if (i >= array.length)
                break;
            i = getNextToken(array, i, buf);
            key = buf.toString();

            i = removeSpaces(array, i, buf);
            if (buf.indexOf("=") >= 0) {
                i = getNextToken(array, i, buf);
                value = buf.toString();
            }
            WikiParameter entry = new WikiParameter(key, value);
            list.add(entry);
        }
    }

    private List<WikiParameter> fList = new ArrayList<WikiParameter>();

    private Map<String, WikiParameter[]> fMap;

    private String fStr;

    /**
     */
    WikiParameters() {
        this((String) null);
    }

    /**
     * @param list
     */
    public WikiParameters(List<WikiParameter> list) {
        super();
        fList.addAll(list);
    }

    /**
     * @param str
     */
    public WikiParameters(String str) {
        super();
        splitToPairs(str, fList);
    }

    /**
     * Creates a new copy of this parameter object with new specified key/value
     * pair.
     * 
     * @param key the parameter name
     * @param value the value of the parameter
     * @return a new copy of parameters object with the given key/value pair
     */
    public WikiParameters addParameter(String key, String value) {
        WikiParameters result = new WikiParameters();
        result.fList.addAll(fList);
        result.fList.add(new WikiParameter(key, value));
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof WikiParameters))
            return false;
        WikiParameters params = (WikiParameters) obj;
        return fList.equals(params.fList);
    }

    /**
     * @param pos the position of the parameter
     * @return the parameter from the specified position
     */
    public WikiParameter getParameter(int pos) {
        return pos < 0 || pos >= fList.size() ? null : fList.get(pos);
    }

    /**
     * @param key the key of the parameter
     * @return the wiki parameter by key
     */
    public WikiParameter getParameter(String key) {
        WikiParameter[] list = getParameters(key);
        return (list != null) ? list[0] : null;
    }

    private Map<String, WikiParameter[]> getParameters() {
        if (fMap == null) {
            fMap = new HashMap<String, WikiParameter[]>();
            for (WikiParameter param : fList) {
                String key = param.getKey();
                WikiParameter[] list = fMap.get(key);
                int len = list != null ? list.length : 0;
                WikiParameter[] newList = new WikiParameter[len + 1];
                if (len > 0) {
                    System.arraycopy(list, 0, newList, 0, len);
                }
                newList[len] = param;
                fMap.put(key, newList);
            }
        }
        return fMap;
    }

    /**
     * Returns all parameters with this key
     * 
     * @param key the key of the parameter
     * @return the wiki parameter by key
     */
    public WikiParameter[] getParameters(String key) {
        Map<String, WikiParameter[]> map = getParameters();
        WikiParameter[] list = map.get(key);
        return list;
    }

    /**
     * Returns the number of parameters in the internal list.
     * 
     * @return the number of parameters in the internal list
     */
    public int getSize() {
        return fList.size();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return fList.hashCode();
    }

    /**
     * @param key the key of the parameter to remove
     * @return a new copy of parameter list without the specified parameter; if
     *         this parameter list does not contain such a key then this method
     *         returns a reference to this object
     */
    public WikiParameters remove(String key) {
        int pos = 0;
        for (WikiParameter param : fList) {
            if (key.equals(param.getKey()))
                break;
            pos++;
        }
        WikiParameters result = this;
        if (pos < fList.size()) {
            result = new WikiParameters(this.fList);
            result.fList.remove(pos);
        }
        return result;
    }

    /**
     * Returns a new list containing all parameters defined in this object.
     * 
     * @return a list of all parameters
     */
    public List<WikiParameter> toList() {
        List<WikiParameter> result = new ArrayList<WikiParameter>();
        result.addAll(fList);
        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (fStr == null) {
            StringBuffer buf = new StringBuffer();
            int len = fList.size();
            int counter = 0;
            for (int i = 0; i < len; i++) {
                WikiParameter pair = fList.get(i);
                if (pair.isValid()) {
                    buf.append(" ");
                    buf.append(pair);
                    counter++;
                }
            }
            fStr = buf.toString();
        }
        return fStr;
    }
}
