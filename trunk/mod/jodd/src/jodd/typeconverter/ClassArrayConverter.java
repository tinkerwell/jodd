// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.util.ClassLoaderUtil;

/**
 * Converts given object to Class array.
 */
public class ClassArrayConverter implements TypeConverter<Class[]>{

	public static Class[] valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof Class[]) {
			return (Class[]) value;
		}
		if (value instanceof Class) {
			return new Class[] {(Class) value};
		}
		String[] allClassNames = StringArrayConverter.valueOf(value);
		int total = allClassNames.length;
		Class[] result = new Class[total];
		try {
			for (int i = 0; i < total; i++) {
				result[i] = ClassLoaderUtil.loadClass(allClassNames[i].trim(), ClassArrayConverter.class);
			}
		} catch (ClassNotFoundException cnfex) {
			throw new TypeConversionException(value, cnfex);
		}
		return result;	
	}

	public Class[] convert(Object value) {
		return valueOf(value);
	}
}
