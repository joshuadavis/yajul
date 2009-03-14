package org.yajul.embedded;

import junit.framework.TestCase;

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
}
