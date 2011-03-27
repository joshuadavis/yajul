package org.yajul.scannermodule;

import com.google.inject.*;
import com.google.inject.util.Types;
import com.sun.activation.registries.LogSupport;
import javassist.bytecode.ClassFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Actually does the work of scanning and binding.
 * <br>
 * User: Josh
 * Date: 3/27/11
 * Time: 5:33 PM
 */
class BindingScanner extends AbstractAnnotationScanner {
    private static final Logger log = LoggerFactory.getLogger(BindingScanner.class);

    private Binder binder;

    public BindingScanner(Binder binder, ClassLoader classLoader) {
        super(ScannerModule.RESOURCE_NAME, classLoader);
        this.binder = binder;
        addAnnotation(Bind.class);
    }

    @Override
    protected void handleAnnotation(String name, ClassFile classFile, Class<? extends Annotation> annotation) {
        final String implClassName = classFile.getName();
        log.info("name=" + name + " " + implClassName + "  annotation=" + annotation);
        try {
            // Load the implementation class.
            Class<?> implClass = classLoader.loadClass(implClassName);
            // Choose a scope based on the presence/absence of @Singleton
            Scope scope = getScope(implClass);
            // See if it's a Provider...
            if (Provider.class.isAssignableFrom(implClass)) {
                bindProvider(implClass,scope);
            } else {
                binder.bind(implClass).in(scope);
            }
        } catch (ClassNotFoundException e) {
            binder.addError(e);
        }
    }

    private void bindProvider(Class<?> implClass, Scope scope) {
        Class<?> providedClass = null;
        // Find the provided type...
        Type[] types = implClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType().equals(Provider.class)) {
                    if (providedClass != null) {
                        binder.addError("Implementation class " + implClass.getCanonicalName()
                                + " already implements Provider<" + providedClass.getSimpleName()+">!");
                        return;
                    }
                    final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments == null || actualTypeArguments.length != 1) {
                        binder.addError("Provider should have one and only one type parameter!");
                        return;
                    }
                    if (actualTypeArguments[0] instanceof Class<?>) {
                        providedClass = (Class<?>) actualTypeArguments[0];
                    }
                    else {
                        binder.addError("Type parameter '" + actualTypeArguments[0] + " is not a class!");
                        return;
                    }
                }
            }
            if (providedClass == null) {
                binder.addError("Unable to find provided class for " + implClass.getCanonicalName());
                return;
            }
            binder.bind(providedClass).toProvider((Class<? extends Provider<?>>) implClass).in(scope);
        }
        if (providedClass == null)
            throw new RuntimeException("Unable to find provided class for " + implClass.getCanonicalName());
    }

    private Scope getScope(Class<?> implClass) {
        return implClass.getAnnotation(Singleton.class) != null ?
                Scopes.SINGLETON : Scopes.NO_SCOPE;
    }

    public void go() {
        scan();
    }
}
