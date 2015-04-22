package compiler488.ast.stmt;

import compiler488.ast.*;
import compiler488.ast.expn.Expn;

/**
 * Represents a loop in which the exit condition is evaluated before each pass.
 */
public class WhileDoStmt extends LoopingStmt {
    public WhileDoStmt(int line, int column, Expn expn, ASTList<Stmt> body) {
        super(line, column, expn, body);
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print("while ");
        expn.prettyPrint(p);
        p.println(" do");

        body.prettyPrintBlock(p);

        p.println("end");
    }
}
