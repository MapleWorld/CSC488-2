package compiler488.symbol;

import java.util.*;

/**
 * Symbol Table This almost empty class is a framework for implementing a Symbol
 * Table class for the CSC488S compiler
 * 
 * Each implementation can change/modify/delete this class as they see fit.
 *
 * @author Anthony Vandikas
 */
public class SymbolTable {
	private ArrayDeque<HashMap<String, Symbol>> symbols = new ArrayDeque<HashMap<String, Symbol>>();

	public void pushScope() {
		symbols.push(new HashMap<String, Symbol>());
	}

	public void popScope() {
		symbols.pop();
	}

	public boolean addEntry(String name, Symbol symbol) {
		boolean exists = symbols.peek().containsKey(name);
		symbols.peek().put(name, symbol);
		return !exists;
	}

	public Symbol getEntry(String name) {
		Symbol output = null;
		for (HashMap<String, Symbol> map : symbols) {
			output = map.get(name);
			if (output != null)
				break;
		}

		return output;
	}
}
