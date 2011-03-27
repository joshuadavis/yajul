package org.yajul.scannermodule;

import com.google.inject.Provider;

/**
 * provider implementation for scanning module test
 * <br>
 * User: Josh
 * Date: 3/27/11
 * Time: 3:47 PM
 */
@Bind
public class FooProviderOne implements Provider<Foo> {
    public Foo get() {
        return new Foo() {

        };
    }
}
