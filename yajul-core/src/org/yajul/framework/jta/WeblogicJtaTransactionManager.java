// $Id$
package org.yajul.framework.jta;

/**
 * TODO: Add class javadoc
 * 
 * @author josh Apr 2, 2004 8:18:06 AM
 */
public class WeblogicJtaTransactionManager extends ContainerSpecificJtaTransactionManager
{
    public WeblogicJtaTransactionManager()
    {
        super("javax.transaction.TransactionManager", "javax.transaction.UserTransaction");
    }
}
