// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.LongConverter;
import junit.framework.TestCase;

public class LongConverterTest extends TestCase {

    public void testConversion() {
		LongConverter longConverter = new LongConverter();
		
        assertNull(longConverter.convert(null));

        assertEquals(Long.valueOf(173), longConverter.convert(Long.valueOf(173)));

        assertEquals(Long.valueOf(173), longConverter.convert(Integer.valueOf(173)));
        assertEquals(Long.valueOf(173), longConverter.convert(Short.valueOf((short) 173)));
        assertEquals(Long.valueOf(173), longConverter.convert(Double.valueOf(173.0D)));
        assertEquals(Long.valueOf(173), longConverter.convert(Float.valueOf(173.0F)));
        assertEquals(Long.valueOf(173), longConverter.convert("173"));
        assertEquals(Long.valueOf(173), longConverter.convert(" 173 "));

		assertEquals(Long.valueOf(-1), longConverter.convert(" -1 "));
        assertEquals(Long.valueOf(1), longConverter.convert(" +1 "));
		assertEquals(Long.valueOf(9223372036854775807L), longConverter.convert(" +9223372036854775807 "));
        assertEquals(Long.valueOf(-9223372036854775808L), longConverter.convert(" -9223372036854775808 "));

        try {
            longConverter.convert("9223372036854775808");
            fail();
        } catch (TypeConversionException ignore) {
        }
        try {
            longConverter.convert("-9223372036854775809");
            fail();
        } catch (TypeConversionException ignore) {
        }
        try {
            longConverter.convert("a");
            fail();
        } catch (TypeConversionException ignore) {
        }
    }

}
