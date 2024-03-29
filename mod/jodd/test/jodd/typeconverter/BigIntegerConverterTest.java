// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.BigIntegerConverter;
import junit.framework.TestCase;

import java.math.BigInteger;

public class BigIntegerConverterTest extends TestCase {

	public void testConversion() {
		BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();

		assertNull(bigIntegerConverter.convert(null));

		assertEquals(new BigInteger("12345"), bigIntegerConverter.convert(new BigInteger("12345")));
		assertEquals(new BigInteger("12345"), bigIntegerConverter.convert("12345"));
		assertEquals(new BigInteger("12345"), bigIntegerConverter.convert(" 12345 "));
		assertEquals(new BigInteger("12345"), bigIntegerConverter.convert(Double.valueOf(12345.0D)));
		assertEquals(new BigInteger("123456789"), bigIntegerConverter.convert(Long.valueOf(123456789)));
	}
}
