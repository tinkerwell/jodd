// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.db;

import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;
import jodd.db.DbSession;

/**
 * {@link JtxTransaction} extension that simplifies beginning of the transaction since
 * related {@link DbJtxTransactionManager} allows only one resource type.
 */
public class DbJtxTransaction extends JtxTransaction {

	protected DbJtxTransaction(JtxTransactionManager txManager, JtxTransactionMode mode, Object scope, boolean active) {
		super(txManager, mode, scope, active);
	}

	public DbSession requestResource() {
		return requestResource(DbSession.class);
	}

}
