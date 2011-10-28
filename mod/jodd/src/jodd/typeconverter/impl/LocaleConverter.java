// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;
import jodd.util.LocaleUtil;

import java.util.Locale;

/**
 * Converts given object to Java locale.
 */
public class LocaleConverter implements TypeConverter<Locale> {

	public static Locale valueOf(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Locale.class) {
			return (Locale) value;
		}

		return LocaleUtil.getLocale(value.toString());
	}

	public Locale convert(Object value) {
		return valueOf(value);
	}
}
