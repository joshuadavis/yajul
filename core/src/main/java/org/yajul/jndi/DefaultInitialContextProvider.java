package org.yajul.jndi;

import com.google.inject.Provider;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Provides the default initial context (initialized from jndi.properties).
 * <br>
 * User: Josh
 * Date: Jan 14, 2010
 * Time: 6:35:07 AM
 */
public class DefaultInitialContextProvider implements Provider<InitialContext> {
    public InitialContext get() {
        return JndiHelper.getDefaultInitialContext();
    }
}
