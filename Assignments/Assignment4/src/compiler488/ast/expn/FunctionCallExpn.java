package compiler488.ast.expn;

import java.util.*;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.type.*;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Represents a function call with arguments.
 */
public class FunctionCallExpn extends Expn {
    /** The name of the function. */
    private String ident;

    /** The arguments passed to the function. */
    private ASTList<Expn> arguments;

    public FunctionCallExpn(int line, int column, String ident, ASTList<Expn> arguments) {
        super(line, column);

        this.ident = ident;
        this.arguments = arguments;
    }

    public ASTList<Expn> getArguments() {
        return arguments;
    }

    public String getIdent() {
        return ident;
    }

    public void prettyPrint(PrettyPrinter p) {
        p.print(ident);

        if (arguments.size() > 0) {
            p.print("(");
            arguments.prettyPrintCommas(p);
            p.print(")");
        }
    }

	@Override
	public ExpnSemantics checkSemantics(MajorScope scope, SymbolTable table, ArrayList<String> errors) {
		// Check that name exists.
		Symbol entry = table.getEntry(ident);
		if (entry == null)
			errors.add(createError(100, "Identifier '" + ident + "' does not exist."));

		boolean returning = false;
		Type output = null;
		Iterator<Expn> argumentIter = arguments.iterator();
		
		// Check that name refers to a function.
		if (!(entry instanceof Symbol.Function))
			errors.add(createError(40, "Identifier '" + ident + "' has not been declared as a function."));
		else {
			Symbol.Function func = (Symbol.Function)entry;
			output = func.getOutput();
			Iterator<Type> parameterIter = func.getParameters().iterator();
			
			// Check that no arguments => no parameters.
			if (!argumentIter.hasNext() && parameterIter.hasNext())
				errors.add(createError(42, "The procedure has parameters."));

			// Check that argument and parameter types match.
			while (parameterIter.hasNext() && argumentIter.hasNext()) {
				Type parameterType = parameterIter.next();
				Expn argument = argumentIter.next();
				
				// Check parameter.
				ExpnSemantics argSem = argument.checkSemantics(scope, table, errors);
				returning |= argSem.isReturning();
				
				// parameterType or argumentType == null => there was an error when computing the types
				// of the corresponding expression, so don't report an error.
				if (parameterType != null && argSem.getType() != null && !parameterType.equals(argSem.getType()))
					errors.add(argument.createError(36, "The argument type must match the parameter type."));
			}
			
			if (parameterIter.hasNext() || argumentIter.hasNext())
				errors.add(createError(43, "The number of arguments does not match the number of parameters."));
		}
		
		// Check remaining parameters.
		while (argumentIter.hasNext())
			returning |= argumentIter.next().checkSemantics(scope, table, errors).isReturning();
		
		return new ExpnSemantics(returning, output);	
	}
}
