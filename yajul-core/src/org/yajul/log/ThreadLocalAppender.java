package org.yajul.log;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Enumeration;

/**
 * A log4j appender that can 'tee' log messages to a thread local appender if one exists.
 * This can be used to keep a single log file for a particular thread, or it can be used
 * by a server side framework to route log messages for a particular user or session that
 * might execute on different servers in a load balanced cluster.
 * <br>
 * Usage:
 * <ul>
 * <li>Associate an instance of ThreadLocalAppender with a Log4J Logger, for example the root logger.</li>
 * <li>During the thread's execution (e.g. the start of a run() method), create a new appender (e.g. a FileAppender) and associate it
 * with the current thread with ThreadLocalAppender.getAppenderAttachable().addAppender(newappender).</li>
 * <li>When the thread is about to be assigned to some other work (e.g. the end of a run() method), remove the appender using
 * ThreadLocalAppender.getAppenderAttachable().removeAppender(newappender).</li>
 * </ul>
 * <br>
 * User: jdavis
 * Date: Sep 16, 2006
 * Time: 1:07:49 PM
 */
public class ThreadLocalAppender extends AppenderSkeleton implements AppenderAttachable
{
    /**
     * The chain of other appenders that this one is attached to.
     */
    private final AppenderAttachableImpl aai = new AppenderAttachableImpl();
    /**
     * Stores an appender attachable impl for each thread, allocated
     * on demand.
     */
    private static ThreadLocal localAai = new ThreadLocal();

    /**
     * Use the AppenderAttachable that this returns to
     * associate/remove appenders on the caller's thread.
     *
     * @return An AppenderAttachable for the current thread.
     */
    public static AppenderAttachable getLocalAppenderAttachable()
    {
        return getLocalAAI();
    }

    private static AppenderAttachableImpl getLocalAAI()
    {
        AppenderAttachableImpl rv = (AppenderAttachableImpl) localAai.get();
        if (rv == null)
        {
            rv = new AppenderAttachableImpl();
            localAai.set(rv);
        }
        return rv;
    }

    protected void append(LoggingEvent loggingEvent)
    {
        // If there is a thread local appender, send the event there first.
        getLocalAAI().appendLoopOnAppenders(loggingEvent);
        synchronized (aai)
        {
            aai.appendLoopOnAppenders(loggingEvent);
        }
    }

    public boolean requiresLayout()
    {
        return false;
    }

    public void close()
    {
    }

    public void addAppender(Appender newAppender)
    {
        synchronized (aai)
        {
            aai.addAppender(newAppender);
        }
    }

    public Enumeration getAllAppenders()
    {
        synchronized (aai)
        {
            return aai.getAllAppenders();
        }
    }

    public Appender getAppender(String name)
    {
        synchronized (aai)
        {
            return aai.getAppender(name);
        }
    }

    public boolean isAttached(Appender appender)
    {
        synchronized (aai)
        {
            return aai.isAttached(appender);
        }
    }

    public void removeAllAppenders()
    {
        synchronized (aai)
        {
            aai.removeAllAppenders();
        }
    }

    public void removeAppender(Appender appender)
    {
        synchronized (aai)
        {
            aai.removeAppender(appender);
        }
    }

    public void removeAppender(String name)
    {
        synchronized (aai)
        {
            aai.removeAppender(name);
        }
    }
}
