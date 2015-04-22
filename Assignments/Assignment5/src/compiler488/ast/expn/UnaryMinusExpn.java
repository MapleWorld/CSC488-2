package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Represents negation of an integer expression
 */
public class UnaryMinusExpn extends UnaryExpn {
    public UnaryMinusExpn(int line, int column, Expn operand) {
        super(line, column, UnaryExpn.OP_MINUS, operand);
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		ExpnSemantics sem = getOperand().checkSemantics(scope, table, errors);
		if (sem.getType() != null && !(sem.getType() instanceof IntegerType))
			errors.add(getOperand().createError(30, "The expression or variable must be an integer."));
		return new ExpnSemantics(sem.isReturning(), new IntegerType(-1, -1));
	}
}
