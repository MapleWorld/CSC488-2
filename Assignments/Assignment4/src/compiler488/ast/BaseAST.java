package compiler488.ast;

/**
 * Base class implementation for the AST hierarchy.
 *
 * This is a convenient place to add common behaviours.
 *
 * @author Dave Wortman, Marsha Chechik, Danny House, Peter McCormick
 */
public abstract class BaseAST implements AST {
	private int line;
	private int column;

	public BaseAST(int line, int column) {
		this.line = line + 1; // CUP's line numbers are zero based.
		this.column = column;
	}

	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}

	public String createError(int number, String contents) {
		return "E" + number + " (" + line + ", " + column + "): " + contents;
	}
	
    /**
     * A default pretty-printer implementation that uses <code>toString</code>.
     *
     * @param p the printer to use
     */
    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print(toString());
    }
}
