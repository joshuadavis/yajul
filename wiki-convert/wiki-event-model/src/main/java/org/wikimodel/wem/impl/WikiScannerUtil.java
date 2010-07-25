package org.wikimodel.wem.impl;

/**
 * This class contains some utility methods used by scanners.
 * 
 * @author MikhailKotelnikov
 */
public class WikiScannerUtil {

    /**
     * Extracts and returns a substring of the given string starting from the
     * given open sequence and finishing by the specified close sequence. This
     * method unescapes all symbols prefixed by the given escape symbol.
     * 
     * @param str from this string the substring framed by the specified open
     *        and close sequence will be returned
     * @param open the start substring sequence
     * @param close the closing substring sequence
     * @param escape the escape symbol
     * @return a substring of the given string starting from the given open
     *         sequence and finishing by the specified close sequence
     */
    public static String extractSubstring(
        String str,
        String open,
        String close,
        char escape) {
        int i;
        StringBuffer buf = new StringBuffer();
        int len = str.length();
        for (i = 0; i < len; i++) {
            if (str.startsWith(open, i)) {
                i += open.length();
                break;
            }
        }
        boolean escaped = false;
        for (; i < len; i++) {
            if (escaped) {
                char ch = str.charAt(i);
                buf.append(ch);
                escaped = false;
            } else {
                if (str.startsWith(close, i))
                    break;
                char ch = str.charAt(i);
                escaped = ch == escape;
                if (!escaped) {
                    buf.append(ch);
                }
            }
        }
        return buf.toString();
    }

    /**
     * Unescapes the given string and returns the result.
     * 
     * @param str the string to unescape
     * @param escape the symbol used to escape characters
     * @return an unescaped string
     */
    public static String unescape(String str, char escape) {
        if (str == null)
            return "";
        StringBuffer buf = new StringBuffer();
        char[] array = str.toCharArray();
        boolean escaped = false;
        for (int i = 0; i < array.length; i++) {
            char ch = array[i];
            if (escaped) {
                buf.append(ch);
                escaped = false;
            } else {
                escaped = (ch == escape);
                if (!escaped)
                    buf.append(ch);
            }
        }
        return buf.toString();
    }
}
