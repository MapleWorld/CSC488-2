package compiler488.ast.decl;

import java.util.*;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.type.Type;
import compiler488.semantics.*;
import compiler488.symbol.*;

/**
 * Holds the declaration of multiple elements.
 */
public class MultiDeclarations extends Declaration {
    /** The parts being declared */
    private ASTList<DeclarationPart> elements;

    public MultiDeclarations(Type type, ASTList<DeclarationPart> elements) {
        super(type.getLine(), type.getColumn(), null, type);

        this.elements = elements;
    }

    public ASTList<DeclarationPart> getParts() {
        return elements;
    }

    public void prettyPrint(PrettyPrinter p) {
        p.print(type + " ");
        elements.prettyPrintCommas(p);
    }

	@Override
	public StmtSemantics checkSemantics(
			MajorScope majorScope, MinorScope minorScope,
			SymbolTable table, ArrayList<String> errors) {
		for (DeclarationPart element : elements) {
			element.checkSemantics(errors);
			if (!table.addEntry(element.getName(), element.toSymbol(type)))
				errors.add(element.createError(103, "The variable has already been declared in the current scope."));
		}
		return new StmtSemantics(false);
	}

}
