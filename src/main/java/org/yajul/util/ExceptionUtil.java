package org.yajul.util;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Helper methods for exceptions.
 * <br>
 * User: josh
 * Date: Sep 24, 2007
 * Time: 11:27:13 AM
 */
public class ExceptionUtil {
    /**
     * Puts a stack trace into a string.
     * @param t the exception
     * @return a string representation of the stack trace
     */
    public static String getStackTraceAsString(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
