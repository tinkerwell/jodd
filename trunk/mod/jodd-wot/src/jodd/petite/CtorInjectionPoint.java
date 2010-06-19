// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import java.lang.reflect.Constructor;

/**
 * Holder for constructor injection points.
 */
public class CtorInjectionPoint {

	public final Constructor constructor;
	public final String[] references;

	public CtorInjectionPoint(Constructor constructor, String[] references) {
		this.constructor = constructor;
		this.references = references;
	}
}
