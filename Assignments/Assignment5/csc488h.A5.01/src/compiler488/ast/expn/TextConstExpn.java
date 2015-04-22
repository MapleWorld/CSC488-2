package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.Printable;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Represents a literal text constant.
 */
public class TextConstExpn extends ConstExpn implements Printable {
    /** The value of this literal. */
    private String value;

    public TextConstExpn(int line, int column, String value) {
        super(line, column);

        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Returns a description of the literal text constant.
     */
    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		return new ExpnSemantics(false, null);
	}

}
