package org.yajul.micro;

import javassist.bytecode.ClassFile;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Records class names of annotated classes.
 * <br>User: Josh
 * Date: Dec 11, 2008
 * Time: 7:17:07 AM
 */
public class AnnotationScanner extends AbstractAnnotationScanner {

    private Set<String> names = new HashSet<String>();

    public AnnotationScanner(String resourceName) {
        super(resourceName);
    }

    public AnnotationScanner(String resourceName, ClassLoader classLoader) {
        super(resourceName, classLoader);
    }
    
    protected void handleAnnotation(String name, ClassFile classFile, Class<? extends Annotation> annotation) {
        names.add(name);
    }

    public Collection<String> getNames() {
        scan();
        return names;
    }
}
