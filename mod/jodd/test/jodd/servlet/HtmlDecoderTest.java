// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import junit.framework.TestCase;

public class HtmlDecoderTest extends TestCase {

	public void testHexDecimal() {
		String s;
		s = HtmlDecoder.decode("&#xFF;");
		assertEquals(1, s.length());
		assertEquals(0xFF, s.charAt(0));

		s = HtmlDecoder.decode("&#xFF");
		assertEquals(5, s.length());
		assertEquals("&#xFF", s);

		s = HtmlDecoder.decode("&#");
		assertEquals("&#", s);

		s = HtmlDecoder.decode("&");
		assertEquals("&", s);

		s = HtmlDecoder.decode("&#123");
		assertEquals("&#123", s);

		s = HtmlDecoder.decode("&#123;");
		assertEquals(1, s.length());
		assertEquals(123, s.charAt(0));

		s = HtmlDecoder.decode("aaa &#x41;&#65; aaa");
		assertEquals("aaa AA aaa", s);
	}

	public void testEntities() {
		String s;

		s = HtmlDecoder.decode("&amp;");
		assertEquals("&", s);

		s = HtmlDecoder.decode("&amp");
		assertEquals("&amp", s);

		s = HtmlDecoder.decode("&");
		assertEquals("&", s);

		s = HtmlDecoder.decode("2 &lt; 5");
		assertEquals("2 < 5", s);

		s = HtmlDecoder.decode("&aacute;");
		assertEquals(1, s.length());
		assertEquals(0xe1, s.charAt(0));
	}

	public void testSameNames() {
		String s;

		s = HtmlDecoder.decode("&aacute;");
		assertEquals(1, s.length());
		assertEquals(0xE1, s.charAt(0));

		s = HtmlDecoder.decode("&Aacute;");
		assertEquals(1, s.length());
		assertEquals(0xC1, s.charAt(0));
	}
}
