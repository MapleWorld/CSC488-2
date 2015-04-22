package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;


/**
 * Place holder for all ordered comparisons expression where both operands must
 * be integer expressions.  e.g. &lt; , &gt;  etc. comparisons
 */
public class CompareExpn extends BinaryExpn {
    public final static String OP_LESS 			= "<";
    public final static String OP_LESS_EQUAL 	= "<=";
    public final static String OP_GREATER 		= ">";
    public final static String OP_GREATER_EQUAL	= ">=";

    public CompareExpn(int line, int column, String opSymbol, Expn left, Expn right) {
        super(line, column, opSymbol, left, right);

        assert ((opSymbol == OP_LESS) ||
                (opSymbol == OP_LESS_EQUAL) ||
                (opSymbol == OP_GREATER) ||
                (opSymbol == OP_GREATER_EQUAL));
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		ExpnSemantics lsem = left.checkSemantics(scope, table, errors);
		ExpnSemantics rsem = right.checkSemantics(scope, table, errors);
		if (lsem.getType() != null && !(lsem.getType() instanceof IntegerType))
			errors.add(left.createError(31, "The expression or variable must be an integer."));
		if (rsem.getType() != null && !(rsem.getType() instanceof IntegerType))
			errors.add(right.createError(31, "The expression or variable must be an integer."));
		
		return new ExpnSemantics(lsem.isReturning() || rsem.isReturning(), new BooleanType(-1, -1));
	}
}
