package org.yajul.fix;

import java.util.Arrays;

/**
 * Represents a raw, unvalidated FIX message.
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 5:32:07 PM
 */
public class RawFixMessage {
    private byte[] bytes;

    public RawFixMessage(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "RawFixMessage{" + (bytes == null ? null : new String(bytes)) + '}';
    }
}
