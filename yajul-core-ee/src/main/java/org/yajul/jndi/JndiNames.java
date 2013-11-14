package org.yajul.jndi;

/**
 * Provides the names of some standard Java Enterprise objects in JNDI.
 * <br>
 * User: Josh
 * Date: Jan 14, 2010
 * Time: 6:14:59 AM
 */
public interface JndiNames {

    /**
     * @return the JNDI name of the TransactionManager.
     */
    String getTransactionManagerName();

    /**
     * @return the JNDI name of the JMS ConnectionFactory
     */
    String getConnectionFactoryName();
}
