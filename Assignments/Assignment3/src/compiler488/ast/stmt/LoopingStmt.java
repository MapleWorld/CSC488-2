package compiler488.ast.stmt;

import java.util.ArrayList;

import compiler488.ast.*;
import compiler488.ast.expn.*;
import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Represents the common parts of loops.
 */
public abstract class LoopingStmt extends Stmt {
    /** The control expression for the looping construct (if any.) */
    protected Expn expn = null;

    /** The body of the looping construct. */
    protected ASTList<Stmt> body;

    public LoopingStmt(int line, int column, Expn expn, ASTList<Stmt> body) {
        super(line, column);

        this.expn = expn;
        this.body = body;
    }

    public LoopingStmt(int line, int column, ASTList<Stmt> body) {
        this(line, column, null, body);
    }

    public Expn getExpn() {
        return expn;
    }

    public ASTList<Stmt> getBody() {
        return body;
    }

    @Override
    public StmtSemantics checkSemantics(
    		MajorScope majorScope, MinorScope minorScope,
    		SymbolTable table, ArrayList<String> errors) {
    	boolean returning = false;
	
    	if (expn != null) {
    		ExpnSemantics sem = expn.checkSemantics(majorScope, table, errors);
    		if (!(sem.getType() instanceof BooleanType))
    			errors.add(createError(30, "The expression must be a boolean."));
    		
    		returning |= sem.isReturning();
    	}

		for (Stmt stmt : body) {
			StmtSemantics sem = stmt.checkSemantics(majorScope, new MinorScope.Loop(), table, errors);
			returning |= sem.isReturning();
		}

		return new StmtSemantics(returning);
    }
}
