package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;


/**
 * Represents a literal integer constant.
 */
public class IntConstExpn extends ConstExpn {
    /**
     * The value of this literal.
     */
    private Integer value;

    public IntConstExpn(Integer line, Integer column, Integer value) {
        super(line, column);

        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString () {
        return value.toString();
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		return new ExpnSemantics(false, new IntegerType(-1, -1));
	}

}
