// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

/**
 * Converts given object to <code>String[]</code>.
 * Conversion rules:
 * <li>an object array is converted to <code>String[]</code> array.
 * <li>primitive arrays are converted to string arrays, element by element
 * <li><code>Class[]</code> is converted to array of class names.
 * <li>simple object is created to <code>String[]</code> array from CSV representation of <code>toString</code>.
 */
public class StringArrayConverter implements TypeConverter<String[]> {

	public String[] convert(Object value) {
		if (value == null) {
			return null;
		}
		
		Class type = value.getClass();
		if (type.isArray() == false) {
			// special case #1
			if (type == Class.class) {
				return new String[] {((Class)value).getName()};
			}
			return CsvUtil.toStringArray(value.toString());
		}

		// handle arrays
		if (type == String[].class) {
			return (String[]) value;
		}

		if (type == int[].class) {
			int[] values = (int[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == long[].class) {
			long[] values = (long[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == double[].class) {
			double[] values = (double[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == byte[].class) {
			byte[] values = (byte[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == float[].class) {
			float[] values = (float[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == boolean[].class) {
			boolean[] values = (boolean[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}
		if (type == short[].class) {
			short[] values = (short[]) value;
			String[] result = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				result[i] = String.valueOf(values[i]);
			}
			return result;
		}

		Object[] values = (Object[]) value;
		String[] result = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			Object v = values[i];
			if (v != null) {
				// special case #1
				if (v.getClass() == Class.class) {
					result[i] = ((Class)v).getName();
					continue;
				}
				result[i] = v.toString();
			}
		}
		return result;
	}

}
