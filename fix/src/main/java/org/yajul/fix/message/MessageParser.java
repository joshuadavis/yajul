package org.yajul.fix.message;

import org.yajul.fix.util.Bytes;
import org.yajul.fix.dictionary.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

/**
 * General message parser loop.  Parses single FIX messages (doesn't handle fragmentation).
 * <br>
 * User: josh
 * Date: Jul 30, 2009
 * Time: 8:50:30 AM
 */
public class MessageParser {
    private static final Logger log = LoggerFactory.getLogger(MessageParser.class);

    private final byte separator;
    private final byte tagsep;
    private final TagList tagList;
    private final Dictionary dictionary;
    private static final int MESG_TYPE = 35;

    public MessageParser(byte separator, byte tagsep, TagList tagList, Dictionary dictionary) {
        this.separator = separator;
        this.tagsep = tagsep;
        this.tagList = tagList;
        this.dictionary = dictionary;
    }

    public void parse(byte[] bytes) {
        // The number of separators is approximately equal to the number of tags.
        int sepcount = Bytes.count(bytes, separator);
        tagList.initialize(separator, tagsep, sepcount);
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
                        valueStart = i + 1;
                    }
                    break;
                case 1:
                    if (b == separator) {
                        valueEnd = i;
                        state = 0;
                        addTag(bytes, tagStart, tagEnd, valueStart, valueEnd);
                        tagStart = i + 1;
                    }
                    break;
            }
        }


    }

    private void addTag(byte[] bytes, int tagStart, int tagEnd, int valueStart, int valueEnd) {
        byte[] tagBytes = Bytes.copy(bytes, tagStart, tagEnd);
        byte[] valueBytes = Bytes.copy(bytes, valueStart, valueEnd);
        int tag = Bytes.parseDigits(bytes, tagStart, tagEnd);
        // MessageType is special, it determines the body TagList.
        if (tag == MESG_TYPE && dictionary != null) {
            String msgType = new String(valueBytes);
            Dictionary.ElementList body = dictionary.findMessageType(msgType);
        }
        tagList.add(tag, tagBytes, valueBytes, null);
    }

    private void nextFieldList() {

    }

    public interface TagList {
        void initialize(byte separator, byte tagsep, int initialSize);

        void add(int tag, byte[] tagBytes, byte[] valueBytes, Dictionary.Field f);
    }
}
