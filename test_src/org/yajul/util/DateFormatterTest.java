
package org.yajul.util;

// JDK
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;

// JUnit
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

/** 
 * A unit test for DateFormatter.
 * @author Joshua Davis
 **/
public class DateFormatterTest extends TestCase
{
    /** The GMT time zone **/
    private TimeZone gmt;
    /** The 'epoch' 1970-01-01 midnight. */
    private Date date1;
    /** A GMT calendar. **/
    private Calendar c;
    
    /** Creates a new test case.
     * @param name - The name of the test case.
     */
    public DateFormatterTest(String name)
    {
        super(name);
    }   

    /**
     * Sets up for a test.
     */
    protected void setUp()
    {
        gmt = TimeZone.getTimeZone("GMT");
        date1 = new Date(0);
        c = DateFormatter.getGMTCalendar();
    } 
    
    /** Test the formatting and parsing methods. **/
    public void testParseAndFormat()
    {
        // Test the basic timestamp and date formats.
        DateFormatter tzf = new DateFormatter(DateFormatter.ISO8601_UTC_FORMAT,"GMT");
        String s = tzf.format(date1);
        assertEquals("1970-01-01T12:00:00,000Z",s);
        try
        {
            Date d = tzf.parse(s);
            assertEquals(d,date1);
        }
        catch(java.text.ParseException pe)
        {
            fail("Failed due to: " + pe.getMessage());
        }


        DateFormatter df = new DateFormatter(DateFormatter.ISO8601_DATE_FORMAT,"GMT");
        s = df.format(date1);
        assertEquals("1970-01-01",s);
        try
        {
            Date d = df.parse(s);
            assertEquals(d,date1);
        }
        catch(java.text.ParseException pe)
        {
            fail("Failed due to: " + pe.getMessage());
        }
    }
}