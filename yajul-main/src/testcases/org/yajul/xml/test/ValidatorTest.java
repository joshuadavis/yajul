/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 4, 2003
 * Time: 3:46:52 PM
 */
package org.yajul.xml.test;

import junit.framework.TestCase;

import org.yajul.xml.Validator;

/**
 * Tests the Validator class.
 * @author josh
 */
public class ValidatorTest extends TestCase
{
    /**
     * Creates test case ValidatorTest
     * @param name The name of the test (method).
     */
    public ValidatorTest(String name)
    {
        super(name);
    }

    public void testXMLValidator() throws Exception
    {
        assertTrue(Validator.validateFile(
                "./src/testcases/xmlvalidator-test1.xml",System.out));
        assertTrue( ! Validator.validateFile(
                "./src/testcases/xmlvalidator-test2.xml",System.out));
    }
}
