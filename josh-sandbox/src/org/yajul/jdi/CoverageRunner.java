/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 15, 2002
 * Time: 9:49:02 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jdi;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.runner.BaseTestRunner;

import java.io.File;
import java.io.IOException;

import org.yajul.xml.DOMUtil;
import org.yajul.junit.LogSupressingSetup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CoverageShell uses this class inside the JDI JVM to execute the tests.  This class is responsible for
 * communicating the test results back to the CoverageShell (for now, via a temporary file).
 * @author Joshua Davis
 */
public class CoverageRunner extends BaseTestRunner
{
    public void addError(Test test, Throwable throwable)
    {
        // TODO: Write information about the error to the output file.
    }

    public void addFailure(Test test, AssertionFailedError error)
    {
        // TODO: Write information about the failure to the output file.
    }

    public void endTest(Test test)
    {
        // TODO: Figure out what this is supposed to do.
    }

    protected void runFailed(String s)
    {
        // TODO: Figure out what this is suposed to do.
    }

    public void startTest(Test test)
    {
        System.out.println("test = " + test.toString());
    }

    /**
     * Creates the TestResult to be used for the test run.
     */
    protected TestResult createTestResult()
    {
        return new TestResult();
    }

    public TestResult doRun(Test suite)
    {
        TestResult result = createTestResult();
        TestConsolePrinter printer = new TestConsolePrinter(System.out);
        result.addListener(printer);
        long startTime = System.currentTimeMillis();
        suite.run(result);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        printer.print(result, runTime);
        return result;
    }

    /**
     * Starts a test run. Analyzes the command line arguments
     * and runs the given test suite.
     */
    protected TestResult start(String args[]) throws Exception
    {

            // Turn off the dynamic class reloading.
            setLoading(false);

            // Open up the input file.
            File f = new File(args[0]);
            if (!f.exists())
                throw new IOException("Input file '" + args[0] + "' does not exist!");
            Document doc = DOMUtil.parseFile(args[0]);

            // Get all of the test file names, in order.   Add them all to a test suite.
            TestSuite suite = new TestSuite("CoverageRunner-Suite");
            Element[] elem = DOMUtil.getChildElements(doc,"test");
            String text = null;
            for(int i = 0 ; i < elem.length; i++)
            {
                text = DOMUtil.getChildText(elem[i]);
                suite.addTest(getTest(text));
            }

            return doRun(new LogSupressingSetup(suite));
    }

    public static void main(String[] args)
    {
        // args[0]  The input file name, with the names of the tests to run.
        // args[1]  The output file name, the results will be placed.
        try
        {
            CoverageRunner aTestRunner = new CoverageRunner();
            TestResult r = aTestRunner.start(args);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
