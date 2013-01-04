package org.yajul.util;

import org.yajul.collections.CollectionUtil;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Pattern;

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

    /**
     * Simplify the stack trace of the exception by filtering out
     * elements that match the pattern.
     * @param e the exception
     * @param pattern the filter pattern
     */
    public static void simplify(Throwable e, Pattern pattern)
    {
        if (e.getCause() != null) {
            simplify(e.getCause(),pattern);
        }

        StackTraceElement[] trace = e.getStackTrace();
        if (trace == null || trace.length == 0)
            return;

        List<StackTraceElement> simpleTrace =
                CollectionUtil.newArrayList(trace.length);

        simpleTrace.add(trace[0]);

        // Skip unnecessary stack trace elements.
        for (int i = 1; i < trace.length; i++) {
            if (pattern.matcher(trace[i].getClassName()).matches())
                continue;
            simpleTrace.add(trace[i]);
        }
        e.setStackTrace(
                simpleTrace.toArray(
                        new StackTraceElement[simpleTrace.size()]));
    }
}
