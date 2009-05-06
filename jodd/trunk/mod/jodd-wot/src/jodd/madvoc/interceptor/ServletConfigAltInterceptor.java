// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.injector.SessionScopeInjector;
import jodd.madvoc.injector.RequestScopeInjector;

/**
 * Alternative servlet configurator that first copies all request parameters to attributes and inject only attributes. 
 * @see jodd.madvoc.interceptor.ServletConfigInterceptor
 */
public class ServletConfigAltInterceptor extends ServletConfigInterceptor {

	@Override
	public void init() {
		requestInjector = new RequestScopeInjector(scopeDataManager).copyParamsToAttributes(true).injectParameters(false);
		sessionInjector = new SessionScopeInjector(scopeDataManager);
	}

}
