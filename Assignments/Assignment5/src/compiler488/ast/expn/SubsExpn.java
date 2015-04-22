package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.Readable;
import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * References to an array element variable
 */
public class SubsExpn extends Expn implements Readable {
    /** Name of the array variable. */
    private String variable;

    /** First subscript. */
    private Expn subscript1;

    /** Second subscript (if any.) */
    private Expn subscript2 = null;

    public SubsExpn(int line, int column, String variable, Expn subscript1, Expn subscript2) {
        super(line, column);

        this.variable = variable;
        this.subscript1 = subscript1;
        this.subscript2 = subscript2;
    }

    public SubsExpn(int line, int column, String variable, Expn subscript1) {
        this(line, column, variable, subscript1, null);
    }

    public String getVariable() {
        return variable;
    }

    public Expn getSubscript1() {
        return subscript1 ;
    }

    public Expn getSubscript2() {
        return subscript2;
    }

    public int numSubscripts() {
        return 1 + (subscript2 != null ? 1 : 0);
    }

    public void prettyPrint(PrettyPrinter p) {
        p.print(variable + "[");

        subscript1.prettyPrint(p);

        if (subscript2 != null) {
            p.print(", ");
            subscript2.prettyPrint(p);
        }

        p.print("]");
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		boolean returning = false;
		Type type = null;

		Symbol symbol = table.getEntry(variable);
		if (symbol == null)
			errors.add(createError(100, "Identifier '" + variable + "' does not exist."));

		ExpnSemantics lsem = subscript1.checkSemantics(scope, table, errors);
		if (lsem.getType() != null && !(lsem.getType() instanceof IntegerType))
			errors.add(subscript1.createError(31, "The expression or variable must be an integer."));
		returning |= lsem.isReturning();
		if (symbol instanceof Symbol.Array1D)
			type = ((Symbol.Array1D)symbol).getType();

		if (subscript2 != null) {
			ExpnSemantics rsem = subscript2.checkSemantics(scope, table, errors);
			if (rsem.getType() != null && !(rsem.getType() instanceof IntegerType))
				errors.add(subscript2.createError(30, "The expression or variable must be an integer."));
			returning |= rsem.isReturning();
			if (symbol instanceof Symbol.Array2D)
				type = ((Symbol.Array2D)symbol).getType();
		}

		if (type == null && symbol != null)
			errors.add(createError(102, "The given variable is not the proper array type."));

		return new ExpnSemantics(returning, type);
	}
}
