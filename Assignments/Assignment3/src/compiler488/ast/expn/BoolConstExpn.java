package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Boolean literal constants.
 */
public class BoolConstExpn extends ConstExpn {
    /** The value of the constant */
    private boolean value;

    public BoolConstExpn(int line, int column, boolean value) {
        super(line, column);

        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString () {
        return value ? "true" : "false";
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		return new ExpnSemantics(false, new BooleanType(-1, -1));
	}

}
