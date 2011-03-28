package org.yajul.scannermodule;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotate implementations or providers with this to automatically bind them.
 * <br>
 * User: Josh
 * Date: 3/27/11
 * Time: 3:39 PM
 */
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Bind {
}
