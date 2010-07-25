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

/**
 * A wiki parameter object.
 * 
 * @author MikhailKotelnikov
 */
public class WikiParameter {

    private String fKey;

    private String fStr;

    private Boolean fValid;

    private String fValue;

    /**
     * @param key
     * @param value
     */
    public WikiParameter(String key, String value) {
        fKey = key;
        fValue = value;
    }

    /**
     * @param pair
     */
    public WikiParameter(WikiParameter pair) {
        fKey = pair.getKey();
        fValue = pair.getValue();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof WikiParameter))
            return false;
        WikiParameter pair = (WikiParameter) obj;
        return fKey.equals(pair.fKey) &&
            (fValue == pair.fValue || (fValue != null && fValue
                .equals(pair.fValue)));
    }

    /**
     * @return the key
     */
    public String getKey() {
        return fKey;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return fValue;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fKey.hashCode() ^ (fValue != null ? fValue.hashCode() : 0);
    }

    /**
     * @return <code>true</code> if this key/value pair is valid
     */
    public boolean isValid() {
        if (fValid == null) {
            int len = (fKey != null) ? fKey.length() : 0;
            boolean result = len > 0;
            boolean delimiter = false;
            for (int i = 0; result && i < len; i++) {
                char ch = fKey.charAt(i);
                if (ch == ':') {
                    result = !delimiter && i > 0 && i < len - 1;
                    delimiter = true;
                } else if (ch == '.' || ch == '-') {
                    result = i > 0 && i < len - 1;
                } else {
                    result &= (i == 0 && Character.isLetter(ch)) ||
                        Character.isLetterOrDigit(ch);
                }
            }
            fValid = result ? Boolean.TRUE : Boolean.FALSE;
        }
        return fValid == Boolean.TRUE;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (fStr == null) {
            fStr = fKey + "='" + WikiPageUtil.escapeXmlAttribute(fValue) + "'";
        }
        return fStr;
    }

}