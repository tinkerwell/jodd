// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

public class FloatArrayConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(FloatArrayConverter.valueOf(null));

		assertEq(arrf((float)1.73), FloatArrayConverter.valueOf(Float.valueOf((float) 1.73)));
		assertEq(arrf((float)1.73, (float)10.22), FloatArrayConverter.valueOf(arrf((float)1.73, (float)10.22)));
		assertEq(arrf((float)1.73, (float)10.22), FloatArrayConverter.valueOf(arrd(1.73, 10.22)));
		assertEq(arrf((float)1.0, (float)7.0, (float)3.0), FloatArrayConverter.valueOf(arri(1, 7, 3)));
		assertEq(arrf((float)1.73, (float)10.22), FloatArrayConverter.valueOf(arrs("1.73", "10.22")));
		assertEq(arrf((float)1.73, 10), FloatArrayConverter.valueOf(arr("1.73", Integer.valueOf(10))));
	}

	private void assertEq(float[] arr1, float[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], arr2[i], 0.0001);
		}
	}

}


