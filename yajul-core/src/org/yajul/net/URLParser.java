package org.yajul.net;

import org.yajul.util.StringUtil;

import java.net.MalformedURLException;

/**
 * Parses protocol, host, port, and path information from URL strings.
 * This can be used as a substitute for java.net.URL, which is not currently
 * able to parse URLs that have 'unknown' protocols in them (like 't3').
 *
 * @author Kent Vogel
 * @author Joshua Davis
 * @version 1.0
 * @see java.net.URL
 * @since 10-21-2000
 */
public class URLParser
{
    /**
     * The string that separates the protocol from the rest of the URL.
     */
    public static final String PROTOCOL_SEPARATOR = "://";

    /**
     * The value returned when no port is specified in the URL.
     */
    public static final int NO_PORT = -1;

    private static int getStart(String url)
    {
        int start = findHost(url);
        if (start == -1)
        //  '://' was not found, use the start of the string
            start = 0;
        return start;
    }

    /**
     * Returns the index of the protocol separator, or -1.
     *
     * @param url The URL to parse.
     * @return The index of the protocol separator.
     */
    public static int findHost(String url)
    {
        int start = url.indexOf(PROTOCOL_SEPARATOR) + PROTOCOL_SEPARATOR.length();
        if (start == 2)
            return -1;
        else
            return start;
    }

    /**
     * Returns the protocol string from the URL.
     *
     * @param url The URL to parse.
     * @return The protocol string
     * @throws MalformedURLException if the url has no protocol spec.
     */
    public static String parseProtocol(String url)
            throws MalformedURLException
    {
        return parseProtocol(url, true);
    }

    /**
     * Returns the protocol string from the URL.
     *
     * @param url      The URL to parse.
     * @param required True if the protocol component is required.
     * @return The protocol string
     * @throws MalformedURLException if the url has no protocol spec *and* requried is set to true.
     */
    public static String parseProtocol(String url, boolean required)
            throws MalformedURLException
    {
        int end = url.indexOf(PROTOCOL_SEPARATOR);
        if (required && end == -1)
            throw new MalformedURLException("No protocol specified in " + url);
        int start = 0;
        return (end == -1) ? null : url.substring(start, end);
    }

    /**
     * Returns the port number from the URL.  If no port is specified, the
     * return value will be NO_PORT (-1).
     *
     * @param url The URL to parse.
     * @return The port number, or NO_PORT (-1)
     */
    public static int parsePort(String url)
            throws MalformedURLException
    {
        int result;
        int start = getStart(url);

        start = url.indexOf(':', start);
        if (start == -1)    // There is no port.
            return NO_PORT;

        int end = url.indexOf('/', start);
        try
        {
            if (end == -1)
                result = Integer.parseInt(url.substring(start + 1));
            else
                result = Integer.parseInt(url.substring(start + 1, end));
        }
        catch (NumberFormatException nfe)
        {
            throw new MalformedURLException("Port number is invalid in " + url);
        }
        return result;
    }

    /**
     * Returns the host in the URL.
     *
     * @param url The URL to parse.
     * @return The host part of the URL
     */
    public static String parseHost(String url)
    {
        int start = findHost(url);
        if (start == -1)
            return null;

        int end = url.indexOf(':', start);
        if (end == -1)
        {
            end = url.indexOf('/', start);
            if (end == -1)
            {
                end = url.length();
            }
        }

        return url.substring(start, end);
    }

    /**
     * Returns the file path of the URL, including any parameters.
     *
     * @param url The URL to parse.
     * @return The file path part of the URL
     */
    public static String parseFile(String url)
    {
        String result;
        int start = findHost(url);
        // If there was a protocol separator, then skip past the host part.
        if (start > 0)
        {
            start = url.indexOf('/', start + 1);
            if (start == -1)
                result = "";
            else
                result = url.substring(start);
        }
        // If there wasn't a protocol separator, then assume the URL is a relative file url.
        else
        {
            // The protocol separator was not found.
            result = url;
        }
        return result;
    }

    public static void parse(String str, GenericURL url)
            throws MalformedURLException
    {
        url.setProtocol(parseProtocol(str));
        url.setHost(parseHost(str));
        url.setPort(parsePort(str));
        url.setFile(parseFile(str));
    }

    public static void parseRelative(String str, GenericURL url)
            throws MalformedURLException
    {
        url.setProtocol(parseProtocol(str, false));
        url.setHost(parseHost(str));
        if (StringUtil.isEmpty(url.getProtocol()))
            url.setPort(NO_PORT);
        else
            url.setPort(parsePort(str));
        url.setFile(parseFile(str));
    }

}