// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

import java.math.BigInteger;

/**
 * Converts given object to <code>BigInteger</code>.
 */
public class BigIntegerConverter implements TypeConverter<BigInteger> {

	public BigInteger convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}
		if (value instanceof Number) {
			return new BigInteger(String.valueOf(((Number)value).longValue()));
		}
		try {
			return new BigInteger(value.toString().trim());
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
	}

}