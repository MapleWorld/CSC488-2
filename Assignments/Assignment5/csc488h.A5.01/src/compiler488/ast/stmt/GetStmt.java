package compiler488.ast.stmt;

import java.util.ArrayList;

import compiler488.ast.*;
import compiler488.ast.expn.*;
import compiler488.ast.type.*;
import compiler488.symbol.*;
import compiler488.semantics.*;

/**
 * The command to read data into one or more variables.
 */
public class GetStmt extends Stmt {
    /** A list of locations to store the values read. */
    private ASTList<Expn> inputs;

    public GetStmt (int line, int column, ASTList<Expn> inputs) {
        super(line, column);

        this.inputs = inputs;
    }

    public ASTList<Expn> getInputs() {
        return inputs;
    }

    public void prettyPrint(PrettyPrinter p) {
        p.print("get ");
        inputs.prettyPrintCommas(p);
    }

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors) {
		boolean returning = false;
		
		for (Expn input : inputs) {
			ExpnSemantics sem = input.checkSemantics(majorScope, table, errors);
			if (!(sem.getType() instanceof IntegerType))
				errors.add(createError(31, "The type of the expression or variable must be integer."));
			
			returning |= sem.isReturning();
		}
		
		return new StmtSemantics(returning);
	}
}
