// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.servlet.upload.FileUploadFactory;
import jodd.servlet.upload.impl.AdaptiveFileUploadFactory;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.madvoc.result.ServletDispatcherResult;
import jodd.madvoc.injector.RequestScopeInjector;
import jodd.util.StringPool;

import java.util.Map;
import java.util.HashMap;

/**
 * Madvoc configuration. This is the single place where component configuration is stored.
 * New custom component that requires configuration may override and enhance this config
 * with new configuration. 
 */
public class MadvocConfig {

	@SuppressWarnings({"unchecked"})
	public MadvocConfig() {
		encoding = StringPool.UTF_8;
		fileUploadFactory = new AdaptiveFileUploadFactory();
		defaultResultType = ServletDispatcherResult.NAME;
		defaultInterceptors = new Class[] {ServletConfigInterceptor.class};
		defaultActionMethodNames = new String[] {"view", "execute"};
		pathAliases = new HashMap<String, String>();
		createDefaultAliases = false;
		defaultExtension = "html";
		supplementAction = null;//DefaultActionSupplement.class;
		rootPackage = null;
		detectDuplicatePathsEnabled = true;
		actionPathMappingEnabled = false;
		preventCaching = true;
		requestScopeInjectorConfig = new RequestScopeInjector.Config();
	}

	// ---------------------------------------------------------------- encoding

	protected String encoding;

	/**
	 * Returns character encoding.
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * Sets web application character encoding. If set to <code>null</code> encoding will be not applied.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}


	// ---------------------------------------------------------------- file upload factory

	protected FileUploadFactory fileUploadFactory;

	/**
	 * Returns file upload factory.
	 */
	public FileUploadFactory getFileUploadFactory() {
		return fileUploadFactory;
	}

	/**
	 * Specifies file upload factory.
	 */
	public void setFileUploadFactory(FileUploadFactory fileUploadFactory) {
		this.fileUploadFactory = fileUploadFactory;
	}

	// ---------------------------------------------------------------- path

	protected String defaultExtension;
	protected String[] defaultActionMethodNames;

	/**
	 * Returns default action extension.
	 */
	public String getDefaultExtension() {
		return defaultExtension;
	}

	public void setDefaultExtension(String defaultExtension) {
		this.defaultExtension = defaultExtension;
	}

	/**
	 * Returns default action method names which will have empty method path.
	 */
	public String[] getDefaultActionMethodNames() {
		return defaultActionMethodNames;
	}

	public void setDefaultActionMethodNames(String... defaultActionMethodNames) {
		this.defaultActionMethodNames = defaultActionMethodNames;
	}

	// ---------------------------------------------------------------- default interceptors

	protected Class<? extends ActionInterceptor>[] defaultInterceptors;

	/**
	 * Returns default interceptors.
	 */
	public Class<? extends ActionInterceptor>[] getDefaultInterceptors() {
		return defaultInterceptors;
	}

	/**
	 * Set default interceptors.
	 */
	public void setDefaultInterceptors(Class<? extends ActionInterceptor>... defaultInterceptors) {
		this.defaultInterceptors = defaultInterceptors;
	}

	// ---------------------------------------------------------------- default result type

	protected String defaultResultType;

	/**
	 * Specifies default result type.
	 */
	public void setDefaultResultType(String type) {
		defaultResultType = type;
	}

	/**
	 * Returns default action result type.
	 */
	public String getDefaultResultType() {
		return defaultResultType;
	}

	// ---------------------------------------------------------------- path aliases

	protected Map<String, String> pathAliases;
	protected boolean createDefaultAliases;

	/**
	 * Registers new path alias.
	 */
	public void registerPathAlias(String alias, String path) {
		pathAliases.put(alias, path);
	}

	/**
	 * Returns path alias.
	 */
	public String lookupPathAlias(String alias) {
		if (pathAliases.isEmpty()) {
			return null;
		}
		return pathAliases.get(alias);
	}

	/**
	 * Reset all aliases.
	 */
	public void resetPathAliases() {
		pathAliases.clear();
	}

	public boolean isCreateDefaultAliases() {
		return createDefaultAliases;
	}

	/**
	 * Specifies if default aliases should be created for all
	 * action paths.
	 */
	public void setCreateDefaultAliases(boolean createDefaultAliases) {
		this.createDefaultAliases = createDefaultAliases;
	}

	// ---------------------------------------------------------------- supplement action

	protected Class supplementAction;

	/**
	 * Returns supplement action class for action requests that are not registered explicitly.
	 */
	public Class getSupplementAction() {
		return supplementAction;
	}

	/**
	 * Specifies new supplement action. If set to <code>null</code> supplement actions
	 * will not be used.
	 */
	public void setSupplementAction(Class supplementAction) {
		this.supplementAction = supplementAction;
	}

	/**
	 * Disable supplement actions.
	 */
	public void disableSupplementAction() {
		this.supplementAction = null;
	}

	// ---------------------------------------------------------------- packageRoot

	protected String rootPackage;

	/**
	 * Returns root package.
	 */
	public String getRootPackage() {
		return rootPackage;
	}

	/**
	 * Sets root package.
	 */
	public void setRootPackage(String rootPackage) {
		this.rootPackage = rootPackage;
	}

	/**
	 * Sets root package equals to package of provided class.
	 */
	public void setRootPackageOf(Class clazz) {
		setRootPackage(clazz.getPackage().getName());
	}


	// ---------------------------------------------------------------- duplicates

	protected boolean detectDuplicatePathsEnabled;

	public boolean isDetectDuplicatePathsEnabled() {
		return detectDuplicatePathsEnabled;
	}

	public void setDetectDuplicatePathsEnabled(boolean detectDuplicatePathsEnabled) {
		this.detectDuplicatePathsEnabled = detectDuplicatePathsEnabled;
	}

	// ---------------------------------------------------------------- mapping

	protected boolean actionPathMappingEnabled;

	public boolean isActionPathMappingEnabled() {
		return actionPathMappingEnabled;
	}

	public void setActionPathMappingEnabled(boolean actionPathMappingEnabled) {
		this.actionPathMappingEnabled = actionPathMappingEnabled;
	}

	// ---------------------------------------------------------------- caching

	protected boolean preventCaching;

	public boolean isPreventCaching() {
		return preventCaching;
	}

	/**
	 * Specifies if Madvoc should add response params to prevent browser caching.
	 */
	public void setPreventCaching(boolean preventCaching) {
		this.preventCaching = preventCaching;
	}

	// ---------------------------------------------------------------- request

	protected RequestScopeInjector.Config requestScopeInjectorConfig;

	public RequestScopeInjector.Config getRequestScopeInjectorConfig() {
		return requestScopeInjectorConfig;
	}

	/**
	 * Specifies {@link jodd.madvoc.injector.RequestScopeInjector request scope injector} configuration.
	 */
	public void setRequestScopeInjectorConfig(RequestScopeInjector.Config requestScopeInjectorConfig) {
		this.requestScopeInjectorConfig = requestScopeInjectorConfig;
	}
}