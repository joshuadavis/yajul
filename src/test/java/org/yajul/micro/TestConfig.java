package org.yajul.micro;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

import java.util.Set;
import java.util.TreeSet;

/**
 * Container configuring component.
 * <br>
 * User: josh
 * Date: Mar 11, 2008
 * Time: 10:50:51 PM
 */
public class TestConfig implements Module {
    public void configure(Binder binder) {
        binder.bind(Set.class).to(TreeSet.class).in(Scopes.SINGLETON);
    }
}
