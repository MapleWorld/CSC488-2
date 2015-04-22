package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;


/**
 * Place holder for all binary expression where both operands must be boolean
 * expressions.
 */
public class BoolExpn extends BinaryExpn {
    public final static String OP_OR 	= "|";
    public final static String OP_AND	= "&";

    public BoolExpn(int line, int column, String opSymbol, Expn left, Expn right) {
        super(line, column, opSymbol, left, right);

        assert ((opSymbol == OP_OR) ||
                (opSymbol == OP_AND));
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		ExpnSemantics lsem = left.checkSemantics(scope, table, errors);
		ExpnSemantics rsem = right.checkSemantics(scope, table, errors);
		if (lsem.getType() != null && !(lsem.getType() instanceof BooleanType))
			errors.add(left.createError(30, "The expression or variable must be a boolean."));
		if (rsem.getType() != null && !(rsem.getType() instanceof BooleanType))
			errors.add(right.createError(30, "The expression or variable must be a boolean."));
		
		return new ExpnSemantics(lsem.isReturning() || rsem.isReturning(), new BooleanType(-1, -1));
	}
}
