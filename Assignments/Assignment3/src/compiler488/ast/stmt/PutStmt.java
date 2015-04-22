package compiler488.ast.stmt;

import java.util.ArrayList;

import compiler488.ast.*;
import compiler488.ast.expn.*;
import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.SymbolTable;

/**
 * The command to write data on the output device.
 */
public class PutStmt extends Stmt {
    /** The values to be printed. */
    private ASTList<Printable> outputs;

    public PutStmt(int line, int column, ASTList<Printable> outputs) {
        super(line, column);

        this.outputs = outputs;
    }

    public ASTList<Printable> getOutputs() {
        return outputs;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print("put ");
        outputs.prettyPrintCommas(p);
    }

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope, 
			SymbolTable table, ArrayList<String> errors) {
		boolean returning = false;
		
		for (Printable input : outputs) {
			if (input instanceof Expn) {
				Expn expn = (Expn)input;
				ExpnSemantics sem = expn.checkSemantics(majorScope, table, errors);
				if (sem.getType() != null && !(sem.getType() instanceof IntegerType))
					errors.add(createError(31, "The type of the expression or variable must be integer."));
				
				returning |= sem.isReturning();
			}
		}
		
		return new StmtSemantics(returning);
	}
}
