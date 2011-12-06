// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.interceptor.ActionInterceptorStack;
import jodd.madvoc.interceptor.EchoInterceptor;

public class MyInterceptorStack extends ActionInterceptorStack {

	public MyInterceptorStack() {
		super(EchoInterceptor.class, MyServletConfigInterceptor.class);
	}
}
