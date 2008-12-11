package org.yajul.micro;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.MemberValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.micro.annotations.Component;

import java.lang.annotation.Annotation;

/**
 * Scans for component annotations.
 * <br>User: Josh
 * Date: Sep 7, 2008
 * Time: 10:49:49 AM
 */
public class ComponentScanner extends AbstractAnnotationScanner {
    private static final Logger log = LoggerFactory.getLogger(ComponentScanner.class);
    private MicroContainer microContainer;

    /**
     * Scan the files in the classloader that include the resource for component annotations
     * @param resourceName the 'tag' resource that indicates which elements of the class loader to scan
     * @param classLoader the classloader
     */
    public ComponentScanner(String resourceName, ClassLoader classLoader) {
        super(resourceName, classLoader);
        addAnnotation(Component.class);
    }

    protected void handleAnnotation(String name, ClassFile classFile, Class<? extends Annotation> annotation) {
        if (annotation == Component.class)
        {
            // Register the component.
            String key = getAnnotationValue(classFile,annotation,"key");
            if (log.isDebugEnabled())
               log.debug("handleAnnotation() : name=" + name + " key=" + key);
        }
    }

    /**
     * Get the value of the annotation on the Javassist {@link ClassFile}, or null
     * if the class doesn't have that annotation
     */
    protected String getAnnotationValue(ClassFile classFile, Class<? extends Annotation> annotationType, String memberName) {
        AnnotationsAttribute visible = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
        if (visible != null) {
            javassist.bytecode.annotation.Annotation annotation = visible.getAnnotation(annotationType.getName());
            if (annotation == null) {
                return null;
            } else {
                MemberValue memberValue = annotation.getMemberValue(memberName);

                return memberValue == null ? null : memberValue.toString(); //TODO: toString() here is probably Bad ;-)
            }
        } else {
            return null;
        }
    }

    public void registerComponents(MicroContainer microContainer) {
        this.microContainer = microContainer;
        scan();
    }
}
