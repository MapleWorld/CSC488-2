package compiler488.ast.decl;

import java.util.*;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.stmt.Scope;
import compiler488.ast.type.Type;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Represents the declaration of a function or procedure.
 */
public class RoutineDecl extends Declaration {
    /** The formal parameters for this routine (if any.)
     *
     * <p>This value must be non-<code>null</code>. If absent, use an empty
     * list instead.</p>
     */
    private ASTList<ScalarDecl> parameters =  new ASTList<ScalarDecl>();

    /** The body of this routine (if any.) */
    private Scope body = null;

    /**
     * Construct a function with parameters, and a definition of the body.
     *   @param  name	      Name of the routine
     *   @param  type	      Type returned by the function
     *   @param  parameters   List of parameters to the routine
     *   @param  body	      Body scope for the routine
     */
    public RoutineDecl(int line, int column, String name, Type type, ASTList<ScalarDecl> parameters, Scope body) {
    	// NOTE: line/column refer to the name of the routine.
        super(line, column, name, type);

        this.parameters = parameters;
        this.body = body;
    }

    /**
     * Construct a function with no parameters, and a definition of the body.
     *   @param  name	      Name of the routine
     *   @param  type	      Type returned by the function
     *   @param  body	      Body scope for the routine
     */
    public RoutineDecl(int line, int column, String name, Type type, Scope body) {
        this(line, column, name, type, new ASTList<ScalarDecl>(), body);
    }

    /**
     * Construct a procedure with parameters, and a definition of the body.
     *   @param  name	      Name of the routine
     *   @param  parameters   List of parameters to the routine
     *   @param  body	      Body scope for the routine
     */
    public RoutineDecl(int line, int column, String name, ASTList<ScalarDecl> parameters, Scope body) {
        super(line, column, name, null);

        this.parameters = parameters;
        this.body = body;
    }

    /**
     * Construct a procedure with no parameters, and a definition of the body.
     *   @param  name	      Name of the routine
     *   @param  body	      Body scope for the routine
     */
    public RoutineDecl(int line, int column, String name, Scope body) {
        this(line, column, name, new ASTList<ScalarDecl>(), body);
    }

    public ASTList<ScalarDecl> getParameters() {
        return parameters;
    }

    public Scope getBody() {
        return body;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        if (type == null) {
            p.print("procedure ");
        } else {
            type.prettyPrint(p);
            p.print(" function ");
        }

        p.print(name);

        if (parameters.size() > 0) {
            p.print("(");
            parameters.prettyPrintCommas(p);
            p.print(")");
        }

        if (body != null) {
            p.print(" ");
            body.prettyPrint(p);
        }
    }

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors) {
		ArrayList<Type> paramTypes = new ArrayList<Type>();
		for (ScalarDecl param : parameters)
			paramTypes.add(param.getType());
		
		Symbol symbol = type == null ?
			new Symbol.Procedure(paramTypes) :
			new Symbol.Function(type, paramTypes);
		if (!table.addEntry(name, symbol))
			errors.add(createError(103, "The function or procedure has already been declared in the current scope."));

		table.pushScope();

		for (ScalarDecl param : parameters)
			param.checkSemantics(majorScope, minorScope, table, errors);
		
		if (type == null)
			body.checkSemantics(new MajorScope.Procedure(), new MinorScope.None(), table, errors);
		else {
			StmtSemantics sem = body.checkSemantics(new MajorScope.Function(type), new MinorScope.None(), table, errors);
			if (!sem.isReturning())
				errors.add(createError(53, "The function must have at least one return statement."));
		}
		
		table.popScope();

		return new StmtSemantics(false);
	}
}
