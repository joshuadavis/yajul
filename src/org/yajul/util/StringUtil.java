
package org.yajul.util;

import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * Provides static methods that implement commonly used string functions.
 * @author Joshua Davis
 * @version 1.0
 */
public class StringUtil
{
    /**
     * Prints the class name of the object, followed by '@', followed by the hash code
     * of the object, just like java.lang.Object.toString().
     * @param o - The object to print.
     * @return String - The object's default string representation.
     */
    public static final String defaultToString(Object o)
    {
        StringBuffer buf = new StringBuffer();
        if (o == null)
            buf.append("null");
        else
        {
            buf.append(o.getClass().getName());
            buf.append("@");
            buf.append(Integer.toHexString(o.hashCode()));
        }
        return buf.toString();
    }

    /**
     * Left-pads a string with spaces out to the specified length and returns it.
     * @param s - The message to write to the log
     * @param length - The desired length of the padded string.
     * @param truncate - Set to true to truncate 's' to 'length' when s.length() > length
     * @return String - The padded (truncated) string.
     */
    public static final String padLeft(String s,int length,boolean truncate)
    {
        final String paddingChunk = "                    ";
        final int paddingChunkLength = paddingChunk.length();

        int spaceCount = length - s.length();

        // Is the string longer than the padded length?
        if (spaceCount <= 0)
        {
            if (truncate)
                return s.substring(0,length);
            else
                return s;
        }
        
        // Add the initial string.
        StringBuffer sb = new StringBuffer(s);
        
        // Append whole chunks.
        while (spaceCount >= paddingChunkLength)
        {
            sb.append(paddingChunk);
            spaceCount -= paddingChunkLength;
        }

        // Append the last partial chunk.
        if (spaceCount >= 0)
            sb.append(paddingChunk.substring(0,spaceCount));

        return sb.toString();
    }
    
    /** 
     * Returns true if the string is null or zero length.
     * @param str - The string to test.
     * @return boolean - True if the string is null or zero length.
     **/
    public static final boolean isEmpty(String str)
    {
        return (str == null || str.length() == 0);
    } 
    
    /**
     * Splits a string into an array of strings using the delimiters given.
     * @param str - The string to split.
     * @param delims - A string containing the delimiter characters.
     * @return String[] - An array of strings.
     * @see java.util.StringTokenizer
     */
    public static final String[] split(String str,String delims)
    {
        StringTokenizer st = new StringTokenizer(str,delims);
        ArrayList list = new ArrayList();
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken());
        } // while
        return (String[])list.toArray(new String[list.size()]);        
    }

    /**
     * Joins an array of strings using the given delimiter.
     * @param array - An array of strings.
     * @param delim - The delimiter (typicaly a single character, but anything can be used).
     * @return String - The joined strings, with the given delimeter in between each string in the array.
     */
    public static final String join(String[] array, String delim)
    {
        StringBuffer b = new StringBuffer();
        for(int i = 0; i < array.length ; i++)
        {
            if (i > 0)           // If this is not the first element,
                b.append(delim); // add a delimiter before adding the element.
            b.append(array[i]);
        } // for
        return b.toString();
    }

    /**
     * Replaces all the occurences of x in the buffer with y
     * @param buffer The string that is the subject of the  operation
     * @param x The string that is to be replaced by y in the buffe
     * @param y The string that will replace x in the buffer
     * @return String the buffer with x replaced by y
     */
    public static final String replace(String buffer, String x, String y)
    {
        return join(split(buffer, x), y);
    }

    /**
     * Compares two strings; if contents of both are the same OR if both are null, returns true
     * @param a string #1
     * @param b string #2
     * @return <code>true</code> if the two match
     */
    public static final boolean equals( String a, String b )
    {
        return ( a == null ? b == null : a.equals(b) );
    }
} // class StringUtil
