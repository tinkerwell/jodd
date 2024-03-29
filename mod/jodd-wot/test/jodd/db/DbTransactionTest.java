// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionMode;
import jodd.jtx.JtxException;
import jodd.jtx.db.DbJtxResourceManager;
import jodd.jtx.db.DbJtxTransaction;
import jodd.util.ThreadUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbTransactionTest extends DbHsqldbTestCase {

	/**
	 * Tests if rollback works.
	 */
	public void testRollback()  throws SQLException {
		// prepare manager
		JtxTransactionManager manager = new JtxTransactionManager();
		manager.registerResourceManager(new DbJtxResourceManager(cp));

		// request transaction
		JtxTransaction tx = manager.requestTransaction(new JtxTransactionMode().propagationRequired().readOnly(false));
		DbSession session = tx.requestResource(DbSession.class);
		assertNotNull(session);

		// insert two records
		DbQuery query = new DbQuery(session, "insert into GIRL values(4, 'Jeniffer', 'fighting')");
		assertEquals(1, query.executeUpdateAndClose());
		query = new DbQuery(session, "insert into GIRL values(5, 'Annita', 'bartender')");
		assertEquals(1, query.executeUpdateAndClose());

		// rollback
		tx.rollback();

		// check !!!
		session = new DbSession(cp);

		DbQuery query2 = new DbQuery(session, "select count(*) from GIRL");
		long count = query2.executeCountAndClose();

		assertEquals(0, count);
		session.closeSession();
	}


	// ---------------------------------------------------------------- misc

	public void testIsolation() throws SQLException {
		JtxTransactionManager manager = new JtxTransactionManager();
		manager.registerResourceManager(new DbJtxResourceManager(cp));

		JtxTransaction tx1 = manager.requestTransaction(new JtxTransactionMode().propagationRequired().readOnly(false));
		DbSession session1 = tx1.requestResource(DbSession.class);
		assertNotNull(session1);
		JtxTransaction tx2 = manager.requestTransaction(new JtxTransactionMode().propagationNotSupported());
		DbSession session2 = tx2.requestResource(DbSession.class);
		assertNotNull(session2);

		assertNotSame(session1, session2);
		assertTotals(manager, 2, 1);

		DbQuery query = new DbQuery(session1, "insert into GIRL values(4, 'Jeniffer', 'fighting')");
		assertEquals(1, query.executeUpdate());
		query.close();

		DbQuery query2 = new DbQuery(session2, "select count(*) from GIRL");
		ResultSet rs = query2.execute();
		if (rs.next()) {
			assertEquals(1, rs.getInt(1));
		}

		session1.rollbackTransaction();

		rs = query2.execute();
		if (rs.next()) {
			assertEquals(0, rs.getInt(1));
		}
		rs.close();
		
		tx2.commit();
		tx1.commit();
		
		assertTotals(manager, 0, 0);
	}

	// ---------------------------------------------------------------- presentation layer

	public void testAction() {
		assertNotNull(dbtxm);
		assertTotals(0, 0);
		assertEquals(0, dbtxm.totalThreadTransactions());
		service0();
		JtxTransaction tx1 = service1();
		service2(tx1);
		service3(tx1);
		tx1.commit();
		assertTotals(0, 0);
	}


	// ---------------------------------------------------------------- service layer


	DbSession s0;

	void service0() {
		assertTotals(0, 0);
		DbJtxTransaction tx = dbtxm.requestTransaction(new JtxTransactionMode().propagationRequired());
		assertTotals(1, 1);
		s0 = tx.requestResource();
		service0_1(tx);
		tx.commit();
		assertTotals(0, 0);
	}

	void service0_1(JtxTransaction uptx) {
		assertTotals(1, 1);
		DbJtxTransaction tx = dbtxm.requestTransaction(new JtxTransactionMode().propagationSupports());
		assertTotals(1, 1);
		assertEquals(uptx, tx);
		DbSession s1 = tx.requestResource();
		assertEquals(s0, s1);
	}

	// ---------------------------------------------------------------- service layer

	DbSession s1;

	// service #1 REQUIRED
	JtxTransaction service1() {
		assertTotals(0, 0);
		DbJtxTransaction tx = dbtxm.requestTransaction(new JtxTransactionMode().propagationRequired());
		assertTotals(1, 1);
		s1 = tx.requestResource();
		return tx;
	}

	// service #2 REQUIRES NEW
	void service2(JtxTransaction tx1) {
		assertTotals(1, 1);
		DbJtxTransaction tx = dbtxm.requestTransaction(new JtxTransactionMode().propagationRequiresNew());
		assertTotals(2, 2);
		assertNotSame(tx1, tx);
		assertNotSame(s1, tx.requestResource());
		tx.commit();
		assertTotals(1, 1);
	}

	// service #3 REQUIRED
	void service3(JtxTransaction tx1) {
		assertTotals(1, 1);
		DbJtxTransaction tx = dbtxm.requestTransaction(new JtxTransactionMode().propagationRequired());
		assertEquals(tx1, tx);
		assertTotals(1, 1);
		assertEquals(s1, tx.requestResource());
		service3_1(tx);
		assertEquals(s1, tx.requestResource());
		assertTotals(1, 1);
	}

	// service #3_1 NOT SUPPORTED
	void service3_1(JtxTransaction tx3) {
		assertTotals(1, 1);
		DbJtxTransaction tx = dbtxm.requestTransaction(new JtxTransactionMode().propagationNotSupported());
		assertNotSame(tx3, tx);
		assertTotals(2, 1);
		assertNotSame(s1, tx.requestResource());
		assertNotSame(s1, tx.requestResource());
		tx.commit();
		assertTotals(1, 1);
	}


	// ---------------------------------------------------------------- util

	public void assertTotals(int total, int totalActive) {
		assertEquals(total, dbtxm.totalThreadTransactions());
		assertEquals(totalActive, dbtxm.totalActiveThreadTransactions());
	}

	public void assertTotals(JtxTransactionManager tm, int total, int totalActive) {
		assertEquals(total, tm.totalThreadTransactions());
		assertEquals(totalActive, tm.totalActiveThreadTransactions());
	}

	// ---------------------------------------------------------------- test time

	public void testTime() {
		JtxTransactionManager manager = new JtxTransactionManager();
		manager.registerResourceManager(new DbJtxResourceManager(cp));

		JtxTransaction tx1 = manager.requestTransaction(new JtxTransactionMode().propagationRequired().transactionTimeout(1));
		DbSession session1 = tx1.requestResource(DbSession.class);
		assertNotNull(session1);
		executeCount(session1, "select count(*) from GIRL");

		ThreadUtil.sleep(2000);
		try {
			DbSession session2 = tx1.requestResource(DbSession.class);
			assertNotNull(session2);
			assertSame(session1, session2);
			executeCount(session1, "select count(*) from GIRL");
			fail();
		} catch (JtxException ignore) {
			
		}
		tx1.rollback();
	}


	// ---------------------------------------------------------------- thread

	public void testThread() {
		final JtxTransactionManager manager = new JtxTransactionManager();
		manager.registerResourceManager(new DbJtxResourceManager(cp));
		final int[] count = new int[1];

		new Thread() {
			@Override
			public void run() {
				JtxTransaction tx = manager.requestTransaction(new JtxTransactionMode().propagationRequired().transactionTimeout(1));
				count[0]++;
				assertEquals(count[0], manager.totalTransactions());
				assertEquals(1, manager.totalTransactions());
				assertEquals(1, manager.totalThreadTransactions());
				ThreadUtil.sleep(1000);
				tx.commit();
				count[0]--;
			}
		}.start();
		ThreadUtil.sleep(500);
		new Thread() {
			@Override
			public void run() {
				JtxTransaction tx = manager.requestTransaction(new JtxTransactionMode().propagationRequired().transactionTimeout(1));
				count[0]++;
				assertEquals(count[0], manager.totalTransactions());
				assertEquals(2, manager.totalTransactions());
				assertEquals(1, manager.totalThreadTransactions());
				ThreadUtil.sleep(1000);
				tx.commit();
				count[0]--;
			}
		}.start();
	}

	// ---------------------------------------------------------------- notx

	public void testNoTx() {

		final JtxTransactionManager manager = new JtxTransactionManager();
		manager.registerResourceManager(new DbJtxResourceManager(cp));

		JtxTransaction tx = manager.requestTransaction(new JtxTransactionMode().propagationSupports());
		assertTrue(tx.isNoTransaction());

		try {
			tx.commit();
		} catch (Exception ignore) {
			fail();
		}

		assertTrue(tx.isCommitted());

		try {
			tx.rollback();
			fail("exception is already committed!");
		} catch (Exception ignore) {
		}

		tx = manager.requestTransaction(new JtxTransactionMode().propagationSupports());

		try {
			tx.rollback();
		} catch (Exception ex) {
			fail(ex.toString());
		}

		tx = manager.requestTransaction(new JtxTransactionMode().propagationSupports());

		try {
			tx.setRollbackOnly();
		} catch (Exception ex) {
			fail(ex.toString());
		}
	}
}