package org.yajul.fix;

/**
 * TODO: Add class level comments!
 * <br>
 * User: josh
 * Date: Jul 15, 2009
 * Time: 6:20:58 PM
 */
public enum MessageType {
    LOGON,
    UNKNOWN;

    public static MessageType valueFor(byte[] value) {
        if (value.length == 1) {
            switch (value[0]) {
                case 'A':
                    return LOGON;
            }
        }
        return UNKNOWN;
    }
}
