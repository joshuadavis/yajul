/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 4, 2003
 * Time: 2:21:58 PM
 */
package org.yajul.util.test;

import junit.framework.TestCase;
import org.yajul.util.CommandLine;

/**
 * JUnit test case for CommandLine
 * @author josh
 */
public class CommandLineTest extends TestCase
{
    /**
     * Creates test case CommandLineTest
     * @param name The name of the test (method).
     */
    public CommandLineTest(String name)
    {
        super(name);
    }

    public void testSimpleCommandLine() throws Exception
    {
        String[] args = new String[] { "one" , "two", "three" };
        CommandLine cl = new CommandLine(args);
        assertEquals(3,cl.getArgumentCount());
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            assertEquals(arg,cl.get(i));
        }
    }

    public void testToggleOption()  throws Exception
    {
        String[] args = new String[] { "one" , "-opt",  "two", "three" };
        CommandLine cl = new CommandLine(args);
        cl.defineOption("opt");
        assertEquals(3,cl.getArgumentCount());
        assertEquals("one",cl.get(0));
        assertEquals("two",cl.get(1));
        assertEquals("three",cl.get(2));
        assertTrue(cl.isSpecified("opt"));
    }

}
