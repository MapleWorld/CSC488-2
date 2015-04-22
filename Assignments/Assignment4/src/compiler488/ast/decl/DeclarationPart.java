package compiler488.ast.decl;

import java.util.*;

import compiler488.ast.BaseAST;
import compiler488.ast.type.*;
import compiler488.symbol.*;

/**
 * The common features of declarations' parts.
 */
public abstract class DeclarationPart extends BaseAST {
    /** The name of the thing being declared. */
    protected String name;

    public DeclarationPart(int line, int column, String name) {
        super(line, column);

        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public abstract Symbol toSymbol(Type type);
    public abstract void checkSemantics(ArrayList<String> errors);
}
