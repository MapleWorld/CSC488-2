package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;


/**
 * Place holder for all binary expression where both operands must be integer
 * expressions.
 */
public class ArithExpn extends BinaryExpn {
    public final static String OP_PLUS 		= "+";
    public final static String OP_MINUS 	= "-";
    public final static String OP_TIMES 	= "*";
    public final static String OP_DIVIDE 	= "/";

    public ArithExpn(int line, int column, String opSymbol, Expn left, Expn right) {
        super(line, column, opSymbol, left, right);

        assert ((opSymbol == OP_PLUS) ||
                (opSymbol == OP_MINUS) ||
                (opSymbol == OP_TIMES) ||
                (opSymbol == OP_DIVIDE));
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		ExpnSemantics lsem = left.checkSemantics(scope, table, errors);
		ExpnSemantics rsem = right.checkSemantics(scope, table, errors);
		if (lsem.getType() != null && !(lsem.getType() instanceof IntegerType))
			errors.add(left.createError(31, "The expression or variable must be an integer."));
		if (rsem.getType() != null && !(rsem.getType() instanceof IntegerType))
			errors.add(right.createError(31, "The expression or variable must be an integer."));
		
		// If you see -1s in the error message, it means I've screwed up...
		return new ExpnSemantics(lsem.isReturning() || rsem.isReturning(), new IntegerType(-1, -1));
	}
}
