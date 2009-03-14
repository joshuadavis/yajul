package org.yajul.embedded;

import junit.framework.TestCase;
import org.jboss.embedded.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <br>
 * User: josh
 * Date: Jan 30, 2009
 * Time: 3:10:22 PM
 */
public class EmbeddedJBossTest extends TestCase {

    public void testEmbeddedJBossBoot() throws Exception {
        try {
            EmbeddedJBossHelper.startup();
        }
        finally {
        }
    }
}
