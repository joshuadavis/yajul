package org.yajul.fix;

import java.io.Serializable;
import java.util.Arrays;

/**
 * An unparsed FIX message.
 * <br>User: Josh
 * Date: May 3, 2009
 * Time: 3:42:34 PM
 */
public class RawFixMessage implements Serializable {
    private byte[] bytes;
    private int beginStringStart;
    private String beginString;
    private int bodyLengthStart;
    private int bodyLength;
    private int bodyEnd;
    private int checksum;
    private byte separator;

    public RawFixMessage(byte[] bytes,
                         int beginStringStart, String beginString,
                         int bodyLengthStart, int bodyLength,
                         int bodyEnd,
                         int checksum,
                         byte separator) {
        this.bytes = bytes;
        this.beginStringStart = beginStringStart;
        this.beginString = beginString;
        this.bodyLengthStart = bodyLengthStart;
        this.bodyLength = bodyLength;
        this.bodyEnd = bodyEnd;
        this.checksum = checksum;
        this.separator = separator;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getBeginStringStart() {
        return beginStringStart;
    }

    public String getBeginString() {
        return beginString;
    }

    public int getBodyLengthStart() {
        return bodyLengthStart;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public int getBodyEnd() {
        return bodyEnd;
    }

    public int getChecksum() {
        return checksum;
    }

    public int computeChecksum() {
        int sum = 0;
        for (int i = 0; i <= bodyEnd; i++) {
            byte b = bytes[i];
            sum += b;
        }
        return sum % 256;
    }

    @Override
    public String toString() {
        return "RawFixMessage{" +
                "bytes=" + (bytes == null ? null : new String(bytes)) +
                ", beginStringStart=" + beginStringStart +
                ", beginString='" + beginString + '\'' +
                ", bodyLengthStart=" + bodyLengthStart +
                ", bodyLength=" + bodyLength +
                ", bodyEnd=" + bodyEnd +
                ", checksum=" + checksum +
                '}';
    }
}
