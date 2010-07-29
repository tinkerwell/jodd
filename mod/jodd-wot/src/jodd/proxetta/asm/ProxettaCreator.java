// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.ProxettaException;
import jodd.util.ClassLoaderUtil;
import jodd.io.StreamUtil;

import java.io.InputStream;
import java.io.IOException;

/**
 * Creates the proxy subclass using ASM library.
 */
public class ProxettaCreator {

	private static final Logger log = LoggerFactory.getLogger(ProxettaCreator.class);

	// ---------------------------------------------------------------- ctor

	protected final ProxyAspect[] aspects;

	public ProxettaCreator(ProxyAspect... aspects) {
		this.aspects = aspects;
	}

	// ---------------------------------------------------------------- variable name

	/**
	 * Number appended to proxy class name, incremented on each use to make classnames unique
	 * in the system (e.g. classloader).
	 * @see #setUseVariableClassName(boolean)
 	 */
	protected static int suffixCounter;

	protected boolean useSuffix;

	protected String classNameSuffix;

	/**
	 * Specifies class name will vary on each creation. This prevents
	 * <code>java.lang.LinkageError: duplicate class definition.</code>
	 */
	public void setUseVariableClassName(boolean useVariableClassName) {
		useSuffix = useVariableClassName;
	}

	/**
	 * Specifies class name suffix for created class. If set to <code>null</code>
	 * suffix is not used.
	 */
	public void setClassNameSuffix(String classNameSuffix) {
		this.classNameSuffix = classNameSuffix;
	}

	/**
	 * Returns new suffix or <code>null</code> if suffix is not in use.
	 */
	protected String classNameSuffix() {
		if (useSuffix == false) {
			return classNameSuffix;
		}
		suffixCounter++;
		return classNameSuffix + suffixCounter;
	}


	// ---------------------------------------------------------------- accept

	protected ClassWriter destClassWriter;			// destination class writer
	protected boolean proxyApplied;
	protected String proxyClassName;

	/**
	 * Single point of class reader acceptance. Reads the target and creates destination class.
	 */
	protected ProxettaCreator accept(ClassReader cr, String reqProxyClassName) {
		if (log.isDebugEnabled()) {
			log.debug("Creating proxy for " + cr.getClassName());
		}
		// reads information
		TargetClassInfoReader targetClassInfoReader = new TargetClassInfoReader();
		cr.accept(targetClassInfoReader, 0);
		// create proxy
		this.destClassWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		ProxettaClassBuilder pcb = new ProxettaClassBuilder(destClassWriter, aspects, classNameSuffix(), reqProxyClassName, targetClassInfoReader);
		cr.accept(pcb, 0);
		proxyApplied = pcb.wd.proxyApplied;
		proxyClassName = pcb.wd.thisReference.replace('/', '.');
		return this;
	}

	public ProxettaCreator accept(InputStream in, String reqProxyClassName) {
		ClassReader cr;
		try {
			cr = new ClassReader(in);
		} catch (IOException ioex) {
			throw new ProxettaException("Error reading class input stream.", ioex);
		}
		return accept(cr, reqProxyClassName);
	}

	public ProxettaCreator accept(String targetName, String reqProxyClassName) {
		InputStream inputStream = null;
		try {
			inputStream = ClassLoaderUtil.getClassAsStream(targetName);
			return accept(inputStream, reqProxyClassName);
		} catch (IOException ioex) {
			throw new ProxettaException("Unable to open stream for class name: " + targetName, ioex);
		} finally {
			StreamUtil.close(inputStream);
		}
	}

	public ProxettaCreator accept(Class target, String reqProxyClassName) {
		InputStream inputStream = null;
		try {
			inputStream = ClassLoaderUtil.getClassAsStream(target);
			return accept(inputStream, reqProxyClassName);
		} catch (IOException ioex) {
			throw new ProxettaException("Unable to open stream for: " + target.getName(), ioex);
		} finally {
			StreamUtil.close(inputStream);
		}
	}


	// ---------------------------------------------------------------- after

	/**
	 * Checks if proxy is created and throws an exception if not.
	 */
	protected void checkAccepted() {
		if (destClassWriter == null) {
			throw new ProxettaException("Target not accepted yet!");
		}
	}

	/**
	 * Returns raw bytecode.
	 */
	public byte[] toByteArray() {
		checkAccepted();
		return destClassWriter.toByteArray();
	}

	/**
	 * Returns <code>true</code> if at least one method was wrapped.
	 */
	public boolean isProxyApplied() {
		checkAccepted();
		return proxyApplied;
	}

	/**
	 * Returns proxy class name.
	 */
	public String getProxyClassName() {
		checkAccepted();
		return proxyClassName;
	}


}