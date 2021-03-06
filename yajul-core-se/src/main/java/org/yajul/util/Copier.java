package org.yajul.util;

import java.io.*;

/**
 * Provides stream and reader/writer copying functions.
 * <br>
 * User: jdavis
 * Date: Jan 28, 2004
 * Time: 5:53:42 PM
 *
 * @author jdavis
 */
public class Copier {
    private static int EOS = -1;
    /**
     * The default buffer size. *
     */
    public static int DEFAULT_BUFFER_SIZE = 1024;
    /**
     * Use this to indicate a non-length limited copy. *
     */
    public static int UNLIMITED = -1;

    /**
     * No callback constant.
     */
    public static final Callback NO_CALLBACK = null;

    public interface Callback {

        /**
         * The copying has started.
         */
        void startOfStream();

        /**
         * Progress callback for InputStream/OutputStream copy.
         * @param buf the current buffer about to be written.
         * @param length the number of bytes in the buffer
         * @param total  the total number of bytes so far
         * @return true if it's okay to keep going, false to stop the copy loop
         */
        boolean beforeWrite(byte[] buf, int length, int total);

        /**
         * Progress callback for Reader/Writer copy.
         * @param buf the current buffer about to be written.
         * @param length the number of bytes in the buffer
         * @param total  the total number of bytes so far
         * @return true if it's okay to keep going, false to stop the copy loop
         */
        boolean beforeWrite(char[] buf, int length, int total);

        /**
         * The end of the input stream was reached.
         * @param total the total number of bytes copied
         */
        void endOfStream(int total);
    }


    /**
     * Copies the input stream into the output stream in an efficient manner.
     * This version does not synchronize on the streams, so it is not safe
     * to use when the streams are being accessed by multiple threads.
     *
     * @param in    The input stream.
     * @param out   The output stream.  If this is null, the input will be
     *              discarded, similar to piping to /dev/null on UN*X.
     * @param bufsz The size of the buffer to use.
     * @param limit The number of bytes to copy, or UNLIMITED (-1) to copy
     *              until the end of the input stream.
     * @return int The number of bytes copied.
     * @throws java.io.IOException When the stream could not be copied.
     */
    public static int copy(InputStream in, OutputStream out, int bufsz, int limit) throws IOException {
        return copy(in,out,bufsz,limit,NO_CALLBACK);
    }

    /**
     * Copies the input stream into the output stream in an efficient manner.
     * This version does not synchronize on the streams, so it is not safe
     * to use when the streams are being accessed by multiple threads.
     *
     * @param in    The input stream.
     * @param out   The output stream.  If this is null, the input will be
     *              discarded, similar to piping to /dev/null on UN*X.
     * @param bufsz The size of the buffer to use.
     * @param limit The number of bytes to copy, or UNLIMITED (-1) to copy
     *              until the end of the input stream.
     * @param callback optional callback interface.
     * @return int The number of bytes copied.
     * @throws java.io.IOException When the stream could not be copied.
     */
    public static int copy(InputStream in, OutputStream out, int bufsz, int limit, Callback callback) throws IOException {
        if (bufsz <= 0)
            throw new IllegalArgumentException("Buffer size must be > 0");
        byte[] buf = new byte[bufsz];
        int bytesRead;
        int total = 0;
        int readLimit = bufsz;
        _start(callback);
        while (true) {
            // If a limit was specified, calculate the number of bytes
            // that should be read by the next read operation.
            if (limit > 0) {
                readLimit = limit - total;
                if (readLimit > bufsz)
                    readLimit = bufsz;
                else if (readLimit <= 0)
                    break;
            }
            bytesRead = in.read(buf, 0, readLimit);
            if (bytesRead == EOS)
                break;
            total += bytesRead;
            if (callback != null) {
                boolean keepGoing = callback.beforeWrite(buf, bytesRead, total);
                if (!keepGoing)
                    break;
            }
            if (out != null)
                out.write(buf, 0, bytesRead);
        } // while
        _end(callback, total);
        return total;
    }

    private static void _end(Callback callback, int total) {
        if (callback != null)
            callback.endOfStream(total);
    }

    private static void _start(Callback callback) {
        if (callback != null)
            callback.startOfStream();
    }

    /**
     * Copies the input stream (reader) into the output stream (writer) in an efficient manner.
     * This version does not synchronize on the streams, so it is not safe
     * to use when the streams are being accessed by multiple threads.
     *
     * @param in    The input reader
     * @param out   The output writer.  If this is null, the input will be
     *              discarded, similar to piping to /dev/null on UN*X.
     * @param bufsz The size of the buffer to use.
     * @param limit The number of bytes to copy, or UNLIMITED (-1) to copy
     *              until the end of the input stream.
     * @return int The number of bytes copied.
     * @throws java.io.IOException When the stream could not be copied.
     */
    public static int copy(Reader in, Writer out, int bufsz, int limit) throws IOException {
        return copy(in,out,bufsz,limit,NO_CALLBACK);
    }

    /**
     * Copies the input stream (reader) into the output stream (writer) in an efficient manner.
     * This version does not synchronize on the streams, so it is not safe
     * to use when the streams are being accessed by multiple threads.
     *
     * @param in    The input reader
     * @param out   The output writer.  If this is null, the input will be
     *              discarded, similar to piping to /dev/null on UN*X.
     * @param bufsz The size of the buffer to use.
     * @param limit The number of bytes to copy, or UNLIMITED (-1) to copy
     *              until the end of the input stream.
     * @param callback optional callback interface.
     * @return int The number of bytes copied.
     * @throws java.io.IOException When the stream could not be copied.
     */
    public static int copy(Reader in, Writer out, int bufsz, int limit,Callback callback) throws IOException {
        if (bufsz <= 0)
            throw new IllegalArgumentException("Buffer size must be > 0");
        char[] buf = new char[bufsz];
        int bytesRead;
        int total = 0;
        int readLimit = bufsz;
        _start(callback);
        while (true) {
            // If a limit was specified, calculate the number of bytes
            // that should be read by the next read operation.
            if (limit > 0) {
                readLimit = limit - total;
                if (readLimit > bufsz)
                    readLimit = bufsz;
                else if (readLimit <= 0)
                    break;
            }
            bytesRead = in.read(buf, 0, readLimit);
            if (bytesRead == EOS)
                break;
            total += bytesRead;
            if (callback != null) {
                boolean keepGoing = callback.beforeWrite(buf, bytesRead, total);
                if (!keepGoing)
                    break;
            }
            if (out != null)
                out.write(buf, 0, bytesRead);
        } // while
        _end(callback,total);
        return total;
    }

    /**
     * Reads the entire input stream into a byte array.
     *
     * @param in The input stream
     * @return The contents of the input stream as a byte array.
     * @throws IOException if something goes wrong while copying.
     */
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(in, baos, Copier.DEFAULT_BUFFER_SIZE, UNLIMITED, NO_CALLBACK);
        return baos.toByteArray();
    }
}
