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
package org.yajul.util;

/**
 * Defines useful date / time formatting constants:
 * <ul><li> <a href="http://www.w3.org/TR/NOTE-datetime">
 * ISO 8601</a> format strings.</li>
 * <li>Commonly used date format strings.<li>
 * </ul>
 * User: josh
 * Date: Nov 24, 2003
 * Time: 9:58:56 PM
 */
public interface DateFormatConstants {
    /**
     * ISO 8601 date format yyyy-MM-dd.
     * A formatting string for java.text.SimpleDateFormat that
     * will allow parsing and formatting of ISO 8601 date strings
     * with year, month, and day specified.
     */
    public final static String ISO8601_DATE_FORMAT =
            "yyyy-MM-dd";

    /**
     * Complete date plus hours, minutes, seconds and a decimal fraction of a
     * second.
     */
    public final static String ISO8601_DATETIME_FORMAT =
            "yyyy-MM-dd'T'HH:mm:ss.S";

    /**
     * A formatting string for java.text.SimpleDateFormat that
     * will allow parsing and formatting of ISO 8601 date strings
     * with all UTC fields specified.
     */
    public static final String ISO8601_UTC_FORMAT =
            "yyyy-MM-dd'T'hh:mm:ss,SSS'Z'";
}
