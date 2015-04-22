package compiler488.ast.expn;

import java.util.ArrayList;

import compiler488.ast.*;
import compiler488.symbol.*;
import compiler488.semantics.*;

/**
 * A placeholder for all expressions.
 */
public abstract class Expn extends BaseAST implements Printable {
	public Expn(int line, int column) {
		super(line, column);
	}
	
	public abstract ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors);
}
