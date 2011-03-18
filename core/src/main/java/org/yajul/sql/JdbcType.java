package org.yajul.sql;

import java.sql.Types;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * java.sql.Types information in an enum
 * <br>
 * User: josh
 * Date: Mar 9, 2010
 * Time: 9:08:10 AM
 */
public enum JdbcType {
    /*
    public static final int BIT = -7;
    public static final int TINYINT = -6;
    public static final int SMALLINT = 5;
    public static final int INTEGER = 4;
    public static final int BIGINT = -5;
    public static final int FLOAT = 6;
    public static final int REAL = 7;
    public static final int DOUBLE = 8;
    public static final int NUMERIC = 2;
    public static final int DECIMAL = 3;
    public static final int CHAR = 1;
    public static final int VARCHAR = 12;
    public static final int LONGVARCHAR = -1;
    public static final int DATE = 91;
    public static final int TIME = 92;
    public static final int TIMESTAMP = 93;
    public static final int BINARY = -2;
    public static final int VARBINARY = -3;
    public static final int LONGVARBINARY = -4;
    public static final int NULL = 0;
    public static final int OTHER = 1111;
    public static final int JAVA_OBJECT = 2000;
    public static final int DISTINCT = 2001;
    public static final int STRUCT = 2002;
    public static final int ARRAY = 2003;
    public static final int BLOB = 2004;
    public static final int CLOB = 2005;
    public static final int REF = 2006;
    public static final int DATALINK = 70;
    public static final int BOOLEAN = 16;
    public static final int ROWID = -8;
    public static final int NCHAR = -15;
    public static final int NVARCHAR = -9;
    public static final int LONGNVARCHAR = -16;
    public static final int NCLOB = 2011;
    public static final int SQLXML = 2009;
     */
    BIT(Types.BIT),
    TINYINT(Types.TINYINT),
    SMALLINT(Types.SMALLINT),
    INTEGER(Types.INTEGER),
    BIGINT(Types.BIGINT),
    FLOAT(Types.FLOAT),
    REAL(Types.REAL),
    DOUBLE(Types.DOUBLE),
    NUMERIC(Types.NUMERIC),
    DECIMAL(Types.DECIMAL),
    CHAR(Types.CHAR),
    VARCHAR(Types.VARCHAR),
    LONGVARCHAR(Types.LONGVARCHAR),
    DATE(Types.DATE),
    TIME(Types.TIME),
    TIMESTAMP(Types.TIMESTAMP),
    BINARY(Types.BINARY),
    VARBINARY(Types.VARBINARY),
    LONGVARBINARY(Types.LONGVARBINARY),
    NULL(Types.NULL),
    OTHER(Types.OTHER),
    JAVA_OBJECT(Types.JAVA_OBJECT),
    DISTINCT(Types.DISTINCT),
    STRUCT(Types.STRUCT),
    ARRAY(Types.ARRAY),
    BLOB(Types.BLOB),
    CLOB(Types.CLOB),
    REF(Types.REF),
    DATALINK(Types.DATALINK),
    BOOLEAN(Types.BOOLEAN),
    ROWID(Types.ROWID),
    NCHAR(Types.NCHAR),
    NVARCHAR(Types.NVARCHAR),
    LONGNVARCHAR(Types.LONGNVARCHAR),
    NCLOB(Types.NCLOB),
    SQLXML(Types.SQLXML)
    ;

    public static EnumSet<JdbcType> NEEDS_SIZE = EnumSet.of(
            CHAR,VARCHAR,LONGVARCHAR,
            NCHAR,NVARCHAR,LONGNVARCHAR,
            BINARY,VARBINARY,LONGVARBINARY,BLOB,
            CLOB,NCLOB);

    private static Map<Integer,JdbcType> TYPE_BY_KEY;
    static {
        TYPE_BY_KEY = new HashMap<Integer,JdbcType>(JdbcType.values().length);
        for (JdbcType jdbcType : JdbcType.values()) {
            TYPE_BY_KEY.put(jdbcType.type,jdbcType);
        }
    }

    public static JdbcType get(int t) {
        return TYPE_BY_KEY.get(t);
    }
        
    private int type;

    JdbcType(int type) {
        this.type = type;
    }

}
