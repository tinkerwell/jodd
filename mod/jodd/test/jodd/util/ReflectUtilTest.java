// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import junit.framework.TestCase;
import jodd.util.testdata.A;
import jodd.util.testdata.B;
import jodd.util.testdata.C;
import jodd.util.testdata2.D;
import jodd.util.testdata2.E;
import jodd.mutable.MutableInteger;
import jodd.typeconverter.JDateTimeConverter;

public class ReflectUtilTest extends TestCase {

	public void testInvoke() {
		TFooBean bean = new TFooBean();
		
		String result;		
		try {
			result = (String) ReflectUtil.invoke(TFooBean.class, bean, "getPublic", null, null);
			assertEquals("public", result);
			result = (String) ReflectUtil.invoke(bean, "getPublic", null, null);
			assertEquals("public", result);
			result = (String) ReflectUtil.invoke(bean, "getPublic", null);
			assertEquals("public", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
		
		try {		
			ReflectUtil.invoke(TFooBean.class, bean, "getDefault", null, null);
			fail("ReflectUtil.invoke() works irregular!");
		} catch (Exception e) {}
		
		try {		
			ReflectUtil.invoke(TFooBean.class, bean, "getProtected", null, null);
			fail("ReflectUtil.invoke() works irregular!");
		} catch (Exception e) {}

		try {		
			ReflectUtil.invoke(TFooBean.class, bean, "getPrivate", null, null);
			fail("ReflectUtil.invoke() works irregular!");
		} catch (Exception e) {}
	}
	
	
	public void testInvokeEx() {
		TFooBean bean = new TFooBean();
		
		String result;		
		try {
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getPublic", null, null);
			assertEquals("public", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getPublic", null, null);
			assertEquals("public", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getPublic", null);
			assertEquals("public", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
		
		try {		
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getDefault", null, null);
			assertEquals("default", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getDefault", null, null);
			assertEquals("default", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
		
		try {		
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getProtected", null, null);
			assertEquals("protected", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getProtected", null, null);
			assertEquals("protected", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}

		try {		
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getPrivate", null, null);
			assertEquals("private", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getPrivate", null);
			assertEquals("private", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
	
	}
	
	public void testInvoke2() {
		TFooBean bean = new TFooBean();
		String result;
		try {		
			result = (String) ReflectUtil.invoke(TFooBean.class, bean, "getMore", new Class[] {String.class, Integer.class}, new Object[] {"qwerty", new Integer(173)});
			assertEquals("qwerty173", result);
			result = (String) ReflectUtil.invoke(TFooBean.class, bean, "getMore", new Object[] {"Qwerty", new Integer(173)});
			assertEquals("Qwerty173", result);
			result = (String) ReflectUtil.invoke(bean, "getMore", new Class[] {String.class, Integer.class}, new Object[] {"QWerty", new Integer(173)});
			assertEquals("QWerty173", result);
			result = (String) ReflectUtil.invoke(bean, "getMore", new Object[] {"QWErty", new Integer(173)});
			assertEquals("QWErty173", result);
			
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getMore", new Class[] {String.class, Integer.class}, new Object[] {"qwerty", new Integer(173)});
			assertEquals("qwerty173", result);
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getMore", new Object[] {"Qwerty", new Integer(173)});
			assertEquals("Qwerty173", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getMore", new Class[] {String.class, Integer.class}, new Object[] {"QWerty", new Integer(173)});
			assertEquals("QWerty173", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getMore", new Object[] {"QWErty", new Integer(173)});
			assertEquals("QWErty173", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
	}
	
	
	public void testMethod0() {
		TFooBean bean = new TFooBean();
		Method m;
		m = ReflectUtil.getMethod0(TFooBean.class, "getMore", String.class, Integer.class);
		assertNotNull(m);
		
		m = ReflectUtil.getMethod0(bean, "getMore", String.class, Integer.class);
		assertNotNull(m);
		
		m = ReflectUtil.getMethod0(bean, "getXXX", String.class, Integer.class);
		assertNull(m);
		
		m = ReflectUtil.getMethod0(bean, "getPublic");
		assertNotNull(m);
		
		m = ReflectUtil.getMethod0(bean, "getDefault");
		assertNull(m);
		
		m = ReflectUtil.getMethod0(bean, "getProtected");
		assertNull(m);
		
		m = ReflectUtil.getMethod0(bean, "getPrivate");
		assertNull(m);
	}


	public void testMethod() {
		TFooBean bean = new TFooBean();
		Method m;
		m = ReflectUtil.findMethod(TFooBean.class, "getMore");
		assertNotNull(m);
		
		m = ReflectUtil.findMethod(bean.getClass(), "getMore");
		assertNotNull(m);
		
		m = ReflectUtil.findMethod(bean.getClass(), "getXXX");
		assertNull(m);
	}
	
	
	public void testMatchClasses() {
		TFooBean a = new TFooBean();
		TFooBean b = new TFooBean();
		TFooBean2 c = new TFooBean2();

		assertTrue(TFooBean.class.isInstance(a));
		assertTrue(ReflectUtil.isSubclass(TFooBean.class, a.getClass()));
		assertTrue(ReflectUtil.isSubclass(TFooBean.class, b.getClass()));
		assertTrue(ReflectUtil.isSubclass(a.getClass(), b.getClass()));
		assertTrue(ReflectUtil.isSubclass(b.getClass(), a.getClass()));
		
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, c.getClass()));
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, TFooBean.class));
		assertFalse(ReflectUtil.isSubclass(TFooBean.class, TFooBean2.class));
		assertTrue(ReflectUtil.isSubclass(c.getClass(), TFooBean.class));
		assertFalse(ReflectUtil.isSubclass(a.getClass(), TFooBean2.class));
		
		assertTrue(ReflectUtil.isSubclass(TFooBean.class, Serializable.class));
		assertTrue(Serializable.class.isInstance(c));
		//noinspection ConstantConditions
		assertTrue(c instanceof Serializable);
		assertTrue(ReflectUtil.isInstanceOf(c, Serializable.class));
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, Serializable.class));
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, Comparable.class));
		assertFalse(ReflectUtil.isSubclass(TFooBean.class, Comparable.class));

		assertTrue(ReflectUtil.isSubclass(TFooBean.class, TFooIndyEx.class));
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, TFooIndyEx.class));
		assertTrue(ReflectUtil.isSubclass(TFooBean.class, TFooIndy.class));
	}


	public void testAccessibleA() {
		Method[] ms = ReflectUtil.getAccessibleMethods(A.class, null);
		assertEquals(4 + 11, ms.length);			// there are 11 accessible Object methods (9 public + 2 protected)
		ms = ReflectUtil.getAccessibleMethods(A.class);
		assertEquals(4, ms.length);
		ms = A.class.getMethods();
		assertEquals(1 + 9, ms.length);				// there are 9 public Object methods
		ms = A.class.getDeclaredMethods();
		assertEquals(4, ms.length);
		ms = ReflectUtil.getSupportedMethods(A.class, null);
		assertEquals(4 + 12, ms.length);			// there are 12 total Object methods (9 public + 2 protected + 1 private)
		ms = ReflectUtil.getSupportedMethods(A.class);
		assertEquals(4, ms.length);


		Field[] fs = ReflectUtil.getAccessibleFields(A.class);
		assertEquals(4, fs.length);
		fs = A.class.getFields();
		assertEquals(1, fs.length);
		fs = A.class.getDeclaredFields();
		assertEquals(4, fs.length);
		fs = ReflectUtil.getSupportedFields(A.class);
		assertEquals(4, fs.length);
	}

	public void testAccessibleB() {
		Method[] ms = ReflectUtil.getAccessibleMethods(B.class, null);
		assertEquals(3 + 11, ms.length);
		ms = ReflectUtil.getAccessibleMethods(B.class);
		assertEquals(3, ms.length);
		ms = B.class.getMethods();
		assertEquals(1 + 9, ms.length);
		ms = B.class.getDeclaredMethods();
		assertEquals(0, ms.length);
		ms = ReflectUtil.getSupportedMethods(B.class, null);
		assertEquals(4 + 12, ms.length);
		ms = ReflectUtil.getSupportedMethods(B.class);
		assertEquals(4, ms.length);


		Field[] fs = ReflectUtil.getAccessibleFields(B.class);
		assertEquals(3, fs.length);
		fs = B.class.getFields();
		assertEquals(1, fs.length);
		fs = B.class.getDeclaredFields();
		assertEquals(0, fs.length);
		fs = ReflectUtil.getSupportedFields(B.class);
		assertEquals(4, fs.length);
	}

	public void testAccessibleC() {
		Method[] ms = ReflectUtil.getAccessibleMethods(C.class, null);
		assertEquals(5 + 11, ms.length);
		ms = ReflectUtil.getAccessibleMethods(C.class);
		assertEquals(5, ms.length);
		ms = C.class.getMethods();
		assertEquals(2 + 9, ms.length);
		ms = C.class.getDeclaredMethods();
		assertEquals(5, ms.length);
		ms = ReflectUtil.getSupportedMethods(C.class, null);
		assertEquals(5 + 12, ms.length);
		ms = ReflectUtil.getSupportedMethods(C.class);
		assertEquals(5, ms.length);


		Field[] fs = ReflectUtil.getAccessibleFields(C.class);
		assertEquals(5, fs.length);
		fs = C.class.getFields();
		assertEquals(3, fs.length);
		fs = C.class.getDeclaredFields();
		assertEquals(5, fs.length);
		fs = ReflectUtil.getSupportedFields(C.class);
		assertEquals(5, fs.length);
	}

	public void testAccessibleD() {
		Method[] ms = ReflectUtil.getAccessibleMethods(D.class, null);
		assertEquals(3 + 11, ms.length);
		ms = ReflectUtil.getAccessibleMethods(D.class);
		assertEquals(3, ms.length);
		ms = D.class.getMethods();
		assertEquals(2 + 9, ms.length);
		ms = D.class.getDeclaredMethods();
		assertEquals(0, ms.length);
		ms = ReflectUtil.getSupportedMethods(D.class, null);
		assertEquals(5 + 12, ms.length);
		ms = ReflectUtil.getSupportedMethods(D.class);
		assertEquals(5, ms.length);

		Field[] fs = ReflectUtil.getAccessibleFields(D.class);
		assertEquals(3, fs.length);
		fs = D.class.getFields();
		assertEquals(3, fs.length);
		fs = D.class.getDeclaredFields();
		assertEquals(0, fs.length);
		fs = ReflectUtil.getSupportedFields(D.class);
		assertEquals(5, fs.length);
	}
		
	public void testAccessibleE() {
		Method[] ms = ReflectUtil.getAccessibleMethods(E.class, null);
		assertEquals(5 + 11, ms.length);
		ms = ReflectUtil.getAccessibleMethods(E.class);
		assertEquals(5, ms.length);
		ms = E.class.getMethods();
		assertEquals(2 + 9, ms.length);
		ms = E.class.getDeclaredMethods();
		assertEquals(4, ms.length);
		ms = ReflectUtil.getSupportedMethods(E.class, null);
		assertEquals(5 + 12, ms.length);
		ms = ReflectUtil.getSupportedMethods(E.class);
		assertEquals(5, ms.length);

		Field[] fs = ReflectUtil.getAccessibleFields(E.class);
		assertEquals(5, fs.length);
		fs = E.class.getFields();
		assertEquals(4, fs.length);
		fs = E.class.getDeclaredFields();
		assertEquals(4, fs.length);
		fs = ReflectUtil.getSupportedFields(E.class);
		assertEquals(5, fs.length);
	}


	public void testCast() {

		String s = "123";
		Integer d = ReflectUtil.castType(s, Integer.class);
		assertEquals(123, d.intValue());

		s = ReflectUtil.castType(d, String.class);
		assertEquals("123", s);

		MutableInteger md = ReflectUtil.castType(s, MutableInteger.class);
		assertEquals(123, md.intValue());

		B b = new B();
		A a = ReflectUtil.castType(b, A.class);
		assertEquals(a, b);
	}

}
