// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.lagarto.dom.Node;
import jodd.typeconverter.Convert;
import jodd.util.StringUtil;

import java.util.LinkedList;

/**
 * Pseudo functions.
 */
public abstract class PseudoFunction<E> {
	/**
	 * The <code>:nth-child(an+b)</code> pseudo-class notation represents an element that has an+b-1
	 * siblings before it in the document tree, for any positive integer or zero value of n,
	 * and has a parent element. For values of a and b greater than zero, this effectively divides
	 * the element's children into groups of a elements (the last group taking the remainder),
	 * and selecting the bth element of each group. For example, this allows the selectors
	 * to address every other row in a table, and could be used to alternate the color of
	 * paragraph text in a cycle of four. The a and b values must be integers (positive, negative, or zero).
	 * The index of the first child of an element is 1.
	 */
	public static class NTH_CHILD extends PseudoFunction<PseudoFunctionExpression> {

		@Override
		public PseudoFunctionExpression parseExpression(String expression) {
			return new PseudoFunctionExpression(expression);
		}

		@Override
		public boolean match(Node node, PseudoFunctionExpression expression) {
			int value = node.getSiblingElementIndex() + 1;

			return expression.match(value);
		}
	}

	/**
	 * The <code>:nth-last-child(an+b)</code> pseudo-class notation represents an element that has
	 * an+b-1 siblings after it in the document tree, for any positive integer or zero value
	 * of n, and has a parent element.
	 */
	public static class NTH_LAST_CHILD extends PseudoFunction<PseudoFunctionExpression> {

		@Override
		public PseudoFunctionExpression parseExpression(String expression) {
			return new PseudoFunctionExpression(expression);
		}

		@Override
		public boolean match(Node node, PseudoFunctionExpression expression) {
			int value = node.getParentNode().getChildElementsCount() - node.getSiblingElementIndex();

			return expression.match(value);
		}

	}

	/**
	 * The <code>:nth-of-type(an+b)</code> pseudo-class notation represents an element that
	 * has an+b-1 siblings with the same expanded element name before it in the document tree,
	 * for any zero or positive integer value of n, and has a parent element.
	 */
	public static class NTH_OF_TYPE extends PseudoFunction<PseudoFunctionExpression> {

		@Override
		public PseudoFunctionExpression parseExpression(String expression) {
			return new PseudoFunctionExpression(expression);
		}

		@Override
		public boolean match(Node node, PseudoFunctionExpression expression) {
			int value = node.getSiblingNameIndex() + 1;

			return expression.match(value);
		}

	}

	/**
	 * The <code>:nth-last-of-type(an+b)</code> pseudo-class notation represents an element
	 * that has an+b-1 siblings with the same expanded element name after it in the document tree,
	 * for any zero or positive integer value of n, and has a parent element.
	 */
	public static class NTH_LAST_OF_TYPE extends PseudoFunction<PseudoFunctionExpression> {

		@Override
		public PseudoFunctionExpression parseExpression(String expression) {
			return new PseudoFunctionExpression(expression);
		}

		@Override
		public boolean match(Node node, PseudoFunctionExpression expression) {
			Node child = node.getParentNode().getLastChildElement(node.getNodeName());
			int value = child.getSiblingNameIndex() + 1 - node.getSiblingNameIndex();

			return expression.match(value);
		}
	}

	// ---------------------------------------------------------------- extension

	/**
	 * Select the element at index n within the matched set.
	 */
	public static class EQ extends PseudoFunction<Integer> {

		@Override
		public Integer parseExpression(String expression) {
			return Convert.toInteger(expression);
		}

		@Override
		public boolean match(Node node, Integer expression) {
			return true;
		}

		@Override
		public boolean match(LinkedList<Node> currentResults, Node node, int index, Integer expression) {
			int value = expression.intValue();
			if (value >= 0) {
				return index == value;
			} else {
				return index == currentResults.size() + value;
			}
		}
	}

	/**
	 * Select all elements at an index greater than index within the matched set.
	 */
	public static class GT extends PseudoFunction<Integer> {

		@Override
		public Integer parseExpression(String expression) {
			return Convert.toInteger(expression);
		}

		@Override
		public boolean match(Node node, Integer expression) {
			return true;
		}

		@Override
		public boolean match(LinkedList<Node> currentResults, Node node, int index, Integer expression) {
			int value = expression.intValue();
			return index > value;
		}
	}

	/**
	 *  Select all elements at an index less than index within the matched set.
	 */
	public static class LT extends PseudoFunction<Integer> {

		@Override
		public Integer parseExpression(String expression) {
			return Convert.toInteger(expression);
		}

		@Override
		public boolean match(Node node, Integer expression) {
			return true;
		}

		@Override
		public boolean match(LinkedList<Node> currentResults, Node node, int index, Integer expression) {
			int value = expression.intValue();
			return index < value;
		}
	}

	/**
	 * Selects all elements that contain the specified text.
	 */
	public static class CONTAINS extends PseudoFunction<String> {

		@Override
		public String parseExpression(String expression) {
			if (StringUtil.startsWithChar(expression, '\'') || StringUtil.startsWithChar(expression, '"')) {
				expression = expression.substring(1, expression.length() - 1);
			}
			return expression;
		}

		@Override
		public boolean match(Node node, String expression) {
			String text = node.getTextContent();
			return text.contains(expression);
		}
	}

	// ---------------------------------------------------------------- interface

	/**
	 * Parses expression before usage.
	 */
	public abstract E parseExpression(String expression);
	
	/**
	 * Matches node using provided parsed expression.
	 */
	public abstract boolean match(Node node, E expression);
	
	/**
	 * Returns <code>true</code> if node matches the pseudoclass within current results.
	 */
	public boolean match(LinkedList<Node> currentResults, Node node, int index, E expression) {
		return true;
	}

	/**
	 * Returns pseudo-function name.
	 */
	public String getPseudoFunctionName() {
		String name = getClass().getSimpleName().toLowerCase();
		name = name.replace('_', '-');
		return name;
	}

}