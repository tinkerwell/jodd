// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.interceptor;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.injector.ActionPathMacroInjector;
import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.injector.ServletContextScopeInjector;
import jodd.madvoc.injector.SessionScopeInjector;
import jodd.madvoc.injector.ApplicationScopeInjector;
import jodd.madvoc.injector.MadvocContextScopeInjector;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ContextInjector;
import jodd.madvoc.meta.In;
import jodd.servlet.ServletUtil;
import jodd.servlet.upload.MultipartRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Configures actions and applies some servlet configuration prior action execution.
 * This interceptor does the following:
 * <ul>
 * <li>uses multi-part request if needed
 * <li>performs the injection (using either default or specified injector)
 * <li>invokes the action
 * <li>performs the outjection.
 * </ul>
 */
public class ServletConfigInterceptor extends ActionInterceptor {

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	@In(scope = ScopeType.CONTEXT)
	protected ContextInjector contextInjector;

	protected RequestScopeInjector requestScopeInjector;
	protected SessionScopeInjector sessionScopeInjector;
	protected ApplicationScopeInjector applicationScopeInjector;
	protected MadvocContextScopeInjector madvocContextScopeInjector;
	protected ServletContextScopeInjector servletContextScopeInjector;
	protected ActionPathMacroInjector actionPathMacroInjector;

	@Override
	public void init() {
		requestScopeInjector = new RequestScopeInjector(madvocConfig);
		sessionScopeInjector = new SessionScopeInjector();
		applicationScopeInjector = contextInjector.getApplicationScopeInjector();
		madvocContextScopeInjector = contextInjector.getMadvocContextScopeInjector();
		servletContextScopeInjector = contextInjector.getServletContextScopeInjector();
		actionPathMacroInjector = new ActionPathMacroInjector();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object intercept(ActionRequest actionRequest) throws Exception {
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();

		// detect multipart request
		if (ServletUtil.isMultipartRequest(servletRequest)) {
			servletRequest = new MultipartRequestWrapper(servletRequest, madvocConfig.getFileUploadFactory(), madvocConfig.getEncoding());
			actionRequest.setHttpServletRequest(servletRequest);
		}

		// do it
		inject(actionRequest);
		Object result = actionRequest.invoke();
		outject(actionRequest);
		return result;
	}

	/**
	 * Performs injection.
	 */
	protected void inject(ActionRequest actionRequest) {
		Object target = actionRequest.getAction();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();

		servletContextScopeInjector.inject(target, servletRequest, servletResponse);
		madvocContextScopeInjector.inject(target);
		applicationScopeInjector.inject(target, servletRequest.getSession().getServletContext());
		sessionScopeInjector.inject(target, servletRequest);
		requestScopeInjector.prepare(servletRequest);
		requestScopeInjector.inject(target, servletRequest);
		actionPathMacroInjector.inject(target, actionRequest);
	}

	/**
	 * Performs outjection.
	 */
	protected void outject(ActionRequest actionRequest) {
		Object target = actionRequest.getAction();
		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();

		servletContextScopeInjector.outject(target, servletResponse);
		madvocContextScopeInjector.outject(target);
		applicationScopeInjector.outject(target, servletRequest.getSession().getServletContext());
		sessionScopeInjector.outject(target, servletRequest);
		requestScopeInjector.outject(target, servletRequest);
	}


}