package org.yajul.fix;

/**
 * Required FIX header tags.
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 8:57:56 AM
 */
public enum HeaderTag {
    BEGIN_STRING(8,"BeginString"),
    BODY_LENGTH(9,"BodyLength"),
    MSG_TYPE(35,"MsgType"),
    SENDER_COMPID(49,"SenderCompID"),
    TARGET_COMPID(56,"TargetCompID"),
    ;

    private int tagNumber;
    private String tagName;

    HeaderTag(int tagNumber, String tagName) {
        this.tagNumber = tagNumber;
        this.tagName = tagName;
    }

    public int getTagNumber() {
        return tagNumber;
    }

    public String getTagName() {
        return tagName;
    }
}
