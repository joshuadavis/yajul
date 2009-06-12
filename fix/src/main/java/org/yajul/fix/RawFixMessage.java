package org.yajul.fix;

import org.yajul.fix.util.Bytes;
import org.yajul.fix.util.CodecConstants;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * An unparsed FIX message.
 * <br>User: Josh
 * Date: May 3, 2009
 * Time: 3:42:34 PM
 */
public class RawFixMessage implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(RawFixMessage.class);

    private List<RawTag> tags;
    private byte separator = CodecConstants.DEFAULT_SEPARATOR;
    private byte tagsep = CodecConstants.DEFAULT_TAG_SEPARATOR;
    private transient byte[] bytes;
    private RawTag beginString;
    private RawTag checkSum;
    private RawTag bodyLength;

    private void parseTags(byte[] bytes) {
        this.bytes = bytes;
        // The number of separators is approximately equal to the number of tags.
        int sepcount = Bytes.count(bytes, separator);
        tags = new ArrayList<RawTag>(sepcount);
        int tagStart = 0;
        int tagEnd = -1;
        int valueStart = -1;
        int valueEnd;
        int state = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            switch (state) {
                case 0:
                    if (b == tagsep) {
                        tagEnd = i;
                        state = 1;
                        valueStart = i+1;
                    }
                    break;
                case 1:
                    if (b == separator) {
                        valueEnd = i;
                        state = 0;
                        addTag(bytes, tagStart, tagEnd, valueStart, valueEnd);
                        tagStart = i+1;
                    }
                    break;
            }
        }
    }

    private void addTag(byte[] bytes, int tagStart, int tagEnd, int valueStart, int valueEnd) {
        RawTag tag = new RawTag(bytes, tagStart, tagEnd, valueStart, valueEnd);
        switch (tag.tag) {
            case CodecConstants.TAG_BEGINSTRING:
                beginString = tag;
                break;
            case CodecConstants.TAG_BODYLENGTH:
                bodyLength = tag;
                break;
            case CodecConstants.TAG_CHECKSUM:
                checkSum = tag;
                break;
        }
        tags.add(tag);
    }

    public RawFixMessage(byte[] bytes) {
        parseTags(bytes);
    }

    public RawFixMessage(byte[] bytes,
                         String beginString,
                         int bodyLength,
                         int bodyEnd,
                         int checksum,
                         byte separator) {
        parseTags(bytes);
//        if (log.isDebugEnabled())
//            log.debug("bodyEnd=" + bodyEnd + " getBodyEnd()=" + getBodyEnd());
        assert getBodyEnd() == bodyEnd;
        assert getBodyLength() == bodyLength;
        assert computeChecksum() == checksum;
        assert beginString.equals(getBeginString());
        this.separator = separator;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getBeginString() {
        return beginString.getStringValue();
    }

    public int getBodyLength() {
        return bodyLength.getIntValue();
    }

    public int getChecksum() {
        return checkSum.getIntValue();
    }

    public int getBodyEnd() {
        return beginString.length() + bodyLength.length() + getBodyLength() - 1;
    }

    public int computeChecksum() {
        return Bytes.checksum(bytes,0,getBodyEnd(), CodecConstants.CHECKSUM_MODULO);
    }

    @Override
    public String toString() {
        return "RawFixMessage{" +
                "bytes=" + (bytes == null ? null : new String(bytes)) +
                ", beginString='" + beginString + '\'' +
                ", bodyLength=" + bodyLength +
                ", checkSum=" + checkSum + " (" + computeChecksum() + ") " +
                '}';
    }

    public List<RawTag> getRawTags() {
        return tags;
    }

    /**
     * Tag / Value pair
     * <br>
     * User: josh
     * Date: Jun 10, 2009
     * Time: 9:09:05 AM
     */
    public class RawTag implements Serializable {
        private int tag;
        private byte[] value;

        public RawTag(byte[] bytes,int tagStart,int tagEnd, int valueStart, int valueEnd) {
            tag = Bytes.parseDigits(bytes,tagStart,tagEnd);
            value = new byte[valueEnd - valueStart];
            System.arraycopy(bytes,valueStart,value,0,value.length);
        }


        @Override
        public String toString() {
            return "RawTag{" +
                    "tag=" + tag +
                    ", value=" + (value == null ? null : new String(value)) +
                    '}';
        }

        public String getStringValue() {
            return new String(value);
        }

        public int getIntValue() {
            return Bytes.parseDigits(value,0,value.length);
        }

        public int length() {
            return Bytes.numdigits(tag) + value.length + 2;
        }
    }

}
