// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

/**
 *  Converts given object to boolean[].
 */
public class BooleanArrayConverter implements TypeConverter<boolean[]> {

	public static boolean[] valueOf(Object value) {
		if (value == null) {
			return null;
		}
		Class type = value.getClass();
		if (type.isArray() == false) {
			if (value instanceof Boolean) {
				return new boolean[] {((Boolean) value).booleanValue()};
			}
			return new boolean[] {BooleanConverter.valueOf(value.toString()).booleanValue()};
		}

		if (type == boolean[].class) {
			return (boolean[]) value;
		}
		if (type == int[].class) {
			int[] values = (int[]) value;
			boolean[] results = new boolean[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (values[i] != 0);
			}
			return results;
		}
		if (type == long[].class) {
			long[] values = (long[]) value;
			boolean[] results = new boolean[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (values[i] != 0);
			}
			return results;
		}
		if (type == double[].class) {
			double[] values = (double[]) value;
			boolean[] results = new boolean[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (values[i] != 0);
			}
			return results;
		}
		if (type == float[].class) {
			float[] values = (float[]) value;
			boolean[] results = new boolean[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (values[i] != 0);
			}
			return results;
		}
		if (type == byte[].class) {
			byte[] values = (byte[]) value;
			boolean[] results = new boolean[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (values[i] != 0);
			}
			return results;
		}
		if (type == short[].class) {
			short[] values = (short[]) value;
			boolean[] results = new boolean[values.length];
			for (int i = 0; i < values.length; i++) {
				results[i] = (values[i] != 0);
			}
			return results;
		}

		Object[] values = (Object[]) value;
		boolean[] results = new boolean[values.length];
		for (int i = 0; i < values.length; i++) {
			results[i] = BooleanConverter.valueOf(values[i]).booleanValue();
		}
		return results;
	}

	public boolean[] convert(Object value) {
		return valueOf(value);
	}
}
