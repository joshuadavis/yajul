/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 14, 2002
 * Time: 5:17:51 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jdi;

import com.sun.jdi.Method;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import org.yajul.log.Logger;

/**
 * Represents the status of all of the threads in the JDI target VM.
 * @author Joshua Davis
 */
public class ThreadStatusMonitor extends DefaultJDIEventListener implements JDIEventListener
{
    private Logger log = Logger.getLogger(ThreadStatusMonitor.class);
    /**
     * Represents the status of a single thread in the JDI target VM.
     */
    class ThreadStatus
    {
        private ThreadReference thread;     // The thread (errr. it's reference, actually).
        private LinkedList active;                // The list of methods currently active.

        public ThreadStatus(ThreadReference thread)
        {
            this.thread = thread;
            active = new LinkedList();
        }

        public void push(Method method)
        {
            Method caller = (active.size() > 0) ? (Method)active.getFirst() : null;
            if (caller == null && log.isDebugEnabled())
                log.debug("New root caller: " + method.declaringType().name() + "." + method.name());

            active.addFirst(method);
            // If there is a call graph, tell it about the new call.
            if (callGraph != null)
            {
                callGraph.addCall(caller,method);
            }
        }

        public void pop(Method method)
        {
            if (active.size() > 0)
            {
                Method m = (Method)active.removeFirst();
                if (!method.name().equals(m.name()))
                    throw new IllegalStateException("Got " + method.toString() + ", expected " + m.toString());
                active.remove(method);
            }
        }
    }

    private Map statusMap;                  // Map of (ThreadReference,ThreadStatus)
    private CallGraph callGraph;            // The call graph (may be null)
    private String  contextClass;           // Class name to start the graph from (may be null)
    private String  packageName;            // Package name filter (may be null).
    private JDIEventDispatcher dispatcher;  // The dipatcher that is feeding events to this class.

    /**
     * Creates a new thread status monitor, with an optional call graph that will
     * be maintained by this thread status monitor.
     * @param callGraph     CallGraph that will be maintained by this monitor, (can be null)
     */
    public ThreadStatusMonitor(CallGraph callGraph)
    {
        this.statusMap = new HashMap();
        this.callGraph = callGraph;
    }

    public void setIncludeFilter(String includePackage)
    {
        this.packageName = includePackage;
    }

    /**
     * Invoked when the listener is added to a dispatcher.
     * @param dispatcher    The dispatcher that this listener has been added to.
     */
    public void addedToDispatcher(JDIEventDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    /** Invoked on method entry.
     * @param event The event
     */
    public void methodEntryEvent(MethodEntryEvent event)
    {
        ReferenceType mclass = event.method().declaringType();
        if (ignore(mclass))
            return;

//        if (log.isDebugEnabled())
//            log.debug("ENTRY : " + event.thread().name()
//                    + " - " + event.method().declaringType().name() + "."
//                    + event.method().name());

        // Find the thread status
        ThreadStatus status = (ThreadStatus) statusMap.get(event.thread());
        if (status == null)
        {
            if (log.isDebugEnabled())
                log.debug("New thread: " + event.thread());
            status = new ThreadStatus(event.thread());

            statusMap.put(event.thread(), status);
        }
        // Push the method info on the stack.
        status.push(event.method());
    }

    private boolean ignore(ReferenceType mclass)
    {
        boolean ignore;
        // If the method class is not in the include package, then ignore it.
        if (packageName != null && (!mclass.name().startsWith(packageName)))
            ignore = true;
        else
            ignore = false;
        return ignore;
//        return false;
    }

    /** Invoked on method exit
     * @param event The event
     */
    public void methodExitEvent(MethodExitEvent event)
    {
        ReferenceType mclass = event.method().declaringType();
        if (ignore(mclass))
            return;

//        if (log.isDebugEnabled())
//            log.debug("EXIT  : " + event.method().declaringType().name() + "." + event.method().name());

        // Find the thread status
        ThreadStatus status = (ThreadStatus) statusMap.get(event.thread());
        if (status == null)
            throw new IllegalStateException("Unknown thread in MethodExitEvent: " + event.thread());

        // Pop the method info off of the stack.
        status.pop(event.method());
    }

    public CallGraph getCallGraph()
    {
        return callGraph;
    }

}
