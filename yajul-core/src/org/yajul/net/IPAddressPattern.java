package org.yajul.net;

import java.util.StringTokenizer;

/**
 * Matches IP addresses against a pattern of the form:
 * <pre>
 *     token '.' token  '.' token '.' token
 * </pre>
 * Where <i>token</i> is either a number between 0 and 255, or '*'.
 * For example, this can be used to match only local, class-C network
 * addresses:
 * <pre>
 *     192.168.*.*
 * </pre>
 */
public class IPAddressPattern
{
    private final static int WILDCARD = 256;
    private int pattern[];

    /**
     * Creates a pattern from a string of the form:
     * <pre>
     *     token '.' token  '.' token '.' token
     * </pre>
     * Where <i>token</i> is either a number between 0 and 255, or '*'.
     * @param addressPattern
     */
    public IPAddressPattern(String addressPattern)
    {

        pattern = new int[4];
        StringTokenizer st = new StringTokenizer(addressPattern, ".");
        if (st.countTokens() != 4)
            throw new IllegalArgumentException("\""
                    + addressPattern
                    + "\" does not represent a valid IP address");
        for (int i = 0; i < 4; i++)
        {
            String next = st.nextToken();
            if ("*".equals(next))
                pattern[i] = WILDCARD;
            else
                pattern[i] = (byte) Integer.parseInt(next);
        }
    }

    /**
     * Returns true if the address matches the pattern.  False if not.
     * @param address The IP address as an array of bytes.
     * @return true if the address matches the pattern, false if not.
     */
    public boolean matches(byte address[])
    {
        for (int i = 0; i < 4; i++)
        {
            if (pattern[i] == WILDCARD) // wildcard
                continue;
            if (pattern[i] != address[i])
                return false;
        }
        return true;
    }
}
