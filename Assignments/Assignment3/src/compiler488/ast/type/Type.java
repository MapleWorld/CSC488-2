package compiler488.ast.type;

import compiler488.ast.BaseAST;

/**
 * A placeholder for types.
 */
public abstract class Type extends BaseAST {
	public Type(int line, int column) {
		super(line, column);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && getClass().equals(obj.getClass());
	}
}
