package org.yajul.fix.message;

import org.yajul.fix.util.Bytes;
import org.yajul.fix.dictionary.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.HashSet;

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
    private Dictionary.FieldList fieldList;
    private Set<Integer> requiredTags;

    public MessageParser(byte separator, byte tagsep, TagList tagList, Dictionary dictionary) {
        this.separator = separator;
        this.tagsep = tagsep;
        this.tagList = tagList;
        this.dictionary = dictionary;
    }

    public void parse(byte[] bytes) {
        // If we're using a dictionary, set the current field list to the header.
        if (dictionary != null) {
            setFieldList(dictionary.getHeader());
        }
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

    private void setFieldList(Dictionary.FieldList fieldList) {
        // If there was a previous field list, check for required values.
        if (requiredTags != null && this.fieldList != null) {
            if (requiredTags.size() > 0) {
                log.warn(this.fieldList.getName() + " missing required tags: " + requiredTags);
            }
        }

        this.fieldList = fieldList;
        requiredTags = new HashSet<Integer>(fieldList.getRequiredTags());
    }

    private void addTag(byte[] bytes, int tagStart, int tagEnd, int valueStart, int valueEnd) {
        byte[] tagBytes = Bytes.copy(bytes, tagStart, tagEnd);
        byte[] valueBytes = Bytes.copy(bytes, valueStart, valueEnd);
        int tag = Bytes.parseDigits(bytes, tagStart, tagEnd);
        // Remove this tag from the required list.
        if (requiredTags != null)
            requiredTags.remove(tag);
        if (fieldList != null) {
            Dictionary.Field f = fieldList.find(tag);

        }
        tagList.add(tag, tagBytes, valueBytes);
    }

    public interface TagList {
        void initialize(byte separator, byte tagsep, int initialSize);

        void add(int tag, byte[] tagBytes, byte[] valueBytes);
    }
}
