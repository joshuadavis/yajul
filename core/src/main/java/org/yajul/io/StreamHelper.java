package org.yajul.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Helper methods for InputStream/OutputStream.
 * <br>
 * User: josh
 * Date: 6/29/11
 * Time: 2:32 PM
 */
public class StreamHelper {
    private static final Logger log = LoggerFactory.getLogger(StreamHelper.class);

    public static void closeNoThrow(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (IOException ioe) {
            log.warn("Unable to close: " + ioe,ioe);
        }
    }
}
