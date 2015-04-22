package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;


/**
 * Represents the boolean negation of an expression.
 */
public class NotExpn extends UnaryExpn {
    public NotExpn(int line, int column, Expn operand) {
        super(line, column, UnaryExpn.OP_NOT, operand);
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		ExpnSemantics sem = getOperand().checkSemantics(scope, table, errors);
		if (sem.getType() != null && !(sem.getType() instanceof BooleanType))
			errors.add(getOperand().createError(30, "The expression or variable must be a boolean."));
		return new ExpnSemantics(sem.isReturning(), new BooleanType(-1, -1));
	}
}
