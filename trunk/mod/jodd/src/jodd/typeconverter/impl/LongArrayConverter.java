// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

/**
 *  Converts given object to long[].
 */
public class LongArrayConverter implements TypeConverter<long[]> {

	public static long[] valueOf(Object value) {
		if (value == null) {
			return null;
		}

		Class type = value.getClass();
		if (type.isArray() == false) {
			if (value instanceof Number) {
				return new long[] {((Number) value).longValue()};
			}
			try {
				return new long[] {Long.parseLong(value.toString())};
			} catch (NumberFormatException nfex) {
				throw new TypeConversionException(value, nfex);
			}
		}

		if (type == long[].class) {
			return (long[]) value;
		}
		if (type == int[].class) {
			int[] values = (int[]) value;
			long[] results = new long[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = values[i];
			}
			return results;
		}
		if (type == double[].class) {
			double[] values = (double[]) value;
			long[] results = new long[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (long) values[i];
			}
			return results;
		}
		if (type == byte[].class) {
			byte[] values = (byte[]) value;
			long[] results = new long[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = values[i];
			}
			return results;
		}
		if (type == float[].class) {
			float[] values = (float[]) value;
			long[] results = new long[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (long) values[i];
			}
			return results;
		}
		if (type == boolean[].class) {
			boolean[] values = (boolean[]) value;
			long[] results = new long[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (values[i] == true ? 1L : 0L);
			}
			return results;
		}
		if (type == short[].class) {
			short[] values = (short[]) value;
			long[] results = new long[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = values[i];
			}
			return results;
		}

		Object[] values = (Object[]) value;
		long[] results = new long[values.length];
		try {
			for (int i = 0; i < values.length; i++) {
				if (values[i] != null) {
					if (values[i] instanceof Number) {
						results[i] = ((Number) values[i]).longValue();
					} else {
						results[i] = Long.parseLong(values[i].toString());
					}
				}
			}
		} catch (NumberFormatException nfex) {
			throw new TypeConversionException(value, nfex);
		}
		return results;
	}

	public long[] convert(Object value) {
		return valueOf(value);
	}
}
