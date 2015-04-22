package compiler488.ast.stmt;

import compiler488.ast.*;

/**
 * Represents a loop in which the exit condition is evaluated before each pass. <- LIES!!!!!!
 */
public class LoopStmt extends LoopingStmt {
    public LoopStmt(int line, int column, ASTList<Stmt> body) {
        super(line, column, body);
    }

    /**
     * Pretty-print this AST node as a <code>loop</code> loop.
     */
    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.println("loop");
        body.prettyPrintNewlines(p);
        p.println("end");
    }
}
