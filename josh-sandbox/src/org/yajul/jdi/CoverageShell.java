/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 14, 2002
 * Time: 12:23:56 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jdi;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import org.yajul.log.Logger;
import org.yajul.util.StreamCopier;
import org.yajul.io.WriterOutputStream;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * A JUnit test runner that provides very simple method coverage analysis using JDI (Java Debug
 * Interface - Part of <a href="http://java.sun.com/products/jpda/">JPDA</a>.
 * @see http://java.sun.com/products/jpda
 * @see http://java.sun.com/j2se/1.4.1/docs/guide/jpda/
 */
public class CoverageShell
{
    private static Logger log = Logger.getLogger(CoverageShell.class);

    private static final Class JUNIT_RUNNER_CLASS = CoverageRunner.class;

    // Class patterns for which we don't want events
    private static final String[] DEFAULT_EXCLUDES =
            {
                "java.*",
                "javax.*",
                "sun.*",
                "com.sun.*",
                "junit.*"
            };

    private boolean defaultExcludes;    // Enables / disables the default filtering.
    private String[] excludes;      // Filters all JDI messages from these class name patterns.
    private String[] testClasses;   // The test classes (invokes target package methods).
    private String[] targetClasses; // The target classes
    private Thread eventThread;     // Thread that receives events from the JDI VM and dispatches them.
    private Thread outThread;       // Thread that reads stdout from the JDI VM and pumps it.
    private Thread errThread;       // Thread that reads stderr from the JDI VM and pumps it.

    /**
     * Creates a CoverageShell that will run multiple tests.
     * @param tests                 An array of class names that are JUnit tests.
     * @param targets               An array of class names that are the target classes for the tests (they should be 'covered' by the tests).
     * @param excludes              An array of class name exclusion patterns (for increased speed).
     * @param useDefaultExcludes    If true, the default exclusion patterns will be used in addition to those passed in here.
     * @param classpath             The classpath to use.
     */
    public CoverageShell(String[] tests,String[] targets,
                         String[] excludes,boolean useDefaultExcludes,String classpath)
    {
    }


    private void go() throws IOException, IllegalConnectorArgumentsException, VMStartException
    {
        String classpath = System.getProperty("java.class.path");
        String inputFileName = "coverage-input.xml";
        String outputFileName = "coverage-output.xml";
        String commandLine = JUNIT_RUNNER_CLASS.getName() + " " + inputFileName;

        // Get access to the VirtualMachineManager.  This object is a singleton that
        // provides access to JDI.
        VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
        log.debug("setUp() : Obtained VirtualMachineManager");

        VirtualMachine vm = launchJVM(vmm, commandLine, classpath);

        // Tell the VM which events we are interested in receiving.
        registerInterestInEvents(vm);

        // Create a dispatcher that will listen on the queue directly
        // and route all events to a list of 'listeners'.
        JDIEventDispatcher dispatcher = new JDIEventDispatcher(vm);

        // Create a model that will be updated based on the events
        // sent from the dispatcher.
        ThreadStatusMonitor threadModel = new ThreadStatusMonitor(new CallGraph());
        // threadModel.setIncludeFilter(this.targetPackage);
        dispatcher.addListener(threadModel);

        // Start the dispatcher on a thread.
        Thread eventThread = new Thread(dispatcher, "JDI event dispatcher");
        eventThread.start();
        log.info("JDIEventDispatcher started.");

        redirectOutput(vm);

        log.info("Resuming JVM...");
        vm.resume();
        log.info("VM Resumed.  Waiting for threads to join...");

        // Shutdown begins when event thread terminates
        try
        {
            eventThread.join();
            errThread.join();   // Make sure output is forwarded
            outThread.join();   // before we exit
        }
        catch (InterruptedException exc)
        {
            // we don't interrupt
        }

        // Dump the call graph
        CallGraph graph = threadModel.getCallGraph();

        Iterator iter = graph.iterator();
        CallGraph.MethodNode node = null;
        while (iter.hasNext())
        {
            node = (CallGraph.MethodNode) iter.next();
            log.debug(node.toString());
        }
    }

    private void redirectOutput(VirtualMachine vm)
    {
        Process process = vm.process();
        log.info("JVM process obtained.");

        // Copy target's output and error to our output and error.
        Runnable r = new StreamCopier(process.getErrorStream(), System.err);

        errThread = new Thread(
                new StreamCopier(
                        process.getErrorStream(),
                        new WriterOutputStream(log)),
                "error reader"
        );
        outThread = new Thread(
                new StreamCopier(
                        process.getInputStream(),
                        new WriterOutputStream(log)),
                "output reader"
        );
        errThread.start();
        outThread.start();
        log.info("Output and error stream threads started.");
    }

    private VirtualMachine launchJVM(VirtualMachineManager vmm, String commandLine, String classpath) throws IOException, IllegalConnectorArgumentsException, VMStartException
    {
        // Get a Connector that can launch the target test program.
        LaunchingConnector connector = vmm.defaultConnector();
        log.debug("setUp() : Obtained LaunchingConnector");

        // Get the default arguments.
        Map arguments = connector.defaultArguments();
        // Get the arguments for the 'main' method of the target program.
        Connector.Argument arg =
                (Connector.Argument) arguments.get("main");
        if (arg == null)
            throw new Error("Bad launching connector");
        // Pass the test class name as the argument to the JUnit runner.
        arg.setValue(commandLine);

        // If the classpath was not specified, pass through the parent's classpath.
        if (classpath == null)
            classpath = System.getProperty("java.class.path");
        arg = (Connector.Argument) arguments.get("options");
        if (arg == null)
            throw new Error("Bad launching connector");
        arg.setValue("-classpath " + classpath);

        // Make sure the JVM will start in 'halted' state!

        // Start the JVM!
        log.info("Launching JVM...");
        if (log.isDebugEnabled())
        {
            log.debug("java.class.path=" + classpath);
            log.debug(connector.name() + " - " + connector.description());
            Iterator iter = arguments.values().iterator();
            while (iter.hasNext())
            {
                arg = (Connector.Argument) iter.next();
                log.debug(arg + " : " + arg.description());
            } // while
        }
        VirtualMachine vm = connector.launch(arguments);
        log.info("JVM launched.");
        return vm;
    }

    private void registerInterestInEvents(VirtualMachine vm)
    {
        // Get the names of all of the classes in the filter package.

        // Set up all requests.
        EventRequestManager mgr = vm.eventRequestManager();

//        // want all exceptions
//        ExceptionRequest excReq = mgr.createExceptionRequest(null,
//                true, true);
//        // suspend so we can step
//        excReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
//        excReq.enable();
//
//        log.info("Exception request enabled (SUSPEND_ALL).");

        MethodEntryRequest menr = mgr.createMethodEntryRequest();
        String[] excludes = DEFAULT_EXCLUDES;
        for (int i = 0; i < excludes.length; ++i)
        {
            menr.addClassExclusionFilter(excludes[i]);
        }
        menr.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        menr.enable();

        log.info("Method entry request enabled.");

        MethodExitRequest mexr = mgr.createMethodExitRequest();
        for (int i = 0; i < excludes.length; ++i)
        {
            mexr.addClassExclusionFilter(excludes[i]);
        }
        mexr.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        mexr.enable();
        log.info("Method exit request enabled.");

//        ThreadDeathRequest tdr = mgr.createThreadDeathRequest();
//        // Make sure we sync on thread death
//        tdr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
//        tdr.enable();
//        log.info("Thread death request enabled (SUSPEND_ALL)");
//

        // NOTE: 2002-09-16 [jsd] If these events are not enabled, only
        // a few of the method entry / exit events are sent to the JDI client.
        // Don't know why... hmmm....

        ClassPrepareRequest cpr = mgr.createClassPrepareRequest();
        for (int i = 0; i < excludes.length; ++i)
        {
            cpr.addClassExclusionFilter(excludes[i]);
        }
        cpr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        cpr.enable();

        log.info("Class prepare request enabled (SUSPEND_ALL)");
    }

    /**
     * Print command line usage help
     */
    void usage()
    {
        System.err.println("Usage: java CoverageShell <test class>");
        System.err.println("<test class> is the program to perform coverage analysis on");
    }
}
