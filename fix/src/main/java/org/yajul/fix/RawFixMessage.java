package org.yajul.fix;

import org.yajul.fix.util.Bytes;
import org.yajul.fix.util.CodecConstants;
import org.yajul.fix.util.ByteCharSequence;
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

    // Important tags.
    private RawTag beginString;
    private RawTag bodyLength;
    private RawTag messageType;
    private RawTag checkSum;

    private void parseTags(byte[] bytes) {
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
        if (log.isDebugEnabled())
            log.debug("parseTags() : messageType= " + messageType + ", " + bytes.length + " bytes, " + tags.size() + " tags.");
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
            case CodecConstants.TAG_MSGTYPE:
                messageType = tag;
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
                         byte separator,
                         byte tagsep) {
        this.separator = separator;
        this.tagsep = tagsep;
        parseTags(bytes);
        assert getBodyEnd() == bodyEnd;
        assert getBodyLength() == bodyLength;
        assert computeChecksum() == checksum;
        assert beginString.equals(getBeginString());
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

    public RawTag getMessageType() {
        return messageType;
    }

    public MessageType getMessageTypeEnum() {
        if (messageType != null)
            return MessageType.valueFor(messageType.getValueBytes());
        else
            return MessageType.UNKNOWN;
    }

    public int computeChecksum() {
        // Use the tag list to compute the checksum.
        int sum = 0;
        for (RawTag tag : tags) {
            sum += tag.sum();
        }
        return sum % CodecConstants.CHECKSUM_MODULO;
    }

    @Override
    public String toString() {
        return "RawFixMessage{" +
                "beginString=" + beginString +
                ", bodyLength=" + bodyLength +
                ", checkSum=" + checkSum + " (" + computeChecksum() + ") " +
                ", messageType=" + messageType +
                '}';
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
        private byte[] tagBytes;
        private byte[] valueBytes;

        private RawTag(byte[] bytes,int tagStart,int tagEnd, int valueStart, int valueEnd) {
            this.tagBytes = Bytes.copy(bytes,tagStart,tagEnd);
            this.valueBytes = Bytes.copy(bytes,valueStart,valueEnd);
            tag = Bytes.parseDigits(bytes,tagStart,tagEnd);
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            append(sb);
            sb.append('}');
            return sb.toString();
        }

        public void append(StringBuilder sb) {
            Bytes.append(sb,tagBytes);
            sb.append((char)tagsep);
            Bytes.append(sb,valueBytes);
            sb.append((char)separator);
        }

        public byte[] getValueBytes() {
            return valueBytes;
        }

        public String getStringValue() {
            return new String(valueBytes);
        }

        public int getIntValue() {
            return Bytes.parseDigits(valueBytes,0,valueBytes.length);
        }

        public int length() {
            return tagBytes.length + valueBytes.length + 2;
        }

        public int sum() {
            return  Bytes.sum(tagBytes) + tagsep + Bytes.sum(valueBytes) + separator;
        }
    }

}
