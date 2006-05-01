
package org.yajul.net.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.yajul.io.Base64Encoder;
import org.yajul.util.NameValuePair;

/**
 * HTTPHeader represents a single HTTP header element, and can parse
 * and generate valid HTTP 1.1 headers.
 */
public class HTTPHeader extends NameValuePair implements HTTPConstants, HeaderConstants
{
    private String[] valueTokens;
    private String valueLower;

    /** Returns the header entry for basic HTTP authorization. */
    public static final String getAuthorizationValue(String username,String password)
    {
        String encoded = Base64Encoder.encode(username+BASIC_SEPARATOR+password);
        return BASIC_PREFIX+encoded;
    }

    /**
     * Returns true if the header is a 'standard' HTTP header.  The comparison
     * is case-insensitive.
     * @param name
     * @return
     */
    public static boolean isStandardHeader(String name)
    {
        return (
            name.equalsIgnoreCase(HOST)                 ||
            name.equalsIgnoreCase(ACCEPT)               ||
            name.equalsIgnoreCase(USER_AGENT)           ||
            name.equalsIgnoreCase(CONNECTION)           ||
            name.equalsIgnoreCase(CONTENT_TYPE)         ||
            name.equalsIgnoreCase(TRANSFER_ENCODING)    ||
            name.equalsIgnoreCase(CONTENT_LENGTH)       ||
            name.equalsIgnoreCase(PROXY_AUTH)     );
    }

    /** Default contstructor for HTTPHeader. */
    public HTTPHeader() {}

    /**
     * Creates an HTTP header from a Map entry (name value pair).
	 * @param e the entry to create the header from
	 */
	public HTTPHeader(Map.Entry e)
    {
        super(e);
    }

    /**
     * Create an HTTP header from the specified name value pair.
	 * @param name The name
	 * @param value The value
	 */
	public HTTPHeader(String name,String value)
    {
        super(name,value);
    }

    public String setValue(String v)
    {
        super.setValue(v);
        // Reset derived values.
        valueTokens = null;
        valueLower = null;
        return getValue();
    }

    public String[] getValueTokens()
    {
        if (valueTokens == null)
            valueTokens = Bytes.tokenizeHeaderValue(getValue());
        return valueTokens;
    }

    /**
	 * Overrides superclass method to print the name/value pair in 'HTTP' form.
	 * @return string representation of the header
	 */
	public String toString()
    {
        return getName() + ": " + getValue();
    }

    /**
	 * Writes the HTTP header to the output in HTTP form.
	 * @param out
	 * @exception java.io.IOException
	 */
	public void write(PrintWriter out)
        throws IOException
    {
        out.print(getName());
        out.print(": ");
        out.print(getValue());
    }

    public String getValueLowerCase()
    {
        if (valueLower == null && getValue() != null)
            valueLower = getValue().toLowerCase();
        return valueLower;
    }
}
