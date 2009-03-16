package org.yajul.fix;

/**
 * FIX message types
 * <br>User: Josh
 * Date: Mar 15, 2009
 * Time: 9:03:28 AM
 */
public enum MessageType {
    LOGON('A',"Logon"),
    ;

    private char type;
    private String name;

    MessageType(char type, String name) {
        this.type = type;
        this.name = name;
    }

    public char getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
