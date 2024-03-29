// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.util.StringPool;

/**
 * Jodd library global defaults. They are used in more then one place (class, package).
 */
public class JoddDefault {

	/**
	 * Jodd version.
	 */
	public static final String JODD_VERSION;

	/**
	 * Jodd root package.
	 */
	public static final String JODD_PACKAGE_NAME;

	/**
	 * Default temp file prefix.
	 */
	public static String tempFilePrefix = "jodd-";

	/**
	 * Default file encoding (UTF8).
	 */
	public static String encoding = StringPool.UTF_8;

	/**
	 * Default IO buffer size (16 KB).
	 */
	public static int ioBufferSize = 16384;

	static {
		Package pkg = Jodd.class.getPackage();
		JODD_VERSION = pkg.getImplementationVersion();
		JODD_PACKAGE_NAME = pkg.getName();
	}

}
