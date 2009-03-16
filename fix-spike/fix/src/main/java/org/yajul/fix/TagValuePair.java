package org.yajul.fix;

/**
 * A fix tag and it's value
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 9:14:42 AM
 */
public class TagValuePair {
    private int tag;
    private byte[] rawValue;

    public TagValuePair(int tag, byte[] rawValue) {
        this.tag = tag;
        this.rawValue = rawValue;
    }

    public int getTag() {
        return tag;
    }

    public byte[] getRawValue() {
        return rawValue;
    }
}
