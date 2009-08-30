package org.yajul.fix.util;

import java.text.DecimalFormat;

/**
 * Formatting helper functions.
 * <br>
 * User: josh
 * Date: Jul 22, 2009
 * Time: 8:12:15 AM
 */
public class Formatter {
    private static DecimalFormat checksumFormat = new DecimalFormat("000");

    public static void indent(int level,StringBuilder sb) {
        for (int i = 0 ; i < level ; i++) {
            sb.append('\t');
        }
    }
}
