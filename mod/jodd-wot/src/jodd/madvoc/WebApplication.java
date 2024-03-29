// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.log.Log;
import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionPathMapper;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.component.ActionPathRewriter;
import jodd.madvoc.component.ContextInjector;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.petite.PetiteContainer;

import javax.servlet.ServletContext;
import java.lang.reflect.Modifier;
import java.util.Properties;

import jodd.props.Props;

/**
 * Web application contains all configurations and holds all managers and controllers of one web application.
 * Custom implementations may override this class to enhance several different functionality.
 */
public class WebApplication {

	private static Log log;

	public static final String MADVOC_CONTAINER_NAME = "madpc";

	protected PetiteContainer madpc;

	/**
	 * Creates web application. Application is not initialized.
	 * @see #WebApplication(boolean)
	 */
	public WebApplication() {
	}

	/**
	 * Creates web application and optionally {@link #initWebApplication() initializes} it.
	 */
	public WebApplication(boolean init) {
		if (init) {
			initWebApplication();
		}
	}

	/**
	 * Initializes web application. Invoked very first.
	 * By default, it creates a Logger and creates internal Petite container.
	 * Also adds itself into it.
	 */
	protected void initWebApplication() {
		log = Log.getLogger(WebApplication.class);
		if (log.isDebugEnabled()) {
			log.debug("Initializing Madvoc web application");
		}
		madpc = new PetiteContainer();
		madpc.addSelf(MADVOC_CONTAINER_NAME);
	}

	// ---------------------------------------------------------------- components

	/**
	 * Resolves the name of the last base non-abstract subclass for provided component.
	 * It iterates all subclasses up to the <code>Object</cde> and declares the last
	 * non-abstract class as base component. Component name will be resolved from the
	 * founded base component.
	 */
	private String resolveBaseComponentName(Class component) {
		Class lastComponent = component;
		while (true) {
			Class superClass = component.getSuperclass();
			if (superClass.equals(Object.class)) {
				break;
			}
			component = superClass;
			if (Modifier.isAbstract(component.getModifiers()) == false) {
				lastComponent = component;
			}
		}
		return madpc.resolveBeanName(lastComponent);
	}

	/**
	 * Registers component using its {@link #resolveBaseComponentName(Class) base name}.
	 * Previously defined component will be removed.
	 * @see #registerComponent(Object) 
	 */
	public final void registerComponent(Class component) {
		String name = resolveBaseComponentName(component);
		registerComponent(name, component);
	}

	public final void registerComponent(String name, Class component) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component '" + name + "' of type " + component.getName());
		}
		madpc.removeBean(name);
		madpc.registerBean(name, component);
	}

	/**
	 * Registers component instance using its {@link #resolveBaseComponentName(Class) base name}.
	 * Previously defined component will be removed.
	 * @see #registerComponent(Class)
	 */
	public final void registerComponent(Object componentInstance) {
		Class component = componentInstance.getClass();
		String name = resolveBaseComponentName(component);
		registerComponent(name, componentInstance);
	}

	/**
	 * Registers component instance and wires it with internal container.
	 */
	public final void registerComponent(String name, Object componentInstance) {
		if (log.isDebugEnabled()) {
			log.debug("Registering component '" + name + "' instance of " + componentInstance.getClass().getName());
		}
		madpc.removeBean(name);
		madpc.addBean(name, componentInstance);
	}

	/**
	 * Returns registered component. Should be used only in special cases.
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T getComponent(Class<T> component) {
		String name = resolveBaseComponentName(component);
		return (T) madpc.getBean(name);
	}

	/**
	 * Returns registered component.
	 */
	public Object getComponent(String componentName) {
		return madpc.getBean(componentName);
	}

	/**
	 * Registers default Madvoc components.
	 * Invoked before {@link #init(MadvocConfig , ServletContext) madvoc initialization}.
	 */
	public void registerMadvocComponents() {
		if (log.isDebugEnabled()) {
			log.debug("Registering Madvoc components");
		}
		registerComponent(ActionMethodParser.class);
		registerComponent(ActionPathMapper.class);
		registerComponent(ActionPathRewriter.class);
		registerComponent(ActionsManager.class);
		registerComponent(InterceptorsManager.class);
		registerComponent(MadvocConfig.class);
		registerComponent(MadvocController.class);
		registerComponent(ResultsManager.class);
		registerComponent(ResultMapper.class);
		registerComponent(ContextInjector.class);
	}


	// ---------------------------------------------------------------- lifecycle

	/**
	 * Initialized web application parameters. Provided properties object is always non-<code>null</code>.
	 * Simple defines parameters for internal container.
	 */
	protected void defineParams(Properties properties) {
		if (log.isDebugEnabled()) {
			log.debug("Defining Madvoc parameters");
		}
		madpc.defineParameters(properties);
	}

	protected void defineParams(Props props) {
		if (log.isDebugEnabled()) {
			log.debug("Defining Madvoc parameters");
		}
		madpc.defineParameters(props);
	}

	/**
	 * Initializes web application custom configuration.
	 * When running web application out from container,
	 * <code>servletContext</code> may be null
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected void init(MadvocConfig madvocConfig, ServletContext servletContext) {
		if (log.isDebugEnabled()) {
			log.debug("Initializing Madvoc");
		}
	}

	/**
	 * Hook for manually registered actions.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected void initActions(ActionsManager actionManager) {
		if (log.isDebugEnabled()) {
			log.debug("Initializing Madvoc actions");
		}
	}

	/**
	 * Hook for manually registered results.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected void initResults(ResultsManager actionManager) {
		log.debug("Initializing Madvoc results");
	}

	/**
	 * Invoked on web application destroy.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected void destroy(MadvocConfig madvocConfig) {
		if (log.isDebugEnabled()) {
			log.debug("Destroying Madvoc");
		}
	}


	// ---------------------------------------------------------------- configurator

	/**
	 * Wires configurator in the the Madvoc container and invokes configuration.
	 */
	public void configure(MadvocConfigurator configurator) {
		if (log.isDebugEnabled()) {
			log.debug("Configuring Madvoc");
		}
		madpc.wire(configurator);
		configurator.configure();
	}
}
