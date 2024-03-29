// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import sun.reflect.Reflection;

import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Utilities to manipulate class path, define and find classes etc.
 */
public class ClassLoaderUtil {

	// ---------------------------------------------------------------- default class loader

	/**
	 * Returns default class loader. By default, it is thread context class loader.
	 * If this one is <code>null</code> then class loader that loaded this class is
	 * returned.
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = ClassLoaderUtil.class.getClassLoader();
		}
		return cl;
	}

	/**
	 * Returns system class loader.
	 */
	public static ClassLoader getSystemClassLoader() {
		return ClassLoader.getSystemClassLoader();
	}

	// ---------------------------------------------------------------- add class path

	/**
	 * Adds additional file or path to classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, ClassLoader)
	 */
	public static void addFileToClassPath(String path, ClassLoader classLoader) {
		addFileToClassPath(new File(path), classLoader);
	}

	/**
	 * Adds additional file or path to classpath during runtime.
	 * @see #addUrlToClassPath(java.net.URL, ClassLoader)
	 */
	public static void addFileToClassPath(File path, ClassLoader classLoader) {
		try {
			addUrlToClassPath(path.toURL(), classLoader);
		} catch (MalformedURLException muex) {
			throw new IllegalArgumentException("Invalid path: " + path, muex);
		}
	}

	/**
	 * Adds the content pointed by the URL to the classpath during runtime.
	 * Uses reflection since <code>addURL</code> method of
	 * <code>URLClassLoader</code> is protected.
	 */
	public static void addUrlToClassPath(URL url, ClassLoader classLoader) {
		try {
			ReflectUtil.invokeDeclared(URLClassLoader.class, classLoader, "addURL",
					new Class[]{URL.class}, new Object[]{url});
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unable to add URL: " + url, ex);
		}
	}


	// ---------------------------------------------------------------- define class

	/**
	 * Defines a class from byte array into the system class loader.
	 * @see #defineClass(String, byte[], ClassLoader) 
	 */
	public static Class defineClass(byte[] classData) {
		return defineClass(null, classData, getDefaultClassLoader());
	}

	/**
	 * Defines a class from byte array into the system class loader.
	 * @see #defineClass(String, byte[], ClassLoader)
	 */
	public static Class defineClass(String className, byte[] classData) {
		return defineClass(className, classData, getDefaultClassLoader());
	}

	/**
	 * Defines a class from byte array into the specified class loader.
	 * @see #defineClass(String, byte[], ClassLoader)
	 */
	public static Class defineClass(byte[] classData, ClassLoader classLoader) {
		return defineClass(null, classData, classLoader);
	}

	/**
	 * Defines a class from byte array into the specified class loader.
	 * Warning: this is a <b>hack</b>!
	 * @param className optional class name, may be <code>null</code>
	 * @param classData bytecode data
	 * @param classLoader classloader that will load class
	 */
	public static Class defineClass(String className, byte[] classData, ClassLoader classLoader) {
		try {
			return (Class) ReflectUtil.invokeDeclared(ClassLoader.class, classLoader, "defineClass",
					new Class[]{String.class, byte[].class, int.class, int.class},
					new Object[]{className, classData, new Integer(0), new Integer(classData.length)});
		} catch (Throwable th) {
			throw new RuntimeException("Unable to define class: " + className, th);
		}
	}

	// ---------------------------------------------------------------- find class

	/**
	 * @see #findClass(String, java.net.URL[], ClassLoader)
	 */
	public static Class findClass(String className, File[] classPath) {
		URL[] urls = new URL[classPath.length];
		for (int i = 0; i < classPath.length; i++) {
			File file = classPath[i];
			try {
				urls[i] = file.toURL();
			} catch (MalformedURLException ignore) {
			}
		}
		return findClass(className, urls, null);
	}


	/**
	 * @see #findClass(String, java.net.URL[], ClassLoader)
	 */
	public static Class findClass(String className, URL[] classPath) {
		return findClass(className, classPath, null);
	}

	/**
	 * Finds and loads class on classpath even if it was already loaded.
	 */
	public static Class findClass(String className, URL[] classPath, ClassLoader parent) {
		URLClassLoader tempClassLoader = parent != null ? new URLClassLoader(classPath, parent) : new URLClassLoader(classPath);
		try {
			return (Class) ReflectUtil.invokeDeclared(URLClassLoader.class, tempClassLoader, "findClass",
					new Class[] {String.class},
					new Object[] {className});
		} catch (Throwable th) {
			throw new RuntimeException("Unable to find class: " + className, th);
		}
	}


	// ---------------------------------------------------------------- classpath

	private static final String[] MANIFESTS = {"Manifest.mf", "manifest.mf", "MANIFEST.MF"};

	/**
	 * Finds <b>tools.jar</b>. Returns <code>null</code> if does not exist.
	 */
	public static File findToolsJar() {
		String tools = new File(SystemUtil.getJavaHome()).getAbsolutePath() + File.separatorChar + "lib" + File.separatorChar + "tools.jar";
		File toolsFile = new File(tools);
		if (toolsFile.exists()) {
			return toolsFile;
		}
		return null;
	}

	/**
	 * Returns classpath item manifest or <code>null</code> if not found.
	 */
	public static Manifest getClasspathItemManifest(File classpathItem) {
		Manifest manifest = null;

		if (classpathItem.isFile()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(classpathItem);
				JarFile jar = new JarFile(classpathItem);
				manifest = jar.getManifest();
			} catch (IOException ignore) {
			}
			finally {
				StreamUtil.close(fis);
			}
		} else {
			File metaDir = new File(classpathItem, "META-INF");
			File manifestFile = null;
			if (metaDir.isDirectory()) {
				for (String m : MANIFESTS) {
					File mFile = new File(metaDir, m);
					if (mFile.isFile() == true) {
						manifestFile = mFile;
						break;
					}
				}
			}
			if (manifestFile != null) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(manifestFile);
					manifest = new Manifest(fis);
				} catch (IOException ignore) {
				}
				finally {
					StreamUtil.close(fis);
				}
			}
		}

		return manifest;
	}

	/**
	 * Returns base folder for classpath item. If item is a (jar) file,
	 * its parent is returned. If item is a directory, its name is returned.
	 */
	public static String getClasspathItemBaseDir(File classpathItem) {
		String base;
		if (classpathItem.isFile()) {
			base = classpathItem.getParent();
		} else {
			base = classpathItem.toString();
		}
		return base;
	}

	/**
	 * Returns default classpath using
	 * {@link #getDefaultClassLoader() default classloader}.
	 */
	public static File[] getDefaultClasspath() {
		return getDefaultClasspath(getDefaultClassLoader());
	}

	/**
	 * Returns default class path from all available <code>URLClassLoader</code>
	 * in classloader hierarchy. The following is added to the classpath list:
	 * <li>file URLs from <code>URLClassLoader</code> (other URL protocols are ignored)
	 * <li>inner entries from containing <b>manifest</b> files (if exist)
	 * <li>bootstrap classpath
	 */
	public static File[] getDefaultClasspath(ClassLoader classLoader) {
		Set<File> classpaths = new HashSet<File>();

		while (classLoader != null) {
			if (classLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) classLoader).getURLs();
				for (URL u : urls) {
					File f = FileUtil.toFile(u);
					if (f != null) {
						try {
							f = f.getCanonicalFile();
							classpaths.add(f);
							addInnerClasspathItems(classpaths, f);
						} catch (IOException ignore) {
						}
					}
				}
			}
			classLoader = classLoader.getParent();
		}

		String bootstrap = SystemUtil.getSunBoothClassPath();
		if (bootstrap != null) {
			classpaths.add(new File(bootstrap));
		}

		File[] result = new File[classpaths.size()];
		return classpaths.toArray(result);
	}

	private static void addInnerClasspathItems(Set<File> classpaths, File item) {

		Manifest manifest = getClasspathItemManifest(item);
		if (manifest == null) {
			return;
		}

		Attributes attributes = manifest.getMainAttributes();
		if (attributes == null) {
			return;
		}

		String s = attributes.getValue(Attributes.Name.CLASS_PATH);
		if (s == null) {
			return;
		}

		String base = getClasspathItemBaseDir(item);

		String[] tokens = StringUtil.splitc(s, ' ');
		for (String t : tokens) {
			try {
				File file = new File(base, t);
				file = file.getCanonicalFile();

				if (file.exists()) {
					classpaths.add(file);
				}
			} catch (IOException ignore) {
			}
		}
	}


	// ---------------------------------------------------------------- get resource

	/**
	 * Retrieves given resource as URL.
	 * @see #getResourceUrl(String, ClassLoader)
	 */
	public static URL getResourceUrl(String resourceName) {
		return getResourceUrl(resourceName, null);
	}

	/**
	 * Retrieves given resource as URL. Resource is always absolute and may
	 * starts with a slash character.
	 * <p>
	 * Resource will be loaded using class loaders in the following order:
	 * <ul>
	 * <li>{@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
	 * <li>{@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}
	 * <li>if <code>callingClass</code> is provided: {@link Class#getClassLoader() callingClass.getClassLoader()}
	 * </ul>
	 */
	public static URL getResourceUrl(String resourceName, ClassLoader classLoader) {

		if (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		
		URL resourceUrl;

		// try #1 - using provided class loader
		if (classLoader != null) {
			resourceUrl = classLoader.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
		}

		// try #2 - using thread class loader
		ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
		if ((currentThreadClassLoader != null) && (currentThreadClassLoader != classLoader)) {
			resourceUrl = currentThreadClassLoader.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
		}

		// try #3 - using caller classloader, similar as Class.forName()
		Class callerClass = Reflection.getCallerClass(2);
		ClassLoader callerClassLoader = callerClass.getClassLoader();

		if ((callerClassLoader != classLoader) && (callerClassLoader != currentThreadClassLoader)) {
			resourceUrl = callerClassLoader.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
		}

		return null;
	}

	// ---------------------------------------------------------------- get resource file

	/**
	 * Retrieves resource as file.
	 * @see #getResourceFile(String) 
	 */
	public static File getResourceFile(String resourceName) {
		return getResourceFile(resourceName, null);
	}

	/**
	 * Retrieves resource as file. Resource is retrieved as {@link #getResourceUrl(String, ClassLoader) URL},
	 * than it is converted to URI so it can be used by File constructor.
	 */
	public static File getResourceFile(String resourceName, ClassLoader classLoader) {
		try {
			return new File(getResourceUrl(resourceName, classLoader).toURI());
		} catch (URISyntaxException ignore) {
			return null;
		}
	}

	// ---------------------------------------------------------------- get resource stream

	/**
	 * Opens a resource of the specified name for reading.
	 * @see #getResourceAsStream(String, ClassLoader)
	 */
	public static InputStream getResourceAsStream(String resourceName) throws IOException {
		return getResourceAsStream(resourceName, null);
	}

	/**
	 * Opens a resource of the specified name for reading.
	 * @see #getResourceUrl(String, ClassLoader)
	 */
	public static InputStream getResourceAsStream(String resourceName, ClassLoader callingClass) throws IOException {
		URL url = getResourceUrl(resourceName, callingClass);
		if (url != null) {
			return url.openStream();
		}
		return null;
	}

	/**
	 * Opens a class of the specified name for reading using class classloader.
	 * @see #getResourceAsStream(String, ClassLoader)
	 */
	public static InputStream getClassAsStream(Class clazz) throws IOException {
		return getResourceAsStream(getClassFileName(clazz), clazz.getClassLoader());
	}

	/**
	 * Opens a class of the specified name for reading. No specific classloader is used
	 * for loading class.
	 * @see #getResourceAsStream(String, ClassLoader)
	 */
	public static InputStream getClassAsStream(String className) throws IOException {
		return getResourceAsStream(getClassFileName(className));
	}

	// ---------------------------------------------------------------- load class

	/**
	 * List of primitive type names.
	 */
	public static final String[] PRIMITIVE_TYPE_NAMES = new String[] {
			"boolean", "byte", "char", "double", "float", "int", "long", "short",
	};
	/**
	 * List of primitive types that matches names list.
	 */
	public static final Class[] PRIMITIVE_TYPES = new Class[] {
			boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class,
	};
	/**
	 * List of primitive bytecode characters that matches names list.
	 */
	public static final char[] PRIMITIVE_BYTECODE_NAME = new char[] {
			'Z', 'B', 'C', 'D', 'F', 'I', 'J', 'S'
	};

	/**
	 * Prepares classname for loading.
	 */
	public static String prepareClassnameForLoading(String className) {
		int bracketCount = StringUtil.count(className, '[');
		if (bracketCount == 0) {
			return className;
		}

		String brackets = StringUtil.repeat('[', bracketCount);

		int bracketIndex = className.indexOf('[');
		className = className.substring(0, bracketIndex);

		int primitiveNdx = getPrimitiveClassNameIndex(className);
		if (primitiveNdx >= 0) {
			className = String.valueOf(PRIMITIVE_BYTECODE_NAME[primitiveNdx]);

			return brackets + className;
		} else {
			return brackets + 'L' + className + ';';
		}
	}

	/**
	 * Detects if provided class name is a primitive type.
	 * Returns >= 0 number if so.
	 */
	private static int getPrimitiveClassNameIndex(String className) {
		int dotIndex = className.indexOf('.');
		if (dotIndex != -1) {
			return -1;
		}
		return Arrays.binarySearch(PRIMITIVE_TYPE_NAMES, className);
	}

	public static Class loadClass(String className) throws ClassNotFoundException {
		return loadClass(className, null);
	}
	
	/**
	 * Loads a class with a given name dynamically, more reliable then <code>Class.forName</code>.
	 * <p>
	 * Class will be loaded using class loaders in the following order:
	 * <ul>
	 * <li>provided class loader (if any)
	 * <li><code>Thread.currentThread().getContextClassLoader()}</code>
	 * <li>caller classloader
	 * <li>using <code>Class.forName</code>
	 * </ul>
	 */
	public static Class loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {

		className = prepareClassnameForLoading(className);
		
		if ((className.indexOf('.') == -1) || (className.indexOf('[') == -1)) {
			// maybe a primitive
			int primitiveNdx = getPrimitiveClassNameIndex(className);
			if (primitiveNdx >= 0) {
				return PRIMITIVE_TYPES[primitiveNdx];
			}
		}

		// try #1 - using provided class loader
		try {
			if (classLoader != null) {
				return classLoader.loadClass(className);
			}
		} catch (ClassNotFoundException ignore) {
		}

		// try #2 - using thread class loader
		ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
		if ((currentThreadClassLoader != null) && (currentThreadClassLoader != classLoader)) {
			try {
				return currentThreadClassLoader.loadClass(className);
			} catch (ClassNotFoundException ignore) {
			}
		}
		
		// try #3 - using caller classloader, similar as Class.forName()
		Class callerClass = Reflection.getCallerClass(2);
		ClassLoader callerClassLoader = callerClass.getClassLoader();

		if ((callerClassLoader != classLoader) && (callerClassLoader != currentThreadClassLoader)) {
			try {
				return callerClassLoader.loadClass(className);
			} catch (ClassNotFoundException ignore) {
			}
		}

		// try #4 - using Class.forName(). We must use this since for JDK >= 6
		// arrays will be not loaded using classloader, but only with forName.
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException ignore) {
		}

		throw new ClassNotFoundException("Class not found: " + className);
	}

	// ---------------------------------------------------------------- misc


	/**
	 * Resolves class file name from class name by replacing dot's with '/' separator
	 * and adding class extension at the end. If array, component type is returned.
	 */
	public static String getClassFileName(Class clazz) {
		if (clazz.isArray()) {
			clazz = clazz.getComponentType();
		}
		return getClassFileName(clazz.getName());
	}

	/**
	 * Resolves class file name from class name by replacing dot's with '/' separator.
	 */
	public static String getClassFileName(String className) {
		return className.replace('.', '/') + ".class";
	}

}
