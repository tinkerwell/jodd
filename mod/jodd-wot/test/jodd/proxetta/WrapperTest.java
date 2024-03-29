// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.data.CalcSuper;
import jodd.proxetta.data.CalcSuperImpl;
import jodd.proxetta.impl.WrapperProxettaBuilder;
import jodd.proxetta.data.Calc;
import jodd.proxetta.data.CalcImpl;
import jodd.proxetta.data.StatCounter;
import jodd.proxetta.data.StatCounterAdvice;
import jodd.proxetta.impl.WrapperProxetta;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;
import junit.framework.TestCase;

import java.lang.reflect.Method;

public class WrapperTest extends TestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		StatCounter.counter = 0;
	}

	public void testClassWrapper() throws Exception {
		Calc calc = new CalcImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return isTopLevelMethod(methodInfo) && isPublic(methodInfo);
			}
		}));

//		proxetta.setDebugFolder("d:\\");

		// wrapper over CLASS
		// resulting object has NO interfaces
		// resulting object wraps ALL target class methods
		WrapperProxettaBuilder builder = proxetta.builder(calc.getClass());

		Class calc2Class = builder.define();

		Object object = calc2Class.newInstance();

		assertFalse(object instanceof Calc);
		assertEquals(0, calc2Class.getInterfaces().length);

		builder.injectTargetIntoWrapper(calc, object);

		assertEquals(1, StatCounter.counter);	// counter in static block !!!

		Method method = calc2Class.getMethod("hello");
		assertNotNull(method);
		method.invoke(object);

		assertEquals(2, StatCounter.counter);

		method = calc2Class.getMethod("calculate", int.class, int.class);
		assertNotNull(method);
		Integer result = (Integer) method.invoke(object, 3, 7);
		assertEquals(10, result.intValue());

		assertEquals(3, StatCounter.counter);

		assertNotNull(calc2Class.getMethod("customMethod"));
	}

	public void testClassWrapperCastToInterface() throws Exception {
		Calc calc = new CalcImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return isTopLevelMethod(methodInfo) && isPublic(methodInfo);
			}
		}));

//		proxetta.setDebugFolder("d:\\");

		// wrapper over CLASS casted to interface,
		// resulting object has ONE interface
		// ALL target methods are wrapped
		WrapperProxettaBuilder builder = proxetta.builder(calc.getClass(), Calc.class, ".CalcImpl2");

		Class<Calc> calc2Class = builder.define();

		Calc calc2 = calc2Class.newInstance();

		builder.injectTargetIntoWrapper(calc, calc2);

		assertEquals(1, StatCounter.counter);	// counter in static block !!!

		calc2.hello();

		assertEquals(2, StatCounter.counter);

		assertEquals(10, calc2.calculate(3, 7));

		assertEquals(3, StatCounter.counter);

		assertNotNull(calc2Class.getMethod("customMethod"));
	}

	public void testInterfaceWrapper() throws Exception {
		Calc calc = new CalcImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return isTopLevelMethod(methodInfo) && isPublic(methodInfo);
			}
		}));

//		proxetta.setDebugFolder("d:\\");

		// wrapper over INTERFACE
		// resulting object has ONE interface
		// only interface methods are wrapped
		WrapperProxettaBuilder builder = proxetta.builder(Calc.class, ".CalcImpl3");

		Class<Calc> calc2Class = builder.define();

		Calc calc2 = calc2Class.newInstance();

		builder.injectTargetIntoWrapper(calc, calc2);

		assertEquals(1, StatCounter.counter);	// counter in static block !!!

		calc2.hello();

		assertEquals(2, StatCounter.counter);

		assertEquals(10, calc2.calculate(3, 7));

		assertEquals(3, StatCounter.counter);

		try {
			calc2Class.getMethod("customMethod");
			fail();
		} catch (Exception ex) {
		}
	}


	public void testPartialMethodsWrapped() throws Exception {

		Calc calc = new CalcSuperImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return
						isPublic(methodInfo) &&
						(methodInfo.getMethodName().equals("hello") || methodInfo.getMethodName().equals("ola"));
			}
		}));

//		proxetta.setDebugFolder("d:\\");

		WrapperProxettaBuilder builder = proxetta.builder(CalcSuper.class);

		Class<CalcSuper> calc2Class = builder.define();

		CalcSuper calc2 = calc2Class.newInstance();

		builder.injectTargetIntoWrapper(calc, calc2);

		assertEquals(1, StatCounter.counter);	// counter in static block !!!

		calc2.hello();

		assertEquals(2, StatCounter.counter);

		assertEquals(10, calc2.calculate(3, 7));

		assertEquals(2, StatCounter.counter);		// counter not called in calculate!

		calc2.ola();

		assertEquals(3, StatCounter.counter);

		calc2.superhi();
		calc2.maybe(4, 5);
		calc2.calculate(4, 5);

		assertEquals(3, StatCounter.counter);
	}

	public void testNoPointcutMatched() throws Exception {

		Calc calc = new CalcSuperImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return false;
			}
		}));

//		proxetta.setDebugFolder("d:\\");

		WrapperProxettaBuilder builder = proxetta.builder(CalcSuper.class, ".CalcSuper22");

		Class<CalcSuper> calc2Class = builder.define();

		CalcSuper calc2 = calc2Class.newInstance();

		builder.injectTargetIntoWrapper(calc, calc2);

		assertEquals(1, StatCounter.counter);	// counter in static block !!!

		calc2.hello();

		assertEquals(1, StatCounter.counter);

		assertEquals(10, calc2.calculate(3, 7));

		assertEquals(1, StatCounter.counter);		// counter not called in calculate!

		calc2.ola();

		assertEquals(1, StatCounter.counter);

		calc2.superhi();
		calc2.maybe(4, 5);
		calc2.calculate(4, 5);

		assertEquals(1, StatCounter.counter);
	}

}
