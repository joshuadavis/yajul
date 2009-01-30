package org.yajul.jndi;

import org.yajul.embedded.EmbeddedJBossTestCase;

import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * JNDI test
 * <br>
 * User: josh
 * Date: Jan 30, 2009
 * Time: 4:07:28 PM
 */
public class JndiTest extends EmbeddedJBossTestCase {
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testJndiHelper() throws Exception {
        InitialContext ic = new InitialContext();
        String listing = JndiHelper.listBindings(ic,"java:/");
        System.out.println(listing);
        DataSource ds = JndiHelper.lookup(ic, DataSource.class, "java:/DefaultDS");
        assertNotNull(ds);
    }
}
