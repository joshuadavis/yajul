/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 14, 2002
 * Time: 2:06:25 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.yajul.jdi;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import org.yajul.log.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JDIEventDispatcher implements Runnable, JDIEventListener
{
    private VirtualMachine vm;          // Running VM
    private boolean connected = true;   // Connected to VM
    private boolean vmDied = true;      // VMDeath occurred
    private Set listeners;              // The set of listeners.

    private static Logger log = Logger.getLogger(JDIEventDispatcher.class);

    /**
     * Creates a new dispatcher.
     * @param   vm      The JVM being monitored.
     */
    public JDIEventDispatcher(VirtualMachine vm)
    {
        this.vm = vm;
        listeners = new HashSet();
    }

    /**
     * Adds a listener to the set.  The listener will be notified when any
     * messages get received from the JDI target JVM.
     * @param listener      A new listener.
     */
    public void addListener(JDIEventListener listener)
    {
        listeners.add(listener);
        listener.addedToDispatcher(this);
    }

    /**
     * Invoked when the listener is added to a dispatcher.
     * @param dispatcher    The dispatcher that this listener has been added to.
     */
    public void addedToDispatcher(JDIEventDispatcher dispatcher)
    {
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     */
    public void run()
    {
        log.info("run() : ENTER");
        EventQueue queue = vm.eventQueue();
        while (connected)
        {
            try
            {
//                log.debug("Waiting for an EventSet...");
                // This is *supposed* to wait forever, but I don't think it always does. [jsd]
                EventSet eventSet = queue.remove();
                EventIterator it = eventSet.eventIterator();
                // Dispatch all events and then resume all target threads.
                while (it.hasNext())
                    handleEvent(it.nextEvent());
//                log.debug("Resume threads in target (if any were suspended)...");
                eventSet.resume();
                Thread.yield(); // Yield to other threads after processing some events?
            }
            catch (InterruptedException exc)
            {
                // Ignore
            }
            catch (VMDisconnectedException discExc)
            {
                handleDisconnectedException();
                break;
            }
            catch (Exception e)
            {
                // Something else happened?  Log and continue.
                log.unexpected(e);
            }
        }
        log.info("run() : EXIT");
    }

    /**
     * Dispatch incoming events
     */
    private void handleEvent(Event event)
    {
//        if (log.isDebugEnabled())
//            log.debug("Dispatching: " + event);

        if (event instanceof ExceptionEvent)
        {
            exceptionEvent((ExceptionEvent) event);
        }
        else if (event instanceof ModificationWatchpointEvent)
        {
            fieldWatchEvent((ModificationWatchpointEvent) event);
        }
        else if (event instanceof MethodEntryEvent)
        {
            methodEntryEvent((MethodEntryEvent) event);
        }
        else if (event instanceof MethodExitEvent)
        {
            methodExitEvent((MethodExitEvent) event);
        }
        else if (event instanceof StepEvent)
        {
            stepEvent((StepEvent) event);
        }
        else if (event instanceof ThreadDeathEvent)
        {
            threadDeathEvent((ThreadDeathEvent) event);
        }
        else if (event instanceof ClassPrepareEvent)
        {
            classPrepareEvent((ClassPrepareEvent) event);
        }
        else if (event instanceof VMStartEvent)
        {
            vmStartEvent((VMStartEvent) event);
        }
        else if (event instanceof VMDeathEvent)
        {
            vmDeathEvent((VMDeathEvent) event);
        }
        else if (event instanceof VMDisconnectEvent)
        {
            vmDisconnectEvent((VMDisconnectEvent) event);
        }
        else
        {
            throw new Error("Unexpected event type");
        }
    }

    /***
     * A VMDisconnectedException has happened while dealing with
     * another event. We need to flush the event queue, dealing only
     * with exit events (VMDeath, VMDisconnect) so that we terminate
     * correctly.
     */
    synchronized void handleDisconnectedException()
    {
        log.info("handleDisconnectedException() : ENTER");
        EventQueue queue = vm.eventQueue();
        while (connected)
        {
            try
            {
                EventSet eventSet = queue.remove();
                EventIterator iter = eventSet.eventIterator();
                while (iter.hasNext())
                {
                    Event event = iter.nextEvent();
                    if (event instanceof VMDeathEvent)
                    {
                        vmDeathEvent((VMDeathEvent) event);
                    }
                    else if (event instanceof VMDisconnectEvent)
                    {
                        vmDisconnectEvent((VMDisconnectEvent) event);
                    }
                }
                eventSet.resume(); // Resume the VM
            }
            catch (InterruptedException exc)
            {
                // ignore
            }
        }
        log.info("handleDisconnectedException() : EXIT");
    }

    // --- JDIEventListener implementation ---

    /** JDIEventListener: Invoked on method entry.
     * @param event The event
     */
    public void methodEntryEvent(MethodEntryEvent event)
    {
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.methodEntryEvent(event);
        }
    }

    /** JDIEventListener: Invoked on method exit
     * @param event The event
     */
    public void methodExitEvent(MethodExitEvent event)
    {
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.methodExitEvent(event);
        }
    }

    /** JDIEventListener: Invoked on watchpoint hit.
     * @param event The event
     */
    public void fieldWatchEvent(ModificationWatchpointEvent event)
    {
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.fieldWatchEvent(event);
        }
    }

    /** JDIEventListener: Invoked on exception.
     * @param event The event
     */
    public void exceptionEvent(ExceptionEvent event)
    {
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.exceptionEvent(event);
        }
    }

    /** JDIEventListener: Invoked on program step.
     * @param event The event
     */
    public void stepEvent(StepEvent event)
    {
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.stepEvent(event);
        }
    }

    /** JDIEventListener: Invoked on thread death.
     * @param event The event
     */
    public void threadDeathEvent(ThreadDeathEvent event)
    {
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.threadDeathEvent(event);
        }
    }

    /** JDIEventListener: Invoked when the JVM starts.
     * @param event The event
     */
    public void vmStartEvent(VMStartEvent event)
    {
        log.info("--- The JVM has started ---");
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.vmStartEvent(event);
        }
    }

    /** JDIEventListener: Invoked when the debugger is disconnected.
     * @param event The event
     */
    public void vmDisconnectEvent(VMDisconnectEvent event)
    {
        connected = false;
        if (!vmDied)
        {
            log.info("-- The application has been disconnected --");
        }
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.vmDisconnectEvent(event);
        }
    }

    /** JDIEventListener: Invoked when a class is loaded.
     * @param event The event
     */
    public void classPrepareEvent(ClassPrepareEvent event)
    {
//        log.debug("PREPARE : " + event.referenceType().name());
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.classPrepareEvent(event);
        }

    }

    /** JDIEventListener: Invoked when the target JVM dies.
     * @param event The event
     */
    public void vmDeathEvent(VMDeathEvent event)
    {
        vmDied = true;
        log.info("-- The application exited --");
        // Broadcast the event to all listeners.
        Iterator i = listeners.iterator();
        JDIEventListener listener = null;
        while (i.hasNext())
        {
            listener = (JDIEventListener) i.next();
            listener.vmDeathEvent(event);
        }
    }

    public VirtualMachine getVm()
    {
        return vm;
    }

    public boolean isConnected()
    {
        return connected;
    }

    public boolean isVmDied()
    {
        return vmDied;
    }
}
