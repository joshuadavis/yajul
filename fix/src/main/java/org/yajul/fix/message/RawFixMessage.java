package org.yajul.fix.message;

import org.yajul.fix.util.Bytes;
import org.yajul.fix.util.CodecConstants;
import org.yajul.fix.message.MessageParser;
import org.yajul.fix.message.MessageType;
import org.yajul.fix.dictionary.Dictionary;
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

    private List<RawTag> headerTags;
    private List<RawTag> footerTags;

    private class TagList implements MessageParser.TagList {

        public void initialize(byte separator, byte tagsep, int initialSize) {
            tags = new ArrayList<RawTag>(initialSize);
        }

        public void add(int tag, byte[] tagBytes, byte[] valueBytes, Dictionary.Field f) {
            RawTag t = new RawTag(tag,tagBytes,valueBytes);
            tags.add(t);
        }
    }

    private void parseTags(byte[] bytes) {
        MessageParser parser = new MessageParser(separator,tagsep,new TagList(),null);
        parser.parse(bytes);
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
/*
        assert getBodyEnd() == bodyEnd;
        assert getBodyLength() == bodyLength;
        assert computeChecksum() == checksum;
        assert beginString.equals(getBeginString());
*/
    }

/*    public String getBeginString() {
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
    }*/
    public MessageType getMessageTypeEnum() {
        return null;
    }

    public RawTag getMessageType() {
        return null;
    }

    public int computeChecksum() {
        // Use the tag list to compute the checksum.
        int sum = 0;
        for (RawTag tag : tags) {
            sum += tag.sum();
        }
        return sum % CodecConstants.CHECKSUM_MODULO;
    }
/*

    @Override
    public String toString() {
        return "RawFixMessage{" +
                "beginString=" + beginString +
                ", bodyLength=" + bodyLength +
                ", checkSum=" + checkSum + " (" + computeChecksum() + ") " +
                ", messageType=" + messageType +
                '}';
    }
*/

    public byte[] getBytes() {
        // TODO: Implement this.
        return new byte[0];
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

        public RawTag(int tag, byte[] tagBytes, byte[] valueBytes) {
            this.tag = tag;
            this.tagBytes = tagBytes;
            this.valueBytes = valueBytes;
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
