package org.yajul.enum;

/**
 * Filters are simple predicates that determine whether a given
 * enumerated value qualifies for a sub-set.
 * User: jdavis
 * Date: Nov 18, 2003
 * Time: 12:30:13 PM
 * @author jdavis
 */
public interface EnumValueFilter
{
    /**
     * Returns true if the enum value is part of the filtered sub-set.
     * @param value The enumerated value to test.
     * @return True if the value 'passes' the filter.
     */
    public boolean test(EnumValue value);
}
