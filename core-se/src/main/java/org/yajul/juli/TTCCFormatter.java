package org.yajul.juli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * JULI Log Formatter similar to the Log4J TTCCLayout
 * TTCC = Timestamp, Thread, Category, Context
 *
 * NOTE: JULI DOES NOT CAPTURE THREAD NAMES!   It just makes integer IDs.   There is no map of Thread names.
 * <br>
 * User: josh
 * Date: 12/28/12
 * Time: 3:59 PM
 */
public class TTCCFormatter extends Formatter {

    public static final String LF = System.getProperty("line.separator");
    public static final String ISO_8601_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATETIME_FORMAT);

    @Override
    public String format(LogRecord record) {
        String timestamp = formatTimestamp(record.getMillis());
        String thread = Integer.toString(record.getThreadID());
        String category = record.getLoggerName();
        String contextMessage = record.getMessage();
        String level = record.getLevel().getName();
        return timestamp + " [" + thread + "] " + level + " " + category + " - " + contextMessage + LF;
    }

    private String formatTimestamp(long millis) {
        synchronized (dateFormat)
        {
            return dateFormat.format(new Date(millis));
        }
    }
}
