package org.yajul.jndi;

import org.yajul.util.ObjectProvider;

import javax.naming.InitialContext;

/**
 * Provides the default initial context (initialized from jndi.properties).
 * <br>
 * User: Josh
 * Date: Jan 14, 2010
 * Time: 6:35:07 AM
 */
public class DefaultInitialContextProvider implements ObjectProvider<InitialContext> {
    public InitialContext getObject() {
        return JndiHelper.getDefaultInitialContext();
    }
}
