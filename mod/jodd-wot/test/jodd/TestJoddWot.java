// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.db.DbTests;
import jodd.db.oom.DbOomTests;
import jodd.decora.DecoraTests;
import jodd.decora.parser.DecoraParserTests;
import jodd.jtx.JtxTests;
import jodd.lagarto.LagartoTests;
import jodd.madvoc.MadvocTests;
import jodd.paramo.ParamoTests;
import jodd.petite.PetiteTests;
import jodd.proxetta.ProxettaTests;
import jodd.vtor.VtorTests;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Jodd JUnit TestSuite.
 */
public class TestJoddWot {

	public static Test suite() {
		TestSuite suite = new TestSuite("Jodd WOT Java Library Test Suite");
		suite.addTest(DbTests.suite());
		suite.addTest(DbOomTests.suite());
		suite.addTest(JtxTests.suite());
		suite.addTest(PetiteTests.suite());
		suite.addTest(MadvocTests.suite());
		suite.addTest(ProxettaTests.suite());
		suite.addTest(ParamoTests.suite());
		suite.addTest(VtorTests.suite());
		suite.addTest(LagartoTests.suite());
		suite.addTest(DecoraTests.suite());
		suite.addTest(DecoraParserTests.suite());
		return suite;
	}
}
