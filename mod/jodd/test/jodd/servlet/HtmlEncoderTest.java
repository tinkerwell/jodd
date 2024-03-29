// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import junit.framework.TestCase;

public class HtmlEncoderTest extends TestCase {

	public void testEncode() {
		String html = "< & \" ' > \r\n \n  \t";

		assertEquals("&lt; &amp; &quot; &#039; &gt; \r\n \n  \t", HtmlEncoder.text(html));

		html = "";
		assertEquals("", HtmlEncoder.text(html));

		html = null;
		assertEquals("", HtmlEncoder.text(html));

		html = new String(new char[] {128, 257});
		assertEquals(html, HtmlEncoder.text(html));
	}

	public void testEncodeText() {
		String html = "< & \" ' > \r\n \n  \t";

		assertEquals("&lt; &amp; &quot; &#039; &gt; <br/> <br/>  \t", HtmlEncoder.block(html));

		html = "";
		assertEquals("", HtmlEncoder.block(html));

		html = null;
		assertEquals("", HtmlEncoder.block(html));

		html = new String(new char[] {128, 257});
		assertEquals(html, HtmlEncoder.block(html));

		html = "\r\n\n\r";
		assertEquals("<br/><br/><br/>", HtmlEncoder.block(html));

		html = "\r\n\r\n";
		assertEquals("<br/><br/>", HtmlEncoder.block(html));

		html = "\n\r";
		assertEquals("<br/><br/>", HtmlEncoder.block(html));
	}


	public void testEncodeTextStrict() {
		String html = "< & \" ' > \r\n \n  \t";

		assertEquals("&lt; &amp; &quot; &#039; &gt; <br/> <br/> &nbsp;\t", HtmlEncoder.strict(html));

		html = "";
		assertEquals("", HtmlEncoder.strict(html));

		html = null;
		assertEquals("", HtmlEncoder.strict(html));

		html = new String(new char[] {128, 257});
		assertEquals(html, HtmlEncoder.strict(html));

		html = "\r\n\n\r";
		assertEquals("<br/><br/><br/>", HtmlEncoder.strict(html));

		html = "\r\n\r\n";
		assertEquals("<br/><br/>", HtmlEncoder.strict(html));

		html = "\n\r";
		assertEquals("<br/><br/>", HtmlEncoder.strict(html));


		html = " ";
		assertEquals(" ", HtmlEncoder.strict(html));
		html = "  ";
		assertEquals(" &nbsp;", HtmlEncoder.strict(html));
		html = "   ";
		assertEquals(" &nbsp; ", HtmlEncoder.strict(html));
		html = "    ";
		assertEquals(" &nbsp; &nbsp;", HtmlEncoder.strict(html));
		html = "     ";
		assertEquals(" &nbsp; &nbsp; ", HtmlEncoder.strict(html));

		html = " a";
		assertEquals(" a", HtmlEncoder.strict(html));
		html = "  a";
		assertEquals(" &nbsp;a", HtmlEncoder.strict(html));
		html = "   a";
		assertEquals(" &nbsp; a", HtmlEncoder.strict(html));
		html = "    a";
		assertEquals(" &nbsp; &nbsp;a", HtmlEncoder.strict(html));
		html = "     a";
		assertEquals(" &nbsp; &nbsp; a", HtmlEncoder.strict(html));
		
		html = "a ";
		assertEquals("a ", HtmlEncoder.strict(html));
		html = "a  ";
		assertEquals("a &nbsp;", HtmlEncoder.strict(html));
		html = "a   ";
		assertEquals("a &nbsp; ", HtmlEncoder.strict(html));
		html = "a    ";
		assertEquals("a &nbsp; &nbsp;", HtmlEncoder.strict(html));
		html = "a     ";
		assertEquals("a &nbsp; &nbsp; ", HtmlEncoder.strict(html));
		
		html = " a ";
		assertEquals(" a ", HtmlEncoder.strict(html));
		html = "  a  ";
		assertEquals(" &nbsp;a &nbsp;", HtmlEncoder.strict(html));
		html = " a  ";
		assertEquals(" a &nbsp;", HtmlEncoder.strict(html));
		html = "  a ";
		assertEquals(" &nbsp;a ", HtmlEncoder.strict(html));
		html = "  a b   c  d e";
		assertEquals(" &nbsp;a b &nbsp; c &nbsp;d e", HtmlEncoder.strict(html));
	}

	public void testEncodeUrl() {
		assertEquals("/aaa", URLCoder.url("/aaa"));
		assertEquals("/aaa?", URLCoder.url("/aaa?"));
		assertEquals("/aaa?b", URLCoder.url("/aaa?b"));
		assertEquals("/aaa?b=", URLCoder.url("/aaa?b="));
		assertEquals("/aaa?b=c", URLCoder.url("/aaa?b=c"));
		assertEquals("/aaa?b=+c", URLCoder.url("/aaa?b= c"));
		assertEquals("/aaa?b=+c&", URLCoder.url("/aaa?b= c&"));
		assertEquals("/aaa?b=+c&dd", URLCoder.url("/aaa?b= c&dd"));
		assertEquals("/aaa?b=+c&dd=", URLCoder.url("/aaa?b= c&dd="));
		assertEquals("/aaa?b=++c&dd=%3D", URLCoder.url("/aaa?b=  c&dd=="));
		assertEquals("/aaa?b=++c&dd=%3D&=", URLCoder.url("/aaa?b=  c&dd==&="));
		assertEquals("?data=The+string+%C3%BC%40foo-bar", URLCoder.url("?data=The string ü@foo-bar"));
	}

	public void testEncodeBaseUrl() {
		assertEquals("/aaa", URLCoder.build().path("/aaa").toString());
		assertEquals("/aaa?", URLCoder.build().path("/aaa").param("").toString());
		assertEquals("/aaa?b", URLCoder.build().path("/aaa").param("b").toString());
		assertEquals("/aaa?b", URLCoder.build().path("/aaa").param("b", null).toString());
		assertEquals("/aaa?b=c", URLCoder.build().path("/aaa").param("b", "c").toString());
		assertEquals("/aaa?b=c", URLCoder.build().path("/aaa").param("b=c").toString());
		assertEquals("/aaa?b=+c", URLCoder.build().path("/aaa").param("b", " c").toString());
		assertEquals("/aaa?b=+c", URLCoder.build().path("/aaa").param("b= c").toString());
		assertEquals("/aaa?b=+c&", URLCoder.build().path("/aaa").param("b", " c").param("").toString());
		assertEquals("/aaa?b=+c&dd", URLCoder.build().path("/aaa").param("b", " c").param("dd").toString());
		assertEquals("/aaa?b=+c&dd", URLCoder.build().path("/aaa").param("b", " c").param("dd", null).toString());
		assertEquals("/aaa?b=+c&dd=%3D", URLCoder.build().path("/aaa").param("b", " c").param("dd==").toString());
		assertEquals("/aaa?b=+c&dd=%3D", URLCoder.build().path("/aaa").param("b", " c").param("dd","=").toString());
		assertEquals("?data=The+string+%C3%BC%40foo-bar", URLCoder.build().path("").param("data", "The string ü@foo-bar").toString());

		assertEquals("/aaa", URLCoder.build().path("/aaa").toString());
		assertEquals("/aaa?p=1", URLCoder.build().path("/aaa").param("p=1").toString());
		assertEquals("/aaa?p=1&b=2", URLCoder.build().path("/aaa").param("p=1").param("b", "2").toString());
	}

	public void testNbsp() {
		assertEquals(" ", HtmlEncoder.text(" "));
		assertEquals("\u00a0", HtmlEncoder.text("\u00a0"));
		assertEquals("\u00a0", HtmlDecoder.decode("&nbsp;"));
	}
	

}
