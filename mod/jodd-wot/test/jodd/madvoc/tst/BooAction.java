// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.tst;

import jodd.madvoc.meta.Action;

public class BooAction {

	public void foo() {}
	public void view() {}
	public void execute() {}

	@Action("xxx")
	public void foo1() {}

	@Action(extension = "xxx")
	public void foo2() {}

	@Action(value = Action.NONE)
	public void foo3() {}

	@Action("/xxx")
	public void foo4() {}

	@Action(value = "/xxx", extension = "not used!", method = "DELETE")
	public void foo41() {}

	@Action(value = "/xxx.[ext]", alias = "dude", method = "POST")
	public void foo5() {}

	@Action(value = "q[method]2")
	public void foo6() {}

	@Action(value = "/[method].[ext]")
	public void foo7() {}

	@Action(extension = Action.NONE)
	public void foo8() {}
	@Action(value = "/boo.foo81")
	public void foo81() {}
	@Action
	public void foo82() {}
	@Action(extension = "json")
	public void foo83() {}

}
