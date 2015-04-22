package compiler488.ast.type;


/**
 * Used to declare objects that yield integers.
 */
public class IntegerType extends Type {
	public IntegerType(int line, int column) {
		super(line, column);
	}
	
	@Override
    public String toString() {
        return "integer";
    }
}
