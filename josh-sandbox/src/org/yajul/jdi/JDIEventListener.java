/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 14, 2002
 * Time: 2:12:35 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jdi;

import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;

/**
 * Implementors handle specific JDI events.  The method invoked depends on the type of event
 * recieved from the JDI event queue.  JDIEventDispatcher is typically the only invoker of these
 * methods.
 * @author Joshua Davis
 */
public interface JDIEventListener
{
    /** Invoked on method entry.
     * @param event The event
     */
    void methodEntryEvent(MethodEntryEvent event);

    /** Invoked on method exit
     * @param event The event
     */
    void methodExitEvent(MethodExitEvent event);

    /** Invoked on watchpoint hit.
     * @param event The event
     */
    void fieldWatchEvent(ModificationWatchpointEvent event);

    /** Invoked on exception.
     * @param event The event
     */
    void exceptionEvent(ExceptionEvent event);

    /** Invoked on program step.
     * @param event The event
     */
    void stepEvent(StepEvent event);

    /** Invoked on thread death.
     * @param event The event
     */
    void threadDeathEvent(ThreadDeathEvent event);

    /** Invoked when the JVM starts.
     * @param event The event
     */
    void vmStartEvent(VMStartEvent event);

    /** Invoked when the debugger is disconnected.
     * @param event The event
     */
    void vmDisconnectEvent(VMDisconnectEvent event);

    /** Invoked when a class is loaded.
     * @param event The event
     */
    void classPrepareEvent(ClassPrepareEvent event);

    /** Invoked when the target JVM dies.
     * @param event The event
     */
    void vmDeathEvent(VMDeathEvent event);

    /**
     * Invoked when the listener is added to a dispatcher.
     * @param dispatcher    The dispatcher that this listener has been added to.
     */
    void addedToDispatcher(JDIEventDispatcher dispatcher);
}
