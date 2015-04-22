package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;


/**
 * Place holder for all binary expression where both operands could be either
 * integer or boolean expressions. e.g. = and != comparisons
 */
public class EqualsExpn extends BinaryExpn {
    public final static String OP_EQUAL 	= "=";
    public final static String OP_NOT_EQUAL	= "!=";

    public EqualsExpn(int line, int column, String opSymbol, Expn left, Expn right) {
        super(line, column, opSymbol, left, right);

        assert ((opSymbol == OP_EQUAL) ||
                (opSymbol == OP_NOT_EQUAL));
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		ExpnSemantics lsem = left.checkSemantics(scope, table, errors);
		ExpnSemantics rsem = right.checkSemantics(scope, table, errors);
		if (lsem.getType() != null && rsem.getType() != null && !lsem.getType().equals(rsem.getType()))
			errors.add(createError(32, "The left and right operands must be the same type."));
		
		return new ExpnSemantics(lsem.isReturning() || rsem.isReturning(), new BooleanType(-1, -1));
	}}
