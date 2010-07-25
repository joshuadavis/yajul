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

import java.util.Arrays;

/**
 * This class contains some utility methods used for escaping xml strings as
 * well as for encoding/decoding http parameters.
 * 
 * @author kotelnikov
 */
public class WikiPageUtil {

    /**
     * Reserved symbols - see RFC 2396 (http://www.ietf.org/rfc/rfc2396.txt)
     */
    private static final char[] HTTP_RESERVED_SYMBOLS = {
        ';',
        '/',
        '?',
        ':',
        '@',
        '&',
        '=',
        '+',
        '$',
        ',' };

    /**
     * Unreserved symbols - see RFC 2396 (http://www.ietf.org/rfc/rfc2396.txt)
     */
    private static final char[] HTTP_UNRESERVED_SYMBOLS = {
        '-',
        '_',
        '.',
        '!',
        '~',
        '*',
        '\'',
        '(',
        ')',
        '#' };

    static {
        Arrays.sort(HTTP_RESERVED_SYMBOLS);
        Arrays.sort(HTTP_UNRESERVED_SYMBOLS);
    }

    /**
     * Returns the decoded http string - all special symbols, replaced by
     * replaced by the %[HEX HEX] sequence, where [HEX HEX] is the hexadecimal
     * code of the escaped symbol will be restored to its original characters
     * (see RFC-2616 http://www.w3.org/Protocols/rfc2616/).
     * 
     * @param str the string to decode
     * @return the decoded string.
     */
    public static String decodeHttpParams(String str) {
        if (str == null)
            return "";
        StringBuffer buf = new StringBuffer();
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            char ch = array[i];
            if (ch == '%') {
                if (i + 2 >= array.length)
                    break;
                int val = (array[++i] - '0');
                val <<= 4;
                val |= (array[++i] - '0');
                ch = (char) val;
            }
            buf.append(ch);
        }
        return buf.toString();
    }

    /**
     * Returns the encoded string - all special symbols will be replaced by
     * %[HEX HEX] sequence, where [HEX HEX] is the hexadecimal code of the
     * escaped symbol (see RFC-2616
     * http://www.w3.org/Protocols/rfc2616/rfc2616.html).
     * 
     * @param str the string to encode
     * @return the encoded string.
     */
    public static String encodeHttpParams(String str) {
        if (str == null)
            return "";
        StringBuffer buf = new StringBuffer();
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            char ch = array[i];
            if ((ch >= 'a' && ch <= 'z')
                || (ch >= 'A' && ch <= 'Z')
                || (ch >= '0' && ch <= '9')
                || Character.isDigit(ch)
                || Arrays.binarySearch(HTTP_RESERVED_SYMBOLS, ch) >= 0
                || Arrays.binarySearch(HTTP_UNRESERVED_SYMBOLS, ch) >= 0) {
                buf.append(array[i]);
            } else {
                buf.append("%" + Integer.toHexString(array[i]));
            }
        }
        return buf.toString();
    }

    /**
     * Returns the escaped attribute string.
     * 
     * @param str the string to escape
     * @return the escaped string.
     */
    public static String escapeXmlAttribute(String str) {
        return escapeXmlString(str, true);
    }

    /**
     * Returns the escaped string.
     * 
     * @param str the string to escape
     * @return the escaped string.
     */
    public static String escapeXmlString(String str) {
        return escapeXmlString(str, false);
    }

    /**
     * Returns the escaped string.
     * 
     * @param str the string to escape
     * @param escapeQuots if this flag is <code>true</code> then "'" and "\""
     *        symbols also will be escaped
     * @return the escaped string.
     */
    public static String escapeXmlString(String str, boolean escapeQuots) {
        if (str == null)
            return "";
        StringBuffer buf = new StringBuffer();
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '>'
                || array[i] == '&'
                || array[i] == '<'
                || (escapeQuots && (array[i] == '\'' || array[i] == '"'))) {
                buf.append("&#x" + Integer.toHexString(array[i]) + ";");
            } else {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * @param text
     * @return CDATA block corresponding to the given text
     */
    public static String getCDATA(String text) {
        StringBuffer buf = new StringBuffer();
        buf.append("<![CDATA[");
        int startPos = 0;
        while (startPos >= 0 && startPos < text.length()) {
            int id = text.indexOf("]]>", startPos);
            if (id >= 0) {
                buf.append(text.substring(startPos, id));
                buf.append("]]]]><![CDATA[>");
                startPos += id + "]]>".length();
            } else {
                buf.append(text.substring(startPos));
                startPos = -1;
            }
        }
        buf.append("]]>");
        return buf.toString();
    }
}
