// $Id$
package org.yajul.framework.jta;

/**
 * PlatformTransactionManager for Weblogic JTA.
 * @author josh Apr 2, 2004 8:18:06 AM
 */
public class WeblogicJtaTransactionManager extends ContainerSpecificJtaTransactionManager
{
    public WeblogicJtaTransactionManager()
    {
        super("javax.transaction.TransactionManager", "javax.transaction.UserTransaction");
    }
}
