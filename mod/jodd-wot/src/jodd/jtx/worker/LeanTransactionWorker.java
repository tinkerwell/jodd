// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.worker;

import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lean transaction worker helps dealing transactions when they were requested
 * in several places, usually in separated methods. This worker knows when requested transaction is
 * the same as current one, or completely new. It might be useful for aspects.
 */
public class LeanTransactionWorker {

	private static final Logger log = LoggerFactory.getLogger(LeanTransactionWorker.class);

	protected final JtxTransactionManager txManager;

	public LeanTransactionWorker(JtxTransactionManager txManager) {
		this.txManager = txManager;
	}

	/**
	 * Returns transaction manager.
	 */
	public JtxTransactionManager getTransactionManager() {
		return txManager;
	}


	/**
	 * Returns current transaction or <code>null</code> if there is no transaction at the moment.
	 */
	public JtxTransaction getCurrentTransaction() {
		return txManager.getTransaction();
	}

	/**
	 * Requests for transaction and returns non-null value <b>only</b> when new transaction
	 * is created! When <code>null</code> is returned, transaction may be get by
	 * {@link #getCurrentTransaction()}.
	 *
	 * @see jodd.jtx.JtxTransactionManager#requestTransaction(jodd.jtx.JtxTransactionMode)
	 */
	public JtxTransaction maybeRequestTransaction(JtxTransactionMode txMode, Object context) {
		if (txMode == null) {
			return null;
		}
		JtxTransaction currentTx = txManager.getTransaction();
		JtxTransaction requestedTx = txManager.requestTransaction(txMode, context);
		if (currentTx == requestedTx) {
			return null;
		}
		return requestedTx;
	}


	/**
	 * Commits transaction if created in the same level where this method is invoked.
	 * Returns <code>true</code> if transaction was actually committed or <code>false</code>
	 * if transaction was not created on this level. 
	 */
	public boolean maybeCommitTransaction(JtxTransaction tx) {
		if (tx == null) {
			return false;
		}
		log.info("commit tx");
		tx.commit();
		return true;
	}

	/**
	 * Rollbacks transaction if created in the same scope where this method is invoked.
	 * If not, current transaction is marked for rollback.
	 * Returns <code>true</code> if transaction was actually roll backed.
	 */
	public boolean markOrRollbackTransaction(JtxTransaction tx, Throwable cause) {
		log.error("rollback tx", cause);
		if (tx == null) {
			tx = getCurrentTransaction();
			if (tx == null) {
				return false;
			}
			tx.setRollbackOnly(cause);
			return false;
		}
		tx.rollback();
		return true;
	}
}
