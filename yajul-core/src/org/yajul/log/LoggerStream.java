package org.yajul.log;

import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Implements an OutputStream in terms of a Log4J Logger.   Useful for
 * setting the log writer in DriverManager, etc.
 * <pre>DriverManager.setLogWriter(new PrintWriter(new LoggerStream(log)));</pre>
 * <hr>
 * User: jdavis<br>
 * Date: Jul 27, 2004<br>
 * Time: 7:05:42 PM<br>
 * @author jdavis
 */
public class LoggerStream extends OutputStream
{
    private Logger logger;
    private StringBuffer buf;

    public LoggerStream(Logger logger)
    {
        this.logger = logger;
        buf = new StringBuffer();
    }

    public void write(int b) throws IOException
    {
        if (b == '\n' || b == '\r')
        {
            flush();
        }
        else
        {
            buf.append((char)b);
        }
    }

    public void flush() throws IOException
    {
        if (buf.length() > 0)
        {
            logger.info(buf.toString());
            buf.setLength(0);
        }
    }
}
