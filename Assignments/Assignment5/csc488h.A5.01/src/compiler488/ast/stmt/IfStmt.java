package compiler488.ast.stmt;

import java.util.ArrayList;

import compiler488.ast.*;
import compiler488.ast.expn.*;
import compiler488.ast.type.*;
import compiler488.symbol.*;
import compiler488.semantics.*;

/**
 * Represents an if-then or an if-then-else construct.
 */
public class IfStmt extends Stmt {
	/** The condition that determines which branch to execute. */
	private Expn condition;

	/** Represents the statement to execute when the condition is true. */
	private ASTList<Stmt> whenTrue;

	/** Represents the statement to execute when the condition is false. */
	private ASTList<Stmt> whenFalse = null;

	public IfStmt(int line, int column, Expn condition, ASTList<Stmt> whenTrue,
			ASTList<Stmt> whenFalse) {
		super(line, column);

		this.condition = condition;
		this.whenTrue = whenTrue;
		this.whenFalse = whenFalse;
	}

	public IfStmt(int line, int column, Expn condition, ASTList<Stmt> whenTrue) {
		this(line, column, condition, whenTrue, null);
	}

	public Expn getCondition() {
		return condition;
	}

	public ASTList<Stmt> getWhenTrue() {
		return whenTrue;
	}

	public ASTList<Stmt> getWhenFalse() {
		return whenFalse;
	}

	/**
	 * Print a description of the <strong>if-then-else</strong> construct. If
	 * the <strong>else</strong> part is empty, just print an
	 * <strong>if-then</strong> construct.
	 */
	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print("if ");
		condition.prettyPrint(p);
		p.println(" then");
		whenTrue.prettyPrintBlock(p);

		if (whenFalse != null) {
			p.println(" else");
			whenFalse.prettyPrintBlock(p);
		}

		p.println("end");
	}

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors) {
		ExpnSemantics sem = condition.checkSemantics(majorScope, table, errors);
		boolean returning = sem.isReturning();
		
		if (!(sem.getType() instanceof BooleanType))
			errors.add(createError(30, "The type of expression must be boolean."));

		for (Stmt stmt : whenTrue)
			returning |= stmt.checkSemantics(majorScope, minorScope, table, errors).isReturning();

		if (whenFalse != null) {
			for (Stmt stmt : whenFalse)
				returning |= stmt.checkSemantics(majorScope, minorScope, table, errors).isReturning();
		}

		return new StmtSemantics(returning);
	}
}
