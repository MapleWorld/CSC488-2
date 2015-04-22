package compiler488.ast.stmt;

import java.util.ArrayList;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.*;
import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.SymbolTable;

/**
 * Represents the command to exit from a loop.
 */
public class ExitStmt extends Stmt {
    /** Condition expression for <code>exit when</code> variation. */
    private Expn expn = null;

    public ExitStmt(int line, int column, Expn expn) {
        super(line, column);

        this.expn = expn;
    }

    public ExitStmt(int line, int column) {
        this(line, column, null);
    }

    public Expn getExpn() {
        return expn;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print("exit");

        if (expn != null) {
            p.print(" when ");
            expn.prettyPrint(p);
        }
    }

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors) {
		if (!(minorScope instanceof MinorScope.Loop))
			errors.add(createError(50, "An exit statement must occur directly inside a loop."));

		boolean returning = false;

		if (expn != null) {
			ExpnSemantics sem = expn.checkSemantics(majorScope, table, errors);
			if (sem.getType() != null && !(sem.getType() instanceof BooleanType))
				errors.add(expn.createError(30, "The expression or variable must be a boolean."));
			
			returning |= sem.isReturning();
		}

		return new StmtSemantics(returning);
	}
}
