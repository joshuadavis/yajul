package org.yajul.fix.util;

import java.text.DecimalFormat;

/**
 * Formatting helper functions.
 * <br>
 * User: josh
 * Date: Jul 22, 2009
 * Time: 8:12:15 AM
 */
public class FormatHelper {
    public static final String CHECKSUM_PATTERN = "000";
    public static final String FOUR_DIGITS = "###0";

    public static void indent(int level,StringBuilder sb) {
        for (int i = 0 ; i < level ; i++) {
            sb.append('\t');
        }
    }
}
