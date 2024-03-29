// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.htmlstapler;

import jodd.bean.BeanUtil;
import jodd.datetime.TimeUtil;
import jodd.io.StreamUtil;
import jodd.lagarto.TagVisitor;
import jodd.lagarto.TagWriter;
import jodd.lagarto.adapter.StripHtmlTagAdapter;
import jodd.lagarto.filter.SimpleLagartoServletFilter;
import jodd.log.Log;
import jodd.servlet.DispatcherUtil;
import jodd.servlet.ServletUtil;
import jodd.util.MimeTypes;
import jodd.util.StringPool;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static jodd.lagarto.htmlstapler.HtmlStaplerBundlesManager.Strategy;

/**
 * HtmlStapler filter.
 * Part of the parameters are here, the other part is in the
 * {@link #createBundleManager(javax.servlet.ServletContext, jodd.lagarto.htmlstapler.HtmlStaplerBundlesManager.Strategy) bundle manager}.
 */
public class HtmlStaplerFilter extends SimpleLagartoServletFilter {

	private static final Log log = Log.getLogger(HtmlStaplerFilter.class);

	protected HtmlStaplerBundlesManager bundlesManager;

	protected boolean enabled = true;
	protected boolean stripHtml = true;
	protected boolean resetOnStart = true;
	protected boolean useGzip;
	protected int cacheMaxAge = TimeUtil.SECONDS_IN_DAY * 30;
	protected Strategy staplerStrategy = Strategy.RESOURCES_ONLY;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);

		bundlesManager = createBundleManager(filterConfig.getServletContext(), staplerStrategy);

		readFilterConfigParameters(filterConfig, this,
				"enabled",
				"stripHtml",
				"resetOnStart",
				"useGzip",
				"cacheMaxAge"
		);

		String staplerStrategyName = filterConfig.getInitParameter("strategy");
		if (staplerStrategyName != null) {
			if (staplerStrategyName.equalsIgnoreCase("ACTION_MANAGED")) {
				staplerStrategy = Strategy.ACTION_MANAGED;
			}
		}

		readFilterConfigParameters(filterConfig, bundlesManager,
				"bundleFolder",
				"downloadLocal",
				"localAddressAndPort",
				"localFilesEncoding",
				"notFoundExceptionEnabled",
				"sortResources",
				"staplerPath"
		);

		if (resetOnStart) {
			bundlesManager.reset();
		}
	}

	/**
	 * Reads filter config parameters and set into destination target.
	 */
	protected void readFilterConfigParameters(FilterConfig filterConfig, Object target, String... parameters) {
		for (String parameter : parameters) {
			String value = filterConfig.getInitParameter(parameter);

			if (value != null) {
				BeanUtil.setDeclaredProperty(target, parameter, value);
			}
		}
	}

	/**
	 * Creates {@link HtmlStaplerBundlesManager} instance.
	 */
	protected HtmlStaplerBundlesManager createBundleManager(ServletContext servletContext, Strategy strategy) {
		String webRoot = servletContext.getRealPath(StringPool.EMPTY);

		String contextPath = ServletUtil.getContextPath(servletContext);

		return new HtmlStaplerBundlesManager(contextPath, webRoot, strategy);
	}

	@Override
	protected LagartoParsingProcessor createParsingProcessor() {
		if (enabled == false) {
			return null;
		}

		return new LagartoParsingProcessor() {
			@Override
			protected char[] parse(TagWriter rootTagWriter, HttpServletRequest request) {

				TagVisitor visitor = rootTagWriter;

				if (stripHtml) {

					visitor = new StripHtmlTagAdapter(rootTagWriter) {
						@Override
						public void end() {
							super.end();
							if (log.isDebugEnabled()) {
								log.debug("Stripped: " + getStrippedCharsCount() + " chars.");
							}
						}
					};
				}

				String servletPath = DispatcherUtil.getServletPath(request);

				HtmlStaplerTagAdapter htmlStaplerTagAdapter =
						new HtmlStaplerTagAdapter(bundlesManager, servletPath, visitor);

				// todo add more adapters

				char[] content = invokeLagarto(htmlStaplerTagAdapter);

				return htmlStaplerTagAdapter.postProcess(content);
			}
		};
	}

	@Override
	protected boolean processActionPath(HttpServletRequest servletRequest, HttpServletResponse servletResponse, String actionPath) throws IOException {

		String bundlePath = '/' + bundlesManager.getStaplerPath() + '/';

		if (actionPath.startsWith(bundlePath) == false) {
			return false;
		}

		String bundleId = actionPath.substring(bundlePath.length());

		File file = bundlesManager.lookupBundleFile(bundleId);

		if (log.isDebugEnabled()) {
			log.debug("bundle: " + bundleId);
		}

		int ndx = bundleId.lastIndexOf('.');
		String extension = bundleId.substring(ndx + 1);

		String contentType = MimeTypes.getMimeType(extension);
		servletResponse.setContentType(contentType);

		if (useGzip && ServletUtil.isGzipSupported(servletRequest)) {
			file = bundlesManager.lookupGzipBundleFile(file);

			servletResponse.setHeader("Content-Encoding", "gzip");
		}

		if (file.exists() == false) {
			throw new IOException("bundle not found: " + bundleId);
		}

		servletResponse.setHeader("Content-Length", String.valueOf(file.length()));
		servletResponse.setHeader("Last-Modified", TimeUtil.formatHttpDate(file.lastModified()));

		if (cacheMaxAge > 0) {
			servletResponse.setHeader("Cache-Control", "max-age=" + cacheMaxAge);
		}

		sendBundleFile(servletResponse, file);

		return true;
	}

	/**
	 * Outputs bundle file to the response.
	 */
	protected void sendBundleFile(HttpServletResponse resp, File bundleFile) throws IOException {
		OutputStream out = resp.getOutputStream();
		StreamUtil.copy(new FileInputStream(bundleFile), out);
	}

}