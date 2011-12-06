// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.StringUtil;

/**
 * Converts given object to a <code>Long</code>.
 */
public class LongConverter implements TypeConverter<Long> {

	public static Long valueOf(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Long.class) {
			return (Long) value;
		}
		if (value instanceof Number) {
			return Long.valueOf(((Number)value).longValue());
		}
		try {
			String stringValue = value.toString().trim();
			if (StringUtil.startsWithChar(stringValue, '+')) {
				stringValue = stringValue.substring(1);
			}
			return Long.valueOf(stringValue);
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

	public Long convert(Object value) {
		return valueOf(value);
	}
	
}
