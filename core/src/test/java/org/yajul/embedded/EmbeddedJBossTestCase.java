package org.yajul.embedded;

import com.google.inject.Provider;
import junit.framework.TestCase;
import org.yajul.jndi.DefaultInitialContextProvider;
import org.yajul.jndi.DefaultJndiLookup;
import org.yajul.jndi.JndiLookup;

import javax.naming.InitialContext;

/**
 * Base class for embedded JBoss tests.
 * <br>
 * User: josh
 * Date: Jan 30, 2009
 * Time: 4:08:07 PM
 */
public abstract class EmbeddedJBossTestCase extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        EmbeddedJBossHelper.startup();
    }
    
    protected JndiLookup getJndiLookup() {
        return new DefaultJndiLookup(getInitialContextProvider());
    }

    protected Provider<InitialContext> getInitialContextProvider() {
        return new DefaultInitialContextProvider();
    }
}
