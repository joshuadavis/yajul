package org.yajul.fix;

import java.util.Date;

/**
 * FIX data types.
 * <br>
 * User: josh
 * Date: Jul 29, 2009
 * Time: 8:18:04 AM
 */
public enum ValueType {
    UNKNOWN(),
    STRING(String.class),
    CHAR(Character.class),
    PRICE(Double.class),
    INT(Integer.class),
    AMT(Double.class),
    QTY(Double.class),
    CURRENCY(String.class),
    MULTIPLEVALUESTRING(),
    EXCHANGE,
    UTCTIMESTAMP(Date.class),
    BOOLEAN(Boolean.class),
    LOCALMKTDATE(),
    DATA(),
    FLOAT(Double.class),
    PRICEOFFSET(Double.class),
    MONTHYEAR,
    DAYOFMONTH(Integer.class),
    UTCDATEONLY(Date.class),
    UTCDATE(Date.class),
    UTCTIMEONLY(Date.class),
    TIME(),
    NUMINGROUP(Integer.class),
    PERCENTAGE(Double.class),
    SEQNUM(Integer.class),
    LENGTH(Integer.class),
    COUNTRY()
    ;

    private Class<?> clazz;

    ValueType() {
    }

    ValueType(Class<?> clazz) {
        this.clazz = clazz;
    }
}
