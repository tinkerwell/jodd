// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci2;

@Entity
public interface User {

	String principal();

	String password();
}