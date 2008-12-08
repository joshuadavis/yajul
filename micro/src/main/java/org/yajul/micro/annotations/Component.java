package org.yajul.micro.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * Tags a class as a PicoContainer component.
 * <br>User: Josh
 * Date: Sep 7, 2008
 * Time: 10:51:11 AM
 */
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Component {
    public static final String NO_KEY_STRING = "no key string";

    /**
     * @return the component key (string or interface name)
     */
    public abstract String key() default NO_KEY_STRING;
}