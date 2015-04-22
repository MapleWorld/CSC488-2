package compiler488.ast.stmt;

import java.util.ArrayList;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.semantics.*;
import compiler488.symbol.SymbolTable;

/**
 * Represents the declarations and instructions of a scope construct.
 */
public class Scope extends Stmt {
    /** Body of the scope, mixed list of declarations and statements. */
    protected ASTList<Stmt> body;

    public Scope(int line, int column) {
        super(line, column);

        this.body = null;
    }

    public Scope(int line, int column, ASTList<Stmt> body) {
        this(line, column);

        this.body = body;
    }

    public ASTList<Stmt> getBody() {
        return body;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.println("begin");
        if (body != null && body.size() > 0) {
            body.prettyPrintBlock(p);
        }
        p.print("end");
    }

    @Override
    public StmtSemantics checkSemantics(
    		MajorScope majorScope, MinorScope minorScope,
    		SymbolTable table, ArrayList<String> errors) {
    	boolean returning = false;
    	table.pushScope();
    	if (body != null) {
	    	for (Stmt stmt : body)
	    		returning |= stmt.checkSemantics(majorScope, minorScope, table, errors).isReturning();
    	}
    	table.popScope();
		return new StmtSemantics(returning);
    }
}
