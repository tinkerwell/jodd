<html>
<head>
	<title>Jodd Madvoc</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="jss.css">
</head>

<body>

<img src="gfx/duke.png" align="left">
<h1 style="padding-top:50px;padding-left:30px;">Some <span style="color:crimson;">Jodd Madvoc</span> quick-and-dirty examples</h1>

<p style="clear:both;">
<a href="hello.world.html?name=JohnDoe&data=173">Hello world</a> - injects simple params, prepare out data and forward.<br>
<a href="hello.all.html?p.name=JohnDoe&p.data=13">Hello all</a> - injects bean params (also creates the target bean); then forwards to manually aliased page.<br>
<a href="hello.again.html">Hello again</a> - no results and direct stream output.<br>
<br>

<a href="default.html?ppp[0].name=Aaa&ppp[0].data=1&ppp[1].name=Bbb&ppp[1].data=2&ppp[2].name=Ccc&ppp[2].data=3">Default action name</a> - performs injections of the same params into list, array and map; uses default action method name that is not part of the action path.<br>
<a href="hello.bigchange.html">BIG change</a> - forwards to complete different page, using double hash ('#') in result value.<br>
<a href="hello.noresult.html">404</a> - error: action exist, but result jsp page doesn't.<br>
<a href="hello.chain.html?chain=173">chains</a> - chaining from one action to another; passing and modifying the param.<br>
<a href="raw.html">raw</a> | <a href="raw.text.html">access</a> - two examples of RawResultData and 'raw:' results.<br>
<br>

<a href="foo/hello">Hello</a> - action with no extension, different and explicitly defined action path.<br>
<a href="foo/boo.zoo/again.exec.html">Hello</a> - action with default extension; action path is build from various parts.<br>
<a href="incognito.html">Manual</a> - manual configuration, done in custom configurator.<br>
<a href="oneRedirect.html">redirect</a> or <a href="oneMove.html">move</a> - true redirection and move: similar, but not equals.<br>
<br>
<a href="i_n_d_e_x.html">Url rewrite</a> - shows how urls can be rewritten in Madvoc.<br>
<br>
<a href="mapped.foo.html">mapped.foo.html</a> | <a href="mapped.foo.txt">mapped.foo.txt</a> | <a href="mapped.html">mapped.html</a> |
<a href="mapped.txt">mapped.txt</a> | <a href="mapped">mapped</a> - all action paths are automatically mapped to the existing action methods (actionPathMappingEnabled = true);
	action class is without any annotation.<br>
<br>
<a href="hello.defint1.html?foo=173">default interceptors</a> - intercepted by default set of interceptors; using default ServletConfigInterceptor<br>
<a href="hello.defint2.html?foo=173&foo2=">parameters are copied</a> - different setting of ServletConfigInterceptor, where:
	1) all parameters are copied to attributes, 2) parameters are not injected and 3) empty params are treated as null.<br>
<a href="misc.html">misc scopes</a> - access different scopes throught their map adapters.<br>
<a href="misc.post.html?girl.id=1&girl.name=requestName">inject id, prepare and execute</a> - prebarable action first injects only id parameters,
	then prepares it and executes the action method at the end.<br>

<br>
<a href="search?query=%C5%A1aran">URI encoded link</a> - URI links can be decoded (if not set so in Tomcat), 
<form action="search" method="post"><input type="text" name="query"  value="šaran"><input type="submit"> - forms are encoded/decoded correctly.</form> 


</p>

<h2>More</h2>
<p>
<a href="form.html">Form</a> example.<br>
<a href="girl.list.html">Session scope</a> example - access session holder that is a PetiteBean of SessionScope.<br>
<a href="uploadfiles.html">Upload</a> example.<br>
</p>

<h2>Info</h2>
<p>
List of all <a href="madvoc-listAllActions.html">Action configurations</a>.<br>
</p>


</body>
</html>
