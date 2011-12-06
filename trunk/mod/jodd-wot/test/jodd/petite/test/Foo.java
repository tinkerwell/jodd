// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test;

import jodd.petite.meta.PetiteBean;

@PetiteBean
public class Foo {

	public static int instanceCounter;

	int counter;

	public Foo() {
		instanceCounter++;
		counter = 0;
	}

	public int hello() {
		return instanceCounter;
	}


	public int getCounter() {
		return counter;
	}


	private String name;
	public String getName() {
		return name;
	}
}
