package compiler488.ast.stmt;

import java.util.ArrayList;
import java.util.Iterator;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.ast.type.Type;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Represents calling a procedure.
 */
public class ProcedureCallStmt extends Stmt {
    /** The name of the procedure being called. */
    private String name;

    /** The arguments passed to the procedure (if any.)
     *
     * <p>This value must be non-<code>null</code>. If the procedure takes no
     * parameters, represent that with an empty list here instead.</p>
     */
    private ASTList<Expn> arguments;

    public ProcedureCallStmt(int line, int column, String name, ASTList<Expn> arguments) {
        super(line, column);

        this.name = name;
        this.arguments = arguments;
    }

    public ProcedureCallStmt(int line, int column, String name) {
        this(line, column, name, new ASTList<Expn>());
    }

    public String getName() {
        return name;
    }

    public ASTList<Expn> getArguments() {
        return arguments;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print(name);

        if ((arguments != null) && (arguments.size() > 0)) {
            p.print("(");
            arguments.prettyPrintCommas(p);
            p.print(")");
        }
    }

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors) {
		// Check that name exists.
		Symbol entry = table.getEntry(name);
		if (entry == null)
			errors.add(createError(100, "Identifier '" + name + "' does not exist."));

		boolean returning = false;
		Iterator<Expn> argumentIter = arguments.iterator();
		
		// Check that name refers to a procedure.
		if (!(entry instanceof Symbol.Procedure))
			errors.add(createError(40, "Identifier '" + name + "' has not been declared as a procedure."));
		else {
			Iterator<Type> parameterIter = ((Symbol.Procedure)entry).getParameters().iterator();
			
			// Check that no arguments => no parameters.
			if (!argumentIter.hasNext() && parameterIter.hasNext())
				errors.add(createError(42, "The procedure has parameters."));

			// Check that argument and parameter types match.
			while (parameterIter.hasNext() && argumentIter.hasNext()) {
				Type parameterType = parameterIter.next();
				Expn argument = argumentIter.next();
				
				// Check parameter.
				ExpnSemantics argSem = argument.checkSemantics(majorScope, table, errors);
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
			returning |= argumentIter.next().checkSemantics(majorScope, table, errors).isReturning();
		
		return new StmtSemantics(returning);
	}
}
