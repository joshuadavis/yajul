package org.yajul.spi.test;

/**
 * Example implementation.
 * User: jdavis
 * Date: Oct 31, 2003
 * Time: 4:33:32 PM
 * @author jdavis
 */
public class ExampleSPIDefaultImpl extends ExampleSPI
{
    public String sayHello()
    {
        return "[default-impl] hello!";
    }
}
