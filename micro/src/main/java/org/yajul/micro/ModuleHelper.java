package org.yajul.micro;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.util.ReflectionUtil;

import java.lang.reflect.Method;

/**
 * Helper methods for Guice modules.
 * <br>
 * User: josh
 * Date: Jan 14, 2010
 * Time: 11:51:08 AM
 */
public class ModuleHelper {
    private static final Logger log = LoggerFactory.getLogger(ModuleHelper.class);

    /**
     * Matches only methods that are defined in the specified class / interface.
     *
     * @param clazz the class (usually an interface)
     * @return A matcher that filters out everything but methods in the class / interface.
     */
    public static Matcher<Method> onlyDefinedIn(final Class<?> clazz) {
        return new AbstractMatcher<Method>() {
            public boolean matches(Method method) {
                return ReflectionUtil.isDefinedIn(method, clazz);
            }
        };
    }

    public static void bindModuleClassName(Binder binder, String className) {
        final Module module = ReflectionUtil.createInstanceNoThrow(className,Module.class);
        install(binder, module);
    }

    private static void install(Binder binder, Module module) {
        if (module != null)
        {
            log.info("Installing " + module);
            binder.install(module);
        }
    }

    public static boolean isModule(Class<?> impl) {
        return Module.class.isAssignableFrom(impl);
    }

    public static void bindModuleClass(Binder binder, Class<?> impl) {
        Module module = (Module) ReflectionUtil.createInstanceNoThrow(impl);
        install(binder, module);
    }

    public static void bindAndInjectInterceptor(Binder binder,
                                     MethodInterceptor interceptor, 
                                     Class<?> interfaceClass,
                                     Class<?> implementationClass) {
        // Intercept the interface methods only.
        binder.bindInterceptor(
                Matchers.identicalTo(implementationClass),
                ModuleHelper.onlyDefinedIn(interfaceClass),
                interceptor);
        // Inject the interceptor, once the injector is created.
        binder.requestInjection(interceptor);
    }
}
