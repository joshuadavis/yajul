// $Id$
package org.yajul.framework.jta;

/**
 * TODO: Add class javadoc
 * 
 * @author josh Apr 2, 2004 8:15:14 AM
 */
public class JbossJtaTransactionManager extends ContainerSpecificJtaTransactionManager
{
    public JbossJtaTransactionManager()
    {
        super("java:/TransactionManager","UserTransaction");
    }
}
