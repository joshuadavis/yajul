package org.yajul.micro;

import java.lang.reflect.Method;

/**
 * Invoked by the MethodWrapperInterceptor before and after each method call.
 * <br>
 * User: josh
 * Date: Jan 13, 2010
 * Time: 1:17:10 PM
 */
public interface MethodWrapper {
    void beforeMethod(Method method);

    void afterMethod(Method method, Object returnValue);

    void onException(Method method, Throwable t);
}
