package org.yajul.framework.jta;

import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiTemplate;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.HeuristicCompletionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.apache.log4j.Logger;

/**
 * A special implementation of Spring's JtaTransactionManager that exposes
 * the underlying JTA TransactionManager object so that JtaSynchronizer can use it.
 * User: jdavis
 * Date: Apr 2, 2004
 * Time: 4:31:50 PM
 * @author jdavis
 */
public class JtaTransactionManager extends AbstractPlatformTransactionManager implements InitializingBean {

	public static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";

    private static Logger logger = Logger.getLogger(JtaTransactionManager.class);

    private JndiTemplate jndiTemplate = new JndiTemplate();

	private UserTransaction userTransaction;

	private String userTransactionName = DEFAULT_USER_TRANSACTION_NAME;

	private TransactionManager transactionManager;

	private String transactionManagerName;


	/**
	 * Set the JndiTemplate to use for JNDI lookups.
	 * A default one is used if not set.
	 */
	public void setJndiTemplate(JndiTemplate jndiTemplate) {
		if (jndiTemplate == null) {
			throw new IllegalArgumentException("jndiTemplate must not be null");
		}
		this.jndiTemplate = jndiTemplate;
	}

	/**
	 * Set the JTA UserTransaction to use as direct reference.
	 * Typically just used for local JTA setups; in a J2EE environment,
	 * the UserTransaction will always be fetched from JNDI.
	 */
	public void setUserTransaction(UserTransaction userTransaction) {
		this.userTransaction = userTransaction;
	}

	/**
	 * Set the JNDI name of the JTA UserTransaction.
	 * The default one is used if not set.
	 * @see #DEFAULT_USER_TRANSACTION_NAME
	 */
	public void setUserTransactionName(String userTransactionName) {
		this.userTransactionName = userTransactionName;
	}

	/**
	 * Set the JTA TransactionManager to use as direct reference.
	 * <p>A TransactionManager is necessary for suspending and resuming transactions,
	 * as this not supported by the UserTransaction interface.
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * Set the JNDI name of the JTA TransactionManager.
	 * <p>A TransactionManager is necessary for suspending and resuming transactions,
	 * as this not supported by the UserTransaction interface.
	 */
	public void setTransactionManagerName(String transactionManagerName) {
		this.transactionManagerName = transactionManagerName;
	}


	public void afterPropertiesSet() throws CannotCreateTransactionException {
		if (this.userTransaction == null) {
			if (this.userTransactionName != null) {
				this.userTransaction = lookupUserTransaction(this.userTransactionName);
			}
			else {
				throw new IllegalArgumentException("Either userTransaction or userTransactionName must be set");
			}
		}
		if (this.transactionManager == null) {
			if (this.transactionManagerName != null) {
				this.transactionManager = lookupTransactionManager(this.transactionManagerName);
			}
			else if (this.userTransaction instanceof TransactionManager) {
				if (logger.isInfoEnabled()) {
					logger.info("JTA UserTransaction object [" + this.userTransaction + "] implements TransactionManager");
				}
				this.transactionManager = (TransactionManager) this.userTransaction;
			}
			else {
				logger.info("No JTA TransactionManager specified - transaction suspension not available");
			}
		}
	}

	/**
	 * Look up the JTA UserTransaction in JNDI via the configured name.
	 * Called by afterPropertiesSet if no direct UserTransaction reference was set.
	 * Can be overridden in subclasses to provide a different UserTransaction object.
	 * @param userTransactionName the JNDI name of the UserTransaction
	 * @return the UserTransaction object
	 * @throws CannotCreateTransactionException if the JNDI lookup failed
	 * @see #setJndiTemplate
	 * @see #setUserTransactionName
	 */
	protected UserTransaction lookupUserTransaction(String userTransactionName)
			throws CannotCreateTransactionException {
		try {
			Object jndiObj = this.jndiTemplate.lookup(userTransactionName);
			if (!(jndiObj instanceof UserTransaction)) {
				throw new CannotCreateTransactionException("Object [" + jndiObj + "] available at JNDI location [" +
				                                           userTransactionName + "] does not implement " +
				                                           "javax.transaction.UserTransaction");
			}
			UserTransaction ut = (UserTransaction) jndiObj;
			if (logger.isInfoEnabled()) {
				logger.info("Using JTA UserTransaction [" + ut + "] from JNDI location [" +
				            userTransactionName + "]");
			}
			return ut;
		}
		catch (NamingException ex) {
			throw new CannotCreateTransactionException("JTA UserTransaction is not available at JNDI location [" +
			                                           userTransactionName + "]", ex);
		}
	}

	/**
	 * Look up the JTA TransactionManager in JNDI via the configured name.
	 * Called by afterPropertiesSet if no direct TransactionManager reference was set.
	 * Can be overridden in subclasses to provide a different TransactionManager object.
	 * @param transactionManagerName the JNDI name of the TransactionManager
	 * @return the UserTransaction object
	 * @throws CannotCreateTransactionException if the JNDI lookup failed
	 * @see #setJndiTemplate
	 * @see #setTransactionManagerName
	 */
	protected TransactionManager lookupTransactionManager(String transactionManagerName)
			throws CannotCreateTransactionException {
		try {
			Object jndiObj = this.jndiTemplate.lookup(transactionManagerName);
			if (!(jndiObj instanceof TransactionManager)) {
				throw new CannotCreateTransactionException("Object [" + jndiObj + "] available at JNDI location [" +
				                                           transactionManagerName + "] does not implement " +
				                                           "javax.transaction.TransactionManager");
			}
			TransactionManager tm = (TransactionManager) jndiObj;
			if (logger.isInfoEnabled()) {
				logger.info("Using JTA TransactionManager [" + tm + "] from JNDI location [" +
				            transactionManagerName + "]");
			}
			return tm;
		}
		catch (NamingException ex) {
			throw new CannotCreateTransactionException("JTA TransactionManager is not available at JNDI location [" +
			                                           transactionManagerName + "]", ex);
		}
	}

	protected Object doGetTransaction() {
		return this.userTransaction;
	}

	protected boolean isExistingTransaction(Object transaction) {
		try {
			int status = ((UserTransaction) transaction).getStatus();
			return (status != Status.STATUS_NO_TRANSACTION);
		}
		catch (SystemException ex) {
			throw new TransactionSystemException("JTA failure on getStatus", ex);
		}
	}

	protected void doBegin(Object transaction, TransactionDefinition definition) {
		if (logger.isDebugEnabled()) {
			logger.debug("Beginning JTA transaction [" + transaction + "] ");
		}
		UserTransaction ut = (UserTransaction) transaction;
		applyIsolationLevel(definition.getIsolationLevel());
		try {
			if (definition.getTimeout() > TransactionDefinition.TIMEOUT_DEFAULT) {
				ut.setTransactionTimeout(definition.getTimeout());
			}
			ut.begin();
		}
		catch (javax.transaction.NotSupportedException ex) {
			// assume "nested transactions not supported"
			throw new IllegalTransactionStateException(
			    "JTA implementation does not support nested transactions", ex);
		}
		catch (UnsupportedOperationException ex) {
			// assume "nested transactions not supported"
			throw new IllegalTransactionStateException(
			    "JTA implementation does not support nested transactions", ex);
		}
		catch (SystemException ex) {
			throw new TransactionSystemException("JTA failure on begin", ex);
		}
	}

	/**
	 * Apply the given transaction isolation level. Default implementation
	 * will throw an exception for any level other than ISOLATION_DEFAULT.
	 * To be overridden in subclasses for specific JTA implementations.
	 * @param isolationLevel isolation level taken from transaction definition
	 * @throws org.springframework.transaction.InvalidIsolationLevelException if the given isolation level
	 * cannot be applied
	 */
	protected void applyIsolationLevel(int isolationLevel) throws InvalidIsolationLevelException {
		if (isolationLevel != TransactionDefinition.ISOLATION_DEFAULT) {
			throw new InvalidIsolationLevelException("JtaTransactionManager does not support custom isolation levels");
		}
	}

	protected Object doSuspend(Object transaction) {
		if (this.transactionManager == null) {
			throw new IllegalTransactionStateException("JtaTransactionManager needs a JTA TransactionManager for " +
																								 "suspending a transaction - specify the 'transactionManager' " +
																								 "or 'transactionManagerName' property");
		}
		try {
			return this.transactionManager.suspend();
		}
		catch (SystemException ex) {
			throw new TransactionSystemException("JTA failure on suspend", ex);
		}
	}

	protected void doResume(Object transaction, Object suspendedResources) {
		if (this.transactionManager == null) {
			throw new IllegalTransactionStateException("JtaTransactionManager needs a JTA TransactionManager for " +
																								 "suspending a transaction - specify the 'transactionManager' " +
																								 "or 'transactionManagerName' property");
		}
		try {
			this.transactionManager.resume((Transaction) suspendedResources);
		}
		catch (javax.transaction.InvalidTransactionException ex) {
			throw new IllegalTransactionStateException("Tried to resume invalid JTA transaction", ex);
		}
		catch (SystemException ex) {
			throw new TransactionSystemException("JTA failure on resume", ex);
		}
	}

	protected boolean isRollbackOnly(Object transaction) throws TransactionException {
		try {
			return ((UserTransaction) transaction).getStatus() == Status.STATUS_MARKED_ROLLBACK;
		}
		catch (SystemException ex) {
			throw new TransactionSystemException("JTA failure on getStatus", ex);
		}
	}

	protected void doCommit(DefaultTransactionStatus status) {
		if (status.isDebug()) {
			logger.debug("Committing JTA transaction [" + status.getTransaction() + "]");
		}
		try {
			((UserTransaction) status.getTransaction()).commit();
		}
		catch (javax.transaction.RollbackException ex) {
			throw new UnexpectedRollbackException("JTA transaction rolled back", ex);
		}
		catch (javax.transaction.HeuristicMixedException ex) {
			throw new HeuristicCompletionException(HeuristicCompletionException.STATE_MIXED, ex);
		}
		catch (javax.transaction.HeuristicRollbackException ex) {
			throw new HeuristicCompletionException(HeuristicCompletionException.STATE_ROLLED_BACK, ex);
		}
		catch (SystemException ex) {
			throw new TransactionSystemException("JTA failure on commit", ex);
		}
	}

	protected void doRollback(DefaultTransactionStatus status) {
		if (status.isDebug()) {
			logger.debug("Rolling back JTA transaction [" + status.getTransaction() + "]");
		}
		try {
			((UserTransaction) status.getTransaction()).rollback();
		}
		catch (SystemException ex) {
			throw new TransactionSystemException("JTA failure on rollback", ex);
		}
	}

	protected void doSetRollbackOnly(DefaultTransactionStatus status) {
		if (status.isDebug()) {
			logger.debug("Setting JTA transaction [" + status.getTransaction() + "] rollback-only");
		}
		try {
			((UserTransaction) status.getTransaction()).setRollbackOnly();
		}
		catch (IllegalStateException ex) {
			throw new NoTransactionException("No active JTA transaction");
		}
		catch (SystemException ex) {
			throw new TransactionSystemException("JTA failure on setRollbackOnly", ex);
		}
	}

	protected void doCleanupAfterCompletion(Object transaction) {
		// nothing to do here
	}

    public TransactionManager getTransactionManager()  {
        return transactionManager;
    }
}
