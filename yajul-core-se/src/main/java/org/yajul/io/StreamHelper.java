package org.yajul.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper methods for InputStream/OutputStream.
 * <br>
 * User: josh
 * Date: 6/29/11
 * Time: 2:32 PM
 */
public class StreamHelper {
    private static final Logger log = Logger.getLogger(StreamHelper.class.getName());

    public static void closeNoThrow(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (IOException ioe) {
            log.log(Level.WARNING,"Unable to close: " + ioe,ioe);
        }
    }
}
