package compiler488.ast.stmt;

import java.util.ArrayList;

import compiler488.ast.BaseAST;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * A placeholder for statements.
 */
public abstract class Stmt extends BaseAST {
	public Stmt(int line, int column) {
		super(line, column);
	}
	
	/**
	 * Checks if this statement is semantically correct.
	 * 
	 * @param table
	 * @param errors
	 * @return The return value indicates whether this statement causes a function or procedure to return.
	 */
	public abstract StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors);
}
