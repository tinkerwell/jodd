// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.lagarto.csselly.CSSellyException;
import jodd.lagarto.csselly.Selector;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeFilter;
import jodd.lagarto.dom.NodeListFilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Pseudo class selector.
 * A pseudo-class always consists of a "colon" (:) followed by
 * the name of the pseudo-class and optionally by a value between parentheses.
 * <p>
 * Selectors introduces the concept of structural pseudo-classes to permit
 * selection based on extra information that lies in the document tree
 * but cannot be represented by other simple selectors or combinators.
 * <p>
 * Standalone text and other non-element nodes are not counted when calculating
 * the position of an element in the list of children of its parent.
 * When calculating the position of an element in the list of children
 * of its parent, the index numbering starts at 1.
 */
public class PseudoClassSelector extends Selector implements NodeFilter, NodeListFilter {

	protected static final Map<String, PseudoClass> PSEUDO_CLASS_MAP;

	static {
		PSEUDO_CLASS_MAP = new HashMap<String, PseudoClass>(26);

		registerPseudoClass(PseudoClass.EMPTY.class);
		registerPseudoClass(PseudoClass.FIRST_CHILD.class);
		registerPseudoClass(PseudoClass.FIRST_OF_TYPE.class);
		registerPseudoClass(PseudoClass.LAST_CHILD.class);
		registerPseudoClass(PseudoClass.LAST_OF_TYPE.class);
		registerPseudoClass(PseudoClass.ONLY_CHILD.class);
		registerPseudoClass(PseudoClass.ONLY_OF_TYPE.class);
		registerPseudoClass(PseudoClass.ROOT.class);

		registerPseudoClass(PseudoClass.FIRST.class);
		registerPseudoClass(PseudoClass.LAST.class);
		registerPseudoClass(PseudoClass.BUTTON.class);
		registerPseudoClass(PseudoClass.CHECKBOX.class);
		registerPseudoClass(PseudoClass.FILE.class);
		registerPseudoClass(PseudoClass.IMAGE.class);
		registerPseudoClass(PseudoClass.INPUT.class);
		registerPseudoClass(PseudoClass.HEADER.class);
		registerPseudoClass(PseudoClass.PARENT.class);
		registerPseudoClass(PseudoClass.PASSWORD.class);
		registerPseudoClass(PseudoClass.RADIO.class);
		registerPseudoClass(PseudoClass.RESET.class);
		registerPseudoClass(PseudoClass.SELECTED.class);
		registerPseudoClass(PseudoClass.CHECKED.class);
		registerPseudoClass(PseudoClass.SUBMIT.class);
		registerPseudoClass(PseudoClass.TEXT.class);
		registerPseudoClass(PseudoClass.EVEN.class);
		registerPseudoClass(PseudoClass.ODD.class);
	}

	/**
	 * Registers pseudo class.
	 */
	public static void registerPseudoClass(Class<? extends PseudoClass> pseudoClassType) {
		PseudoClass pseudoClass;
		try {
			pseudoClass = pseudoClassType.newInstance();
		} catch (Exception ex) {
			throw new CSSellyException(ex);
		}
		PSEUDO_CLASS_MAP.put(pseudoClass.getPseudoClassName(), pseudoClass);
	}

	/**
	 * Lookups pseudo class for given pseudo class name.
	 */
	public static PseudoClass lookupPseudoClass(String pseudoClassName) {
		PseudoClass pseudoClass = PSEUDO_CLASS_MAP.get(pseudoClassName);
		if (pseudoClass == null) {
			throw new CSSellyException("Unsupported pseudo class: " + pseudoClassName);
		}
		return pseudoClass;
	}

	// ---------------------------------------------------------------- selector

	protected final PseudoClass pseudoClass;

	public PseudoClassSelector(String pseudoClassName) {
		super(Type.PSEUDO_CLASS);
		this.pseudoClass = lookupPseudoClass(pseudoClassName.trim());
	}

	/**
	 * Returns {@link PseudoClass pseudo class} value.
	 */
	public PseudoClass getPseudoClass() {
		return pseudoClass;
	}

	/**
	 * Matches node to this selector.
	 */
	public boolean accept(Node node) {
		return pseudoClass.match(node);
	}

	/**
	 * Accepts node within selected results. Invoked after results are matched.
	 */
	public boolean accept(LinkedList<Node> currentResults, Node node, int index) {
		return pseudoClass.match(currentResults, node, index);
	}

}