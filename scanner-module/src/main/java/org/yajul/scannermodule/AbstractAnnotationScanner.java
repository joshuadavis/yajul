package org.yajul.scannermodule;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds classes with a given set of annotations.
 * <br>User: Josh
 * Date: Sep 24, 2008
 * Time: 6:22:36 AM
 */
abstract class AbstractAnnotationScanner extends AbstractScanner {
    private static final Logger log = LoggerFactory.getLogger(AbstractAnnotationScanner.class);
    private Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();

    public AbstractAnnotationScanner(String resourceName, ClassLoader classLoader,Class<? extends Annotation>... annotations) {
        super(resourceName, classLoader);
        for (Class<? extends Annotation> annotation : annotations) {
            addAnnotation(annotation);
        }
    }

    public void addAnnotation(Class<? extends Annotation> annotation) {
        annotations.add(annotation);
    }

    public void doScan()
    {
        scan();
    }

    /**
     * Turns a class file name into a class name.
     *
     * @param filename the file name
     * @return the class name.
     */
    public static String filenameToClassname(String filename) {
        return filename.substring(0, filename.lastIndexOf(".class"))
                .replace('/', '.').replace('\\', '.');
    }
    @Override
    final public void handleItem(String name) {
        if (name.endsWith(".class")) {
            String className = filenameToClassname(name);
            try {
                // Use Javassist to get the meta data so we don't actually
                // load the class into the class loader.
                ClassFile classFile = getClassFile(name);
                for (Class<? extends Annotation> annotation : annotations) {
                    if (hasAnnotation(classFile, annotation)) {
                        handleAnnotation(name, classFile, annotation);
                    }
                }
            } catch (IOException e) {
                if (log.isDebugEnabled())
                    log.debug("handleItem() " + e.getMessage());
            }
        }
    }

    protected abstract void handleAnnotation(String name, ClassFile classFile, Class<? extends Annotation> annotation);

    protected ClassFile getClassFile(String name) throws IOException {
        InputStream stream = getResourceAsStream(name);
        DataInputStream dstream = new DataInputStream(stream);
        try {
            return new ClassFile(dstream);
        }
        finally {
            dstream.close();
            stream.close();
        }
    }

    /**
     * @param classFile      the ClassFile
     * @param annotationType the annotation
     * @return true if the Javassist {@link ClassFile} has the specfied annotation
     */
    protected boolean hasAnnotation(ClassFile classFile, Class<? extends Annotation> annotationType) {
        AnnotationsAttribute visible = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
        return visible != null && visible.getAnnotation(annotationType.getName()) != null;
    }
}
