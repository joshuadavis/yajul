package org.yajul.log;

import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Proxy that logs all method calls to an object via SLF4J.
 * <br>
 * User: josh
 * Date: Aug 12, 2008
 * Time: 11:18:17 AM
 */
public class Log4JDebugProxy implements java.lang.reflect.InvocationHandler {
    private Object target;
    private Logger log;

    public Log4JDebugProxy(Object target) {
        this.target = target;
        log = Logger.getLogger(target.getClass());
    }

    public static <T> T create(Class<T> interfaceClass, Object target) {
        Log4JDebugProxy proxy = new Log4JDebugProxy(target);
        Class targetClass = target.getClass();
        Object proxyInstance = java.lang.reflect.Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                new Class[] { interfaceClass },
                proxy
        );
        return interfaceClass.cast(proxyInstance);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        long start = System.currentTimeMillis();
        try {
            if (log.isDebugEnabled())
                log.debug(method.getName() + "() : ENTER");

            result = method.invoke(target, args);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " +
                    e.getMessage());
        }
        finally {
            if (log.isDebugEnabled())
                log.debug(method.getName() + "() : LEAVE (" + (System.currentTimeMillis() - start) + "ms)");
        }
        return result;
    }
}
