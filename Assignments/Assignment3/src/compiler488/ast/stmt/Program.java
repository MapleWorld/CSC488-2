package compiler488.ast.stmt;

import compiler488.ast.ASTList;

/**
 * Placeholder for the scope that is the entire program
 */
public class Program extends Scope {
    public Program(int line, int column, ASTList<Stmt> body) {
        super(line, column, body);
    }

    public Program(Scope scope) {
        super(scope.getLine(), scope.getColumn(), scope.getBody());
    }
}
