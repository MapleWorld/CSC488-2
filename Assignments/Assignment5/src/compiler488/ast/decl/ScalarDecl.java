package compiler488.ast.decl;

import java.util.*;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.type.Type;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDecl extends Declaration {
    public ScalarDecl(int line, int column, String name, Type type) {
    	// NOTE: line/column refer to the location of name, not type.
    	super(line, column, name, type);
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print(type + " " + name);
    }

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors) {
		if (!table.addEntry(name, new Symbol.Scalar(type, true)))
			errors.add(createError(103, "The parameter has already been declared in the current scope."));
		return new StmtSemantics(false);
	}
}
