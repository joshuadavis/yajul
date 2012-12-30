package org.yajul.micro;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import java.util.Collection;
import java.util.HashSet;

/**
 * Test module
 * <br>
 * User: josh
 * Date: Nov 10, 2009
 * Time: 12:34:09 PM
 */
public class MyModule extends AbstractModule
{
    protected void configure()
    {
        bind(Collection.class).to(HashSet.class).in(Scopes.SINGLETON);
        bind(FluxCapacitor.class).in(Scopes.SINGLETON);
        bind(TimeMachine.class).to(Delorian.class).in(Scopes.SINGLETON);
    }
}
