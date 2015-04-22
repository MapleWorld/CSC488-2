package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.Readable;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 *  References to a scalar variable or function call without parameters.
 */
public class IdentExpn extends Expn implements Readable {
    /** Name of the identifier. */
    private String ident;

    public IdentExpn(int line, int column, String ident) {
        super(line, column);

        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }

    /**
     * Returns the name of the variable or function.
     */
    @Override
    public String toString() {
        return ident;
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		Symbol symbol = table.getEntry(ident);
		if (symbol == null) {
			errors.add(createError(100, "Identifier '" + ident + "' does not exist."));
			return new ExpnSemantics(false, null);
		} else if (symbol instanceof Symbol.Scalar) {
			Symbol.Scalar scalar = (Symbol.Scalar)symbol;
			return new ExpnSemantics(false, scalar.getType());
		} else if (symbol instanceof Symbol.Function) {
			Symbol.Function func = (Symbol.Function)symbol;
			if (func.getParameters().iterator().hasNext()) {
				errors.add(createError(42, "The function does not have zero parameters."));
				return new ExpnSemantics(false, null);
			} else {
				return new ExpnSemantics(false, func.getOutput());
			}
		} else {
			errors.add(createError(101, "Identifier '" + ident + "' has an invalid type."));
			return new ExpnSemantics(false, null);
		}
	}
}
