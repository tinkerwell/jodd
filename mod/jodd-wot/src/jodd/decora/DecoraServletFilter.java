// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

import jodd.decora.parser.DecoraParser;
import jodd.log.Log;
import jodd.servlet.DispatcherUtil;
import jodd.servlet.wrapper.BufferResponseWrapper;
import jodd.servlet.wrapper.LastModifiedData;
import jodd.util.ClassLoaderUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.CharBuffer;

/**
 * Decora main filter.
 */
public class DecoraServletFilter implements Filter {

	private static final Log log = Log.getLogger(DecoraServletFilter.class);

	public static final String PARAM_DECORA_MANAGER = "decora.manager";
	public static final String PARAM_DECORA_PARSER = "decora.parser";

	protected DecoraManager decoraManager;
	protected DecoraParser decoraParser;

	/**
	 * Creates Decora manager. Override to provide custom decora manager.
	 * Alternatively, set it in filter init parameters.
	 */
	protected DecoraManager createDecoraManager() {
		return new DecoraManager();
	}

	/**
	 * Creates Decora parser. Override to provide custom decora parser.
	 */
	protected DecoraParser createDecoraParser() {
		return new DecoraParser();
	}

	/**
	 * Initializes Decora filter. Loads manager and parser from init parameters.
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		String decoraManagerClass = filterConfig.getInitParameter(PARAM_DECORA_MANAGER);

		if (decoraManagerClass != null) {
			try {
				Class decoraManagerType = ClassLoaderUtil.loadClass(decoraManagerClass);
				decoraManager = (DecoraManager) decoraManagerType.newInstance();
			} catch (Exception ex) {
				log.error("Unable to load Decora manager class: " + decoraManagerClass, ex);
				throw new ServletException(ex);
			}
		} else {
			decoraManager = createDecoraManager();
		}

		String decoraParserClass = filterConfig.getInitParameter(PARAM_DECORA_PARSER);

		if (decoraParserClass != null) {
			try {
				Class decoraParserType = ClassLoaderUtil.loadClass(decoraParserClass);
				decoraParser = (DecoraParser) decoraParserType.newInstance();
			} catch (Exception ex) {
				log.error("Unable to load Decora parser class: " + decoraParserClass, ex);
				throw new ServletException(ex);
			}
		} else {
			decoraParser = createDecoraParser();
		}
	}

	public void destroy() {
	}


	/**
	 * Creates HTTP request wrapper. By default returns {@link DecoraRequestWrapper}.
	 */
	protected HttpServletRequest wrapRequest(HttpServletRequest request) {
		return new DecoraRequestWrapper(request);
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;

		if (decoraManager.decorateRequest(request) == false) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		HttpServletRequest decoraRequest = wrapRequest(request);

		/* PROCESS PAGE */

		final LastModifiedData lastModifiedData = new LastModifiedData();

		DecoraResponseWrapper pageWrapper = new DecoraResponseWrapper(request, response, lastModifiedData, decoraManager);

		filterChain.doFilter(decoraRequest, pageWrapper);

		if (pageWrapper.isBufferingEnabled() == false) {
			// content was NOT buffered, so original request/response were used
			return;
		}

		char[] pageContent = pageWrapper.getBufferContentAsChars();

		if (pageContent == null || pageContent.length == 0) {
			// no page content
			return;
		}

		/* PROCESS DECORATOR */

        boolean decorated = false;

		// content was buffered, so try to decorate it

		String actionPath = DispatcherUtil.getServletPath(request);
		String decoratorPath = decoraManager.resolveDecorator(request, actionPath);
		if (decoratorPath != null) {
			BufferResponseWrapper decoratorWrapper = new BufferResponseWrapper(response, lastModifiedData);
			DispatcherUtil.forward(decoraRequest, decoratorWrapper, decoratorPath);
			char[] decoraContent = decoratorWrapper.getBufferedChars();

			decoraParser.decorate(servletResponse.getWriter(), pageContent, decoraContent);

			decorated = true;
		}

//		if (response.isCommitted() == false) {
//			pageWrapper.preResponseCommit();
//		}
		pageWrapper.commitResponse();

		/* DECORATOR NOT APPLIED, USE ORIGINAL RESPONSE (that is buffered) */

        if (!decorated) {
			if (pageWrapper.isBufferStreamBased()) {
				ServletOutputStream outputStream = response.getOutputStream();
				outputStream.write(pageWrapper.getBufferedBytes());
				outputStream.flush();
			} else {
				PrintWriter writer = response.getWriter();
				writer.append(CharBuffer.wrap(pageWrapper.getBufferedChars()));
				writer.flush();
			}
		}
	}

}
