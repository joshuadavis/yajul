
package org.yajul.net.http;

/**
 * HTTPConstants constants.
 */
public interface HTTPConstants
{
    /**
     * Default IP port for HTTPConstants traffic.
     */
    public final static int DEFAULT_HTTP_PORT   = 80;
    
    /**
     * Default IP port for HTTPS traffic.
     */
    public final static int DEFAULT_HTTPS_PORT   = 443;

    /**
     * Char array constant that is CRLF.
     */
    public static final char[] CRLF = { '\r', '\n' };

    /**
     * Carriage return followed by line feed: CRLF.
     */
    public static final byte[] CRLF_BYTES = { '\r', '\n' };

    /**
     * Char array with space and horizontal tab in it.
     */
    public static final char[] SPHT = { ' ', '\t' };

    /**
     * Space and horizontal tab: SPHT.
     */
    public static final byte[] SPHT_BYTES = { ' ', '\t' };

    /**
     * Char array that is the current HTTPConstants version.
     */
    public static final char[] VERSION = { 'H', 'T', 'T', 'P', '/', '1', '.', '1' };
    
    /**
     * Space character (called SP in the RFC).
     */
    public static final char SP = ' ';
    
    /**
     * Server root document
     */
    public static final String SERVER_ROOT = "/";

    public static final String PROTOCOL_HTTPS = "https";

    public static final String METHOD_PUT = "PUT";

    public static final String METHOD_POST = "POST";

    public static final String METHOD_GET = "GET";
}