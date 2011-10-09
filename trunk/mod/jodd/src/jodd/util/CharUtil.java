// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.JoddDefault;

import java.io.UnsupportedEncodingException;

/**
 * Various character and character sequence utilities.
 */
public class CharUtil {

	// ---------------------------------------------------------------- to byte array

	/**
	 * Converts char array into byte array by stripping high byte.
	 */
	public static byte[] toSimpleByteArray(char[] carr) {
		if (carr == null) {
			return null;
		}
		byte[] barr = new byte[carr.length];
		for (int i = 0; i < carr.length; i++) {
			barr[i] = (byte) carr[i];
		}
		return barr;
	}

	/**
	 * Converts char array to byte array using default Jodd encoding.
	 */
	public static byte[] toByteArray(char[] carr) throws UnsupportedEncodingException {
		return new String(carr).getBytes(JoddDefault.encoding);
	}

	/**
	 * Converts char array to byte array using provided encoding.  
	 */
	public static byte[] toByteArray(char[] carr, String charset) throws UnsupportedEncodingException {
		return new String(carr).getBytes(charset);
	}

	/**
	 * Converts char array into {@link #toAscii(char) ASCII} array.
	 */
	public static byte[] toAsciiArray(char[] carr) {
		if (carr == null) {
			return null;
		}
		byte[] barr = new byte[carr.length];
		for (int i = 0; i < carr.length; i++) {
			barr[i] = (byte) toAscii(carr[i]);
		}
		return barr;
	}

	/**
	 * Converts char sequence into byte array. Chars are truncated to byte size.
	 */
	public static byte[] toSimpleByteArray(CharSequence charSequence) {
		if (charSequence == null) {
			return null;
		}
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			barr[i] = (byte) charSequence.charAt(i);
		}
		return barr;
	}

	/**
	 * Converts char sequence into ASCII array.
	 */
	public static byte[] toAsciiArray(CharSequence charSequence) {
		if (charSequence == null) {
			return null;
		}
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			barr[i] = (byte) toAscii(charSequence.charAt(i));
		}
		return barr;
	}

	// ---------------------------------------------------------------- to char array

	/**
	 * Converts byte array to char array by simply extending bytes to chars.
	 */
	public static char[] toSimpleCharArray(byte[] barr) {
		if (barr == null) {
			return null;
		}
		char[] carr = new char[barr.length];
		for (int i = 0; i < barr.length; i++) {
			carr[i] = (char) barr[i];
		}
		return carr;
	}

	/**
	 * Converts byte array of default Jodd encoding to char array.
	 */
	public static char[] toCharArray(byte[] barr) throws UnsupportedEncodingException {
		return new String(barr, JoddDefault.encoding).toCharArray();
	}

	/**
	 * Converts byte array of specific encoding to char array.
	 */
	public static char[] toCharArray(byte[] barr, String charset) throws UnsupportedEncodingException {
		return new String(barr, charset).toCharArray();
	}

	/**
	 * Returns ASCII value of a char. In case of overload, 0x3F is returned.
	 */
	public static int toAscii(char c) {
		if (c <= 0xFF) {
			return c;
		} else {
			return 0x3F;
		}
	}

	// ---------------------------------------------------------------- find


	/**
	 * Match if one character equals to any of the given character.
	 *
	 * @return <code>true</code> if characters match any character from given array,
	 *         otherwise <code>false</code>
	 */
	public static boolean equalsOne(char c, char[] match) {
		for (char aMatch : match) {
			if (c == aMatch) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds index of the first character in given array the matches any from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstEqual(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (equalsOne(source[i], match) == true) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the matches any from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstEqual(char[] source, int index, char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] == match) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * Finds index of the first character in given array the differs from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstDiff(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (equalsOne(source[i], match) == false) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the differs from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstDiff(char[] source, int index, char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] != match) {
				return i;
			}
		}
		return -1;
	}

	// ---------------------------------------------------------------- is

	/**
	 * Returns <code>true</code> if character is a white space (<= ' ').
	 * White space definition is taken from String class (see: <code>trim()</code>).
	 */
	public static boolean isWhitespace(char c) {
		return c <= ' ';
	}

	/**
	 * Returns <code>true</code> if specified character is lowercase ASCII.
	 * If user uses only ASCIIs, it is much much faster.
	 */
	public static boolean isLowercaseLetter(char c) {
		return (c >= 'a') && (c <= 'z');
	}

	/**
	 * Returns <code>true</code> if specified character is uppercase ASCII.
	 * If user uses only ASCIIs, it is much much faster.
	 */
	public static boolean isUppercaseLetter(char c) {
		return (c >= 'A') && (c <= 'Z');
	}

	public static boolean isLetter(char c) {
		return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
	}

	public static boolean isDigit(char c) {
		return (c >= '0') && (c <= '9');
	}

	public static boolean isLetterOrDigit(char c) {
		return isDigit(c) || isLetter(c);
	}

	public static boolean isWordChar(char c) {
		return isDigit(c) || isLetter(c) || (c == '_');
	}

	public static boolean isPropertyNameChar(char c) {
		return isDigit(c) || isLetter(c) || (c == '_') || (c == '.') || (c == '[') || (c == ']');
	}

	// ---------------------------------------------------------------- conversions

	/**
	 * Uppers lowercase ASCII char.
	 */
	public static char toUpperAscii(char c) {
		if (isLowercaseLetter(c)) {
			c -= (char) 0x20;
		}
		return c;
	}


	/**
	 * Lowers uppercase ASCII char.
	 */
	public static char toLowerAscii(char c) {
		if (isUppercaseLetter(c)) {
			c += (char) 0x20;
		}
		return c;
	}

}
