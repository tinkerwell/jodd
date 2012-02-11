// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.ClassLoaderUtil;

/**
 * Converts given object to <code>Class</code>.
 */
public class ClassConverter implements TypeConverter<Class> {

	public Class convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Class.class) {
			return (Class) value;
		}
		try {
			return ClassLoaderUtil.loadClass(value.toString().trim());
		} catch (ClassNotFoundException cnfex) {
			throw new TypeConversionException(value, cnfex);
		}
	}

}
