package org.yajul.micro;

import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.yajul.util.ReflectionUtil;

import java.lang.reflect.Method;

/**
 * Guice interceptor that invokes MethodWrapper callback methods in the target.
 * <br>
 * User: josh
 * Date: Jan 13, 2010
 * Time: 1:52:48 PM
 */
public class MethodWrapperInterceptor implements MethodInterceptor {

    private static final Matcher<Method> METHOD_MATCHER = new AbstractMatcher<Method>() {
        public boolean matches(Method m) {
            return !ReflectionUtil.isDefinedIn(m,MethodWrapper.class);
        }
    };

    /**
     * Use this to prevent recursion.
     * @return A Guice method matcher that filters out methods that belong to the MethodWrapper interface
     */
    public static Matcher<Method> notMethodWrapper() {
        return METHOD_MATCHER;
    }

    /**
     * Matches only methods that are defined in the specified class / interface.
     * @param clazz the class (usually an interface)
     * @return A matcher that filters out everything but methods in the class / interface.
     */
    public static Matcher<Method> onlyDefinedIn(final Class<?> clazz) {
        return new AbstractMatcher<Method>() {
            public boolean matches(Method method) {
                return ReflectionUtil.isDefinedIn(method,clazz);
            }
        };
    }
    
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Object target = invocation.getThis();
        final Method method = invocation.getMethod();
        final MethodWrapper methodWrapper = (target instanceof MethodWrapper) ? (MethodWrapper) target : null;

        try
        {
            if (methodWrapper != null)
                methodWrapper.beforeMethod(method);
            final Object rv = invocation.proceed();
            if (methodWrapper != null)
                methodWrapper.afterMethod(method, rv);
            return rv;
        }
        catch (Throwable t)
        {
            if (methodWrapper != null)
                methodWrapper.onException(method, t);
            throw t;
        }
    }
}
