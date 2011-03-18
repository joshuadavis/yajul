package org.yajul.io;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.IOException;

/**
 * Implements InputStream in terms of an ObjectInput interface.
 * Useful in Externalizable implementations.
 * <br>
 * User: josh
 * Date: Sep 8, 2009
 * Time: 10:02:04 AM
 */
public class ObjectInputStreamAdapter extends InputStream {
    private ObjectInput input;

    public ObjectInputStreamAdapter(ObjectInput input) {
        this.input = input;
    }

    public int read() throws IOException {
        return input.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return input.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return input.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return input.skip(n);
    }

    @Override
    public int available() throws IOException {
        return input.available();
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
