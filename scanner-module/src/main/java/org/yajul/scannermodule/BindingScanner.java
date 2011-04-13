package org.yajul.scannermodule;

import com.google.inject.*;
import javassist.bytecode.ClassFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Externalizable;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Actually does the work of scanning and binding.
 * <br>
 * User: Josh
 * Date: 3/27/11
 * Time: 5:33 PM
 */
class BindingScanner extends AbstractAnnotationScanner {
    private static final Logger log = LoggerFactory.getLogger(BindingScanner.class);

    /**
     * Interfaces that are ignored by default.
     */
    private static Set<Class> IGNORED = new HashSet<Class>(
            Arrays.asList(
                    Serializable.class,
                    Externalizable.class)
    );

    private Binder binder;

    public BindingScanner(Binder binder, ClassLoader classLoader) {
        super(ScannerModule.RESOURCE_NAME, classLoader, Bind.class);
        this.binder = binder;
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
                bindProvider(implClass, scope);
            } else {
                bindImplementation(implClass, scope);
            }
        } catch (ClassNotFoundException e) {
            binder.addError(e);
        }
    }

    private void bindImplementation(Class<?> implClass, Scope scope) {
        Set<Key> keys = new HashSet<Key>();
        org.yajul.scannermodule.Type type = implClass.getAnnotation(org.yajul.scannermodule.Type.class);
        if (type != null) {
            if (type.annotatedWith() != null)
                keys.add(Key.get(type.type(),type.annotatedWith()));
            else
                keys.add(Key.get(type.type()));
        } else {
            Class[] interfaces = implClass.getInterfaces();
            for (Class anInterface : interfaces) {
                if (!IGNORED.contains(anInterface))
                    keys.add(Key.get(anInterface));
                else
                    log.info("Ignoring " + anInterface.getSimpleName());
            }
        }

        if (keys.size() == 1) {
            final Key toBind = keys.iterator().next();
            log.info("Binding single key " + toBind + " to " + implClass.getSimpleName() + " ...");
            //noinspection unchecked
            binder.bind(toBind).to(implClass).in(scope);
        } else {
            log.info("Binding impl " + implClass.getSimpleName() + " ...");
            binder.bind(implClass).in(scope);
            for (Key key : keys) {
                log.info("Binding " + key + " to impl " + implClass.getSimpleName() + "...");
                binder.bind(key).to(implClass);
            }
        }
    }

    private void bindProvider(Class implClass, Scope scope) {
        Class providedClass = null;
        // Find the provided type...
        Type[] types = implClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                // Make sure it's a provider.
                if (parameterizedType.getRawType().equals(Provider.class)) {
                    // Paranoid check - Make sure it doesn't implement more than one Provider
                    // (should be impossible)
                    if (providedClass != null) {
                        binder.addError("Implementation class " + implClass.getCanonicalName()
                                + " already implements Provider<" + providedClass.getSimpleName() + ">!");
                        return;
                    }
                    // Provider should have only one type argument.
                    final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments == null || actualTypeArguments.length != 1) {
                        binder.addError("Provider should have one and only one type parameter!");
                        return;
                    }
                    if (actualTypeArguments[0] instanceof Class<?>) {
                        providedClass = (Class<?>) actualTypeArguments[0];
                    }
                    // The Provider type should be a class.
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
        }
        if (providedClass == null)
            throw new RuntimeException("Unable to find provided class for " + implClass.getCanonicalName());

        //noinspection unchecked
        binder.bind(providedClass).toProvider(implClass).in(scope);
    }

    private Scope getScope(Class<?> implClass) {
        return implClass.getAnnotation(Singleton.class) != null ?
                Scopes.SINGLETON : Scopes.NO_SCOPE;
    }

    public void go() {
        scan();
    }
}
