package org.yajul.io;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * This is a reader that provides simple character-based parsing.
 */
public class ParsingReader extends PushbackReader
{
    /**
     * Creates a new parsing reader with a pushback bufer of the size specified
     */
    public ParsingReader(Reader r, int bufferSize)
    {
        super(r, bufferSize);
    }

    /**
     * Creates a new parsing reader with a pushback bufer of the size specified.
     * The input stream is converted into a reader with InputStreamReader and
     * also buffered with a BufferedReader.
     */
    public ParsingReader(InputStream is, int bufferSize)
    {
        super(new BufferedReader(new InputStreamReader(is)), bufferSize);
    }

    /**
     * Reads the next char if it matches 'aChar'.
     */
    public int skipChar(int aChar)
            throws IOException
    {
        int c = read();
        if (c == aChar)
            return c;
        else
        {
            unread(c);
            return c;
        }
    }

    /**
     * Reads the sequence of characters in 'chars' from the stream.  If
     * the sequence does not match, all characters are pushed back into the stream.
     */
    public char[] matchChars(char[] chars)
            throws IOException
    {
        char[] read = new char[chars.length];
        int c,i;
        for (i = 0; i < chars.length; i++)
        {
            c = read();
            read[i] = (char) c;
            if (c != chars[i])
                break;
        }
        if (i != chars.length)      // If the entire sequence did not match...
        {
            unread(read, 0, i + 1);     // Put all of the chars back into the stream.
            read = null;            // ... return null.
        }
        return read;
    }

    /**
     * Reads characters that are / are not in the set of characters specified.  Returns
     * the characters read.  The 'match' parameter determines the type of matching:
     * <pre>
     *    true  - Input stream characters must be in 'chars' (matching mode).
     *            (like C <i>strspn()</i>)
     *    false - Input stream characters must <b>not</b> be in 'chars' (delimiter mode).
     *            (like C <i>strtok()</i>)
     * </pre>
     *
     * At the end of the operation, the stream (Reader) will be positioned at the first
     * character that did not meet the matching condition.
     *
     * @param chars The set of chars.
     * @param match If true, this will read until a char that is *not* in 'chars'.
     * If false, this will read until a char that is in 'chars'.
     */
    public char[] readCharset(char[] chars, boolean match)
            throws IOException
    {
        int c;
        if (chars.length == 0)
            return null;

        boolean found = true;
        char[] read = null;
        CharArrayWriter caw = new CharArrayWriter();

        while (true)
        {
            c = read();
            found = false;
            for (int i = 0; i < chars.length && !found; i++)
            {
                if (c == chars[i])
                    found = true;
            }
            if ((match && !found) || (!match && found))
            {
                read = caw.toCharArray();
                unread(c);
                break;
            }
            caw.write(c);
        } // while

        return read;
    }

    /**
     * Blocks the thread that calls this method until
     * there is input available.
     */
    public void waitUntilReady()
            throws IOException
    {
        int c = read();
        unread(c);
    }
}
