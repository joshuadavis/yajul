package org.yajul.time;

import java.util.Calendar;

import org.yajul.enum.EnumType;
import org.yajul.enum.EnumTypeMap;

/**
 * TODO: Add javadoc
 * User: jdavis
 * Date: Nov 25, 2003
 * Time: 3:07:38 PM
 * @author jdavis
 */
public class TimeUnitEnum implements TimeUnit
{
    public static final EnumType ENUM_TYPE;

    static
    {
        EnumTypeMap map = null;
        map = EnumTypeMap.loadTypeMapFromResource("org/yajul/time/time-enums.xml");
        ENUM_TYPE = map.findEnumTypeById("TimeUnit");
    }

    public static int toCalendarUnit(int intervalUnit)
    {
        switch(intervalUnit)
        {
            case YEAR : return Calendar.YEAR;
            case MONTH : return Calendar.MONTH;
            case WEEK : return Calendar.WEEK_OF_YEAR;
            case DAY : return Calendar.DAY_OF_YEAR;
            case HOUR : return Calendar.HOUR;
            case MINUTE : return Calendar.MINUTE;
            case SECOND : return Calendar.SECOND;
            case MILLISECOND : return Calendar.MILLISECOND;
            default:
                throw new IllegalArgumentException("Unknown time unit: "
                        + intervalUnit);
        }
    }

    public static int xmlToCalendarUnit(String s)
    {
        int timeUnit = ENUM_TYPE.xmlToValueId(s);
        return toCalendarUnit(timeUnit);
    }
}
