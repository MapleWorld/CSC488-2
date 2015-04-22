package compiler488.ast.stmt;

import java.util.ArrayList;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.semantics.*;
import compiler488.symbol.SymbolTable;

/**
 * The command to return from a function.
 */
public class ReturnStmt extends Stmt {
    /* The value to be returned by the function (if any.) */
    private Expn value = null;

    /**
     * Construct a function <code>return <em>value</em></code> statement with a value expression.
     *   @param  value  AST for the return expression
     */
    public ReturnStmt(int line, int column, Expn value) {
        super(line, column);

        this.value = value;
    }

    /**
     * Construct a procedure <code>return</code> statement (with no return value)
     */
    public ReturnStmt(int line, int column) {
        this(line, column, null);
    }

    public Expn getValue() {
        return value;
    }

    public void prettyPrint(PrettyPrinter p) {
        p.print("return");

        if (value != null) {
            p.print(" (");
            value.prettyPrint(p);
            p.print(")");
        }
    }

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors) {
		if (value == null) {
			if (!(majorScope instanceof MajorScope.Procedure))
				errors.add(createError(52, "This return statement must be inside a procedure."));
		} else {
			ExpnSemantics sem = value.checkSemantics(majorScope, table, errors);

			if (!(majorScope instanceof MajorScope.Function))
				errors.add(createError(51, "This return statement must be inside a function."));
			else if (sem.getType() != null && !majorScope.equals(new MajorScope.Function(sem.getType())))
				errors.add(createError(35, "The expression type must match the return type of the enclosing function."));
		}
		
		return new StmtSemantics(true);
	}
}
