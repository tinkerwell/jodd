// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import javax.servlet.http.HttpServletRequest;

/**
 * Rewrites action path.
 */
public class ActionPathRewriter {

	/**
	 * Rewrites action path.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	public String rewrite(String actionPath, HttpServletRequest servletRequest, String httpMethod) {
		return actionPath;
	}
}
