// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import java.sql.Clob;
import java.sql.SQLException;

/**
 * Converts given object to String.
 */
public class StringConverter implements TypeConverter<String> {

	public static String valueOf(Object value) {
		if (value == null) {
			return null;
		}
		Class type = value.getClass();
		if (type == byte[].class) {
			byte[] valueArray = (byte[]) value;
			return new String(valueArray, 0, valueArray.length);
		}
		if (value instanceof Clob) {
			Clob clob = (Clob) value;
			try {
				long lenght = clob.length();
				if (lenght > Integer.MAX_VALUE) {
					throw new TypeConversionException("Clob is too big.");
				}
				return clob.getSubString(1, (int) lenght);
			} catch (SQLException sex) {
				throw new TypeConversionException(value, sex);
			}
		}
		return value.toString();
	}

	public String convert(Object value) {
		return valueOf(value);
	}
	
}