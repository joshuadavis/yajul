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
package org.yajul.net.http;

import java.util.ArrayList;

/**
 * TODO: Add class javadoc
 * User: josh
 * Date: Jan 29, 2004
 * Time: 7:21:40 AM
 */
public class Bytes
{
    /**
     * Returns true if a character is a control character.
     */
    public static boolean isControl(int c)
    {
        return (((c >= 0) && (c <= 31)) || (c == 127));
    }

    /**
     * Returns true if the character is a separator defined by HTTP/1.1
     *
     * <i>From RFC 2616</i>
     * <pre>
     *  separators     = "(" | ")" | "<" | ">" | "@"
     *                 | "," | ";" | ":" | "\" | <">
     *                 | "/" | "[" | "]" | "?" | "="
     *                 | "{" | "}" | SP | HT
     * </pre>
     */
    public static boolean isSeparator(int c)
    {
        return (
                (c == '(') || (c == ')') || (c == '<') || (c == '>') || (c == '@') ||
                (c == ',') || (c == ';') || (c == ':') || (c == '\\') || (c == '\"') ||
                (c == '/') || (c == '[') || (c == ']') || (c == '?') || (c == '=') ||
                (c == '{') || (c == '}') || (c == ' ') || (c == '\t')
                );
    }

    /**
     * Returns true if the character is a HEX character.
     * <i>From RFC 2616</i>
     *   Hexadecimal numeric characters are used in several protocol elements.
     *
     *     HEX            = "A" | "B" | "C" | "D" | "E" | "F"
     *                    | "a" | "b" | "c" | "d" | "e" | "f" | DIGIT
     */
    public static boolean isHex(int c)
    {
        return (
                (c == 'A') || (c == 'B') || (c == 'C') || (c == 'D') || (c == 'E') || (c == 'F') ||
                (c == 'a') || (c == 'b') || (c == 'c') || (c == 'd') || (c == 'e') || (c == 'f') ||
                Character.isDigit((char) c)
                );
    }

    public static String[] tokenizeHeaderValue(String v)
    {
        char[] chars = v.toCharArray();
        ArrayList list = new ArrayList();
        StringBuffer buf = new StringBuffer();
        boolean inToken = false;
        for (int i = 0; i < chars.length; i++)
        {
            int c = chars[i];
            if (!Bytes.isControl(c) && !Bytes.isSeparator(c))
            {
                inToken = true;
                buf.append((char)c);
            }
            else
            {
                if (inToken)
                {
                    list.add(buf.toString());
                    buf = new StringBuffer();
                }
                inToken = false;
            } // else
        } // for
        return (String[]) list.toArray(new String[list.size()]);
    }
}
