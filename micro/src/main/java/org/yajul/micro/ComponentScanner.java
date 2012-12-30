package org.yajul.micro;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.micro.annotations.Component;
import org.yajul.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Scans for component annotations.
 * <br>User: Josh
 * Date: Sep 7, 2008
 * Time: 10:49:49 AM
 */
public class ComponentScanner extends AbstractAnnotationScanner {
    private static final Logger log = LoggerFactory.getLogger(ComponentScanner.class);

    private Set<Class<?>> implementations = new HashSet<Class<?>>();
    private Map<Class, Class> keyToImpl = new HashMap<Class, Class>();

    /**
     * Scan the files in the classloader that include the resource for component annotations
     *
     * @param resourceName the 'tag' resource that indicates which elements of the class loader to scan
     * @param classLoader  the classloader
     */
    public ComponentScanner(String resourceName, ClassLoader classLoader) {
        super(resourceName, classLoader);
        addAnnotation(Component.class);
    }

    public ComponentScanner(String resourceName) {
        super(resourceName, ReflectionUtil.getCurrentClassLoader());
        addAnnotation(Component.class);
    }

    protected void handleAnnotation(String name, ClassFile classFile, Class<? extends Annotation> annotation) {
        if (annotation == Component.class) {
            // Register the component.
            String key = getAnnotationValue(classFile, annotation, "key");
            if (log.isTraceEnabled())
                log.trace("handleAnnotation() : name=" + name + " key=" + key);
            if (key == null) {
                Object implObject = MicroContainer.processName(name, getClassLoader());
                if (implObject instanceof Class<?>) {
                    Class<?> c = (Class<?>) implObject;
                    implementations.add(c);
                }
            } else {
                String[] interfaceNames = classFile.getInterfaces();
                Object implObject = MicroContainer.processName(name,  getClassLoader());
                if (implObject instanceof Class<?>) {
                    Class<?> implClass = (Class<?>) implObject;
                    Object keyObject = MicroContainer.processName(key,  getClassLoader());
                    if (keyObject instanceof Class<?>) {
                        Class<?> keyClass = (Class<?>) keyObject;
                        keyToImpl.put(keyClass, implClass);
                    } else {
                        Class<?> keyClass = null;
                        for (String interfaceName : interfaceNames) {
                            if (log.isTraceEnabled())
                               log.trace("handleAnnotation() : key=" + key + " interfaceName=" + interfaceName);
                            if (interfaceName.endsWith(key)) {
                                try {
                                    keyClass =  getClassLoader().loadClass(interfaceName);
                                    break;
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        if (keyClass != null)
                            keyToImpl.put(keyClass, implClass);
                        else
                        {
                            if (log.isTraceEnabled())
                               log.trace("handleAnnotation() : ignoring key " + key +
                                       ", it is not a class name or an interface name");
                        }
                    }
                } else {
                    if (log.isDebugEnabled())
                        log.debug("handleAnnotation() : ignoring name " + name + " it is not a class name.");
                }
            }
        }
    }

    /**
     * Get the value of the annotation on the Javassist {@link ClassFile}, or null
     * if the class doesn't have that annotation
     *
     * @param classFile      the classFile
     * @param annotationType the annotation to look for
     * @param memberName     the member to look for
     * @return the annotation value
     */
    protected String getAnnotationValue(ClassFile classFile, Class<? extends Annotation> annotationType, String memberName) {
        AnnotationsAttribute visible = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
        if (visible != null) {
            javassist.bytecode.annotation.Annotation annotation = visible.getAnnotation(annotationType.getName());
            if (annotation == null) {
                return null;
            } else {
                MemberValue memberValue = annotation.getMemberValue(memberName);
                if (memberValue instanceof StringMemberValue) {
                    StringMemberValue stringMemberValue = (StringMemberValue) memberValue;
                    return stringMemberValue.getValue();
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public Injector createInjector() {
        scan();
        ModuleList moduleList = new ModuleList();
        moduleList.add(new AbstractModule() {
            protected void configure() {
                for (Class<?> implementation : implementations) {
                    bind(implementation).in(Scopes.SINGLETON);
                }
            }
        });
        moduleList.add(new AbstractModule() {
            protected void configure() {
                for (Map.Entry<Class, Class> entry : keyToImpl.entrySet()) {
                    //noinspection unchecked
                    bind(entry.getKey()).to(entry.getValue()).in(Scopes.SINGLETON);
                }
            }
        });
        return moduleList.createInjector();
    }
}
