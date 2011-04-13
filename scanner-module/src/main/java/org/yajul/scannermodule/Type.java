package org.yajul.scannermodule;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Selects a specific interface for binding (like @Type in JSR-299), optional annotation (annotatedWith in
 * Guice, qualifier in JSR-299).
 * <br>
 * User: Josh
 * Date: 3/27/11
 * Time: 3:39 PM
 */
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Type {
    Class type();
    Class<? extends Annotation> annotatedWith() default Unqualified.class;
}
