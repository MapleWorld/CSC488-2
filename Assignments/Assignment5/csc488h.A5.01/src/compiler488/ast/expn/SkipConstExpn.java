package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.Printable;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Represents the special literal constant associated with writing a new-line
 * character on the output device.
 */
public class SkipConstExpn extends ConstExpn implements Printable {
    public SkipConstExpn(int line, int column) {
        super(line, column);
    }

    @Override
    public String toString() {
        return "skip";
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		return new ExpnSemantics(false, null);
	}
}
