package org.yajul.micro;

import org.picocontainer.monitors.AbstractComponentMonitor;
import org.picocontainer.monitors.DefaultComponentMonitor;
import org.picocontainer.ComponentMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Member;

/**
  * A {@link org.picocontainer.ComponentMonitor} which writes to an SLF4J {@link org.slf4j.Logger} instance.
  * The Logger instance can either be injected or, if not set, the {@link org.slf4j.LoggerFactory LoggerFactory}
  * will be used to retrieve it at every invocation of the monitor.
  *
  * @author Paul Hammant
  * @author Mauro Talevi
 *  @author Joshua Davis (SLF4J)
  * @version $Revision: $
 * <br>User: Joshua Davis
 * Date: Sep 11, 2007
 * Time: 7:58:51 AM
 */
public class Slf4jComponentMonitor extends AbstractComponentMonitor implements Serializable{
    private Logger logger;
    private final ComponentMonitor delegate;

    /**
     * Creates a Slf4jComponentMonitor with no Logger instance set.
     * The {@link org.slf4j.LoggerFactory LoggerFactory} will be used to retrieve the Logger instance
     * at every invocation of the monitor.
     */
    public Slf4jComponentMonitor() {
        delegate = new DefaultComponentMonitor();
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     */
    public Slf4jComponentMonitor(Class loggerClass) {
        this(loggerClass.getName());
    }

    /**
     * Creates an Slf4jComponentMonitor with a given Logger instance name. It uses the
     * {@link org.slf4j.LoggerFactory LoggerFactory} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     */
    public Slf4jComponentMonitor(String loggerName) {
        this(LoggerFactory.getLogger(loggerName));
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance
     *
     * @param logger the Logger to write to
     */
    public Slf4jComponentMonitor(Logger logger) {
        this();
        this.logger = logger;
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance class.
     * The class name is used to retrieve the Logger instance.
     *
     * @param loggerClass the class of the Logger
     * @param delegate the delegate
     */
    public Slf4jComponentMonitor(Class loggerClass, ComponentMonitor delegate) {
        this(loggerClass.getName(), delegate);
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance name. It uses the
     * {@link org.slf4j.LoggerFactory LoggerFactory} to create the Logger instance.
     *
     * @param loggerName the name of the Log
     * @param delegate the delegate
     */
    public Slf4jComponentMonitor(String loggerName, ComponentMonitor delegate) {
        this(LoggerFactory.getLogger(loggerName), delegate);
    }

    /**
     * Creates a Slf4jComponentMonitor with a given Logger instance
     *
     * @param logger the Logger to write to
     * @param delegate the delegate
     */
    public Slf4jComponentMonitor(Logger logger, ComponentMonitor delegate) {
        this(delegate);
        this.logger = logger;
    }

    public Slf4jComponentMonitor(ComponentMonitor delegate) {
        this.delegate = delegate;
    }

    public void instantiating(Constructor constructor) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INSTANTIATING, new Object[]{constructor}));
        }
        delegate.instantiating(constructor);
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] injected, long duration) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INSTANTIATED2, new Object[]{constructor, duration, instantiated, injected}));
        }
        delegate.instantiated(constructor, instantiated, injected, duration);
    }

    public void instantiated(Constructor constructor, long duration) {
        Logger logger = getLogger(constructor);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INSTANTIATED, new Object[]{constructor, duration}));
        }
        //noinspection deprecation
        delegate.instantiated(constructor, duration);
    }

    public void instantiationFailed(Constructor constructor, Exception cause) {
        Logger logger = getLogger(constructor);
        if (logger.isWarnEnabled()) {
            logger.warn(format(INSTANTIATION_FAILED, new Object[]{constructor, cause.getMessage()}), cause);
        }
        delegate.instantiationFailed(constructor, cause);
    }

    public void invoking(Method method, Object instance) {
        Logger logger = getLogger(method);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INVOKING, new Object[]{method, instance}));
        }
        delegate.invoking(method, instance);
    }

    public void invoked(Method method, Object instance, long duration) {
        Logger logger = getLogger(method);
        if (logger.isDebugEnabled()) {
            logger.debug(format(INVOKED, new Object[]{method, instance, duration}));
        }
        delegate.invoked(method, instance, duration);
    }

    public void invocationFailed(Method method, Object instance, Exception cause) {
        Logger logger = getLogger(method);
        if (logger.isWarnEnabled()) {
            logger.warn(format(INVOCATION_FAILED, new Object[]{method, instance, cause.getMessage()}), cause);
        }
        delegate.invocationFailed(method, instance, cause);
    }

    public void lifecycleInvocationFailed(Method method, Object instance, RuntimeException cause) {
        Logger logger = getLogger(method);
        if (logger.isWarnEnabled()) {
            logger.warn(format(LIFECYCLE_INVOCATION_FAILED, new Object[]{method, instance, cause.getMessage()}), cause);
        }
        delegate.lifecycleInvocationFailed(method, instance, cause);
    }

    protected Logger getLogger(Member member) {
        if ( logger != null ){
            return logger;
        }
        return LoggerFactory.getLogger(member.getDeclaringClass());
    }

}
