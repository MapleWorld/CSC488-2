package compiler488.ast.decl;

import java.util.ArrayList;

import compiler488.ast.stmt.Stmt;
import compiler488.ast.type.Type;
import compiler488.semantics.*;
import compiler488.symbol.SymbolTable;

/**
 * The common features of declarations.
 */
public abstract class Declaration extends Stmt {
    /** The name of the thing being declared. */
    protected String name;

    /** The type of thing being declared. */
    protected Type type = null;

    public Declaration(int line, int column, String name, Type type) {
        super(line, column);

        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
    
    @Override
    public abstract StmtSemantics checkSemantics(
    		MajorScope majorScope, MinorScope minorScope,
    		SymbolTable table, ArrayList<String> errors);
}
