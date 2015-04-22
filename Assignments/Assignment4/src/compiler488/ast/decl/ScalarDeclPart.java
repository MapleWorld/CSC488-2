package compiler488.ast.decl;

import java.util.*;

import compiler488.ast.type.*;
import compiler488.symbol.*;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDeclPart extends DeclarationPart {
    public ScalarDeclPart(int line, int column, String name) {
        super(line, column, name);
    }

    /**
     * Returns a string describing the name of the object being
     * declared.
     */
    @Override
    public String toString() {
        return name;
    }

	@Override
	public Symbol toSymbol(Type type) {
		return new Symbol.Scalar(type, false);
	}

	@Override
	public void checkSemantics(ArrayList<String> errors) {
		// Nothing to do...
	}

}
