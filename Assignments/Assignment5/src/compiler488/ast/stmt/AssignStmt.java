package compiler488.ast.stmt;

import java.util.ArrayList;

import compiler488.ast.*;
import compiler488.ast.expn.*;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Holds the assignment of an expression to a variable.
 */
public class AssignStmt extends Stmt {
	/** The location being assigned to. */
	private Expn lval;

	/** The value being assigned. */
	private Expn rval;

	public AssignStmt(int line, int column, Expn lval, Expn rval) {
		super(line, column);

		this.lval = lval;
		this.rval = rval;
	}

	public Expn getLval() {
		return lval;
	}

	public Expn getRval() {
		return rval;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		lval.prettyPrint(p);
		p.print(" <= ");
		rval.prettyPrint(p);
	}

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors) {
		ExpnSemantics lsem = lval.checkSemantics(majorScope, table, errors);
		ExpnSemantics rsem = rval.checkSemantics(majorScope, table, errors);

		// Hack, because the AST does not have a "reference" expression.
		if (lval instanceof IdentExpn) {
			IdentExpn ident = (IdentExpn)lval;
			Symbol symbol = table.getEntry(ident.getIdent());
			table.addEntry(ident.getIdent(), symbol);
			if (symbol instanceof Symbol.Function)
				errors.add(createError(104, "The target of an assignment cannot be a function."));
			
			// Another hack, because we thought we could assign parameters (with call-by-value semantics).
			if (symbol instanceof Symbol.Scalar && ((Symbol.Scalar)symbol).isParameter())
				errors.add(createError(105, "The target of an assignment cannot be a parameter."));			
		}
		
		// If getType() is null, then type checking failed for that particular expressions,
		// so there is no point in reporting a type mismatch error.
		if (lsem.getType() != null && rsem.getType() != null && !lsem.getType().equals(rsem.getType()))
			errors.add(createError(34, "Variable and expression in assignment must be the same type."));
		
		return new StmtSemantics(lsem.isReturning() || rsem.isReturning());
	}
}
