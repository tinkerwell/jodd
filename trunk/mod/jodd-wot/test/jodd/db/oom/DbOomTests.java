// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import junit.framework.TestSuite;
import junit.framework.Test;
import jodd.db.oom.sqlgen.DbEntitySqlTest;

public class DbOomTests extends TestSuite {

	public DbOomTests() {
		super("jodd.db.orm test suite");
		addTestSuite(DbNameUtilTest.class);
		addTestSuite(DbOomTest.class);
		addTestSuite(DbOom2Test.class);
		addTestSuite(DbSqlTemplateTest.class);
		addTestSuite(DbSqlTemplateWithPrefixTest.class);
		addTestSuite(DbSqlBuilderTest.class);
		addTestSuite(JointHintResolverTest.class);
		addTestSuite(DbEntitySqlTest.class);
		addTestSuite(MappingTest.class);
		addTestSuite(DbNoTableTest.class);
	}

	public static Test suite() {
		return new DbOomTests();
	}

}
