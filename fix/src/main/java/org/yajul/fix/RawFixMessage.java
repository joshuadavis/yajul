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

    public RawFixMessage(byte[] bytes,
                         int beginStringStart, String beginString,
                         int bodyLengthStart, int bodyLength,
                         int bodyEnd,
                         int checksum) {
        this.bytes = bytes;
        this.beginStringStart = beginStringStart;
        this.beginString = beginString;
        this.bodyLengthStart = bodyLengthStart;
        this.bodyLength = bodyLength;
        this.bodyEnd = bodyEnd;
        this.checksum = checksum;
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
