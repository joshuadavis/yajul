/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 17, 2002
 * Time: 9:43:41 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jdi;


import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.runner.BaseTestRunner;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Enumeration;

class TestConsolePrinter implements TestListener
{
    PrintStream w;
    int column = 0;

    public TestConsolePrinter(PrintStream writer)
    {
        w = writer;
    }

    /*
    API for use by CoverageRunner
	 */

    synchronized void print(TestResult result, long runTime)
    {
        printHeader(runTime);
        printErrors(result);
        printFailures(result);
        printFooter(result);
    }

    /*
    Internal methods
	*/

    protected void printHeader(long runTime)
    {
        getWriter().println();
        getWriter().println("Time: " + elapsedTimeAsString(runTime));
    }

    protected void printErrors(TestResult result)
    {
        printDefects(result.errors(), result.errorCount(), "error");
    }

    protected void printFailures(TestResult result)
    {
        printDefects(result.failures(), result.failureCount(), "failure");
    }

    protected void printDefects(Enumeration booBoos, int count, String type)
    {
        if (count == 0) return;
        if (count == 1)
            getWriter().println("There was " + count + " " + type + ":");
        else
            getWriter().println("There were " + count + " " + type + "s:");
        for (int i = 1; booBoos.hasMoreElements(); i++)
        {
            printDefect((TestFailure) booBoos.nextElement(), i);
        }
    }

    public void printDefect(TestFailure failure, int count)
    { // only public for testing purposes
        printDefectHeader(failure, count);
        printDefectTrace(failure);
    }

    protected void printDefectHeader(TestFailure failure, int count)
    {
        // I feel like making this a println, then adding a line giving the throwable a chance to print something
        // before we get to the stack trace.
        getWriter().print(count + ") " + failure.failedTest());
    }

    protected void printDefectTrace(TestFailure failure)
    {
        getWriter().print(BaseTestRunner.getFilteredTrace(failure.thrownException()));
    }

    protected void printFooter(TestResult result)
    {
        if (result.wasSuccessful())
        {
            getWriter().println();
            getWriter().print("OK");
            getWriter().println(" (" + result.runCount() + " test" + (result.runCount() == 1 ? "": "s") + ")");

        }
        else
        {
            getWriter().println();
            getWriter().println("FAILURES!!!");
            getWriter().println("Tests run: " + result.runCount() +
                    ",  Failures: " + result.failureCount() +
                    ",  Errors: " + result.errorCount());
        }
        getWriter().println();
    }


    /**
     * Returns the formatted string of the elapsed time.
     * Duplicated from BaseTestRunner. Fix it.
     */
    protected String elapsedTimeAsString(long runTime)
    {
        return NumberFormat.getInstance().format((double) runTime / 1000);
    }

    public PrintStream getWriter()
    {
        return w;
    }

    /**
     * @see junit.framework.TestListener#addError(Test, Throwable)
     */
    public void addError(Test test, Throwable t)
    {
        getWriter().print("E");
    }

    /**
     * @see junit.framework.TestListener#addFailure(Test, AssertionFailedError)
     */
    public void addFailure(Test test, AssertionFailedError t)
    {
        getWriter().print("F");
    }

    /**
     * @see junit.framework.TestListener#endTest(Test)
     */
    public void endTest(Test test)
    {
//        printDot();
    }

    /**
     * @see junit.framework.TestListener#startTest(Test)
     */
    public void startTest(Test test)
    {
        printDot();
    }

    private void printDot()
    {
        getWriter().print(".");
        if (column++ >= 40)
        {
            getWriter().println();
            column = 0;
        }
    }


}
