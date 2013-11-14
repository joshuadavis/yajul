package org.yajul.serialization;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ObjectOutput;

/**
 * Implements output stream, delegates to ObjectOutput.  Useful in Externalizable implementations.
 * <br>
 * User: josh
 * Date: Sep 8, 2009
 * Time: 9:53:22 AM
 */
public class ObjectOutputStreamAdapter extends OutputStream {
    private ObjectOutput output;

    public ObjectOutputStreamAdapter(ObjectOutput output) {
        this.output = output;
    }

    public void write(int b) throws IOException {
        output.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        output.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        output.write(b);
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void close() throws IOException {
        output.close();
    }
}
