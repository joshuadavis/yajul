/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 14, 2002
 * Time: 5:22:50 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jdi;

import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.VMDeathEvent;

/**
 * Implements the JDIEventListener methods, ignoring everything.
 * @author Joshua Davis
 */
public class DefaultJDIEventListener implements JDIEventListener
{
    /** Invoked on method entry.
     * @param event The event
     */
    public void methodEntryEvent(MethodEntryEvent event)
    {
    }

    /** Invoked on method exit
     * @param event The event
     */
    public void methodExitEvent(MethodExitEvent event)
    {
    }

    /** Invoked on watchpoint hit.
     * @param event The event
     */
    public void fieldWatchEvent(ModificationWatchpointEvent event)
    {
    }

    /** Invoked on exception.
     * @param event The event
     */
    public void exceptionEvent(ExceptionEvent event)
    {
    }

    /** Invoked on program step.
     * @param event The event
     */
    public void stepEvent(StepEvent event)
    {
    }

    /** Invoked on thread death.
     * @param event The event
     */
    public void threadDeathEvent(ThreadDeathEvent event)
    {
    }

    /** Invoked when the JVM starts.
     * @param event The event
     */
    public void vmStartEvent(VMStartEvent event)
    {
    }

    /** Invoked when the debugger is disconnected.
     * @param event The event
     */
    public void vmDisconnectEvent(VMDisconnectEvent event)
    {
    }

    /** Invoked when a class is loaded.
     * @param event The event
     */
    public void classPrepareEvent(ClassPrepareEvent event)
    {
    }

    /** Invoked when the target JVM dies.
     * @param event The event
     */
    public void vmDeathEvent(VMDeathEvent event)
    {
    }

    /**
     * Invoked when the listener is added to a dispatcher.
     * @param dispatcher    The dispatcher that this listener has been added to.
     */
    public void addedToDispatcher(JDIEventDispatcher dispatcher)
    {
    }
}
