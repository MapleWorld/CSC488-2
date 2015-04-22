package compiler488.semantics;

import compiler488.ast.type.*;

public final class ExpnSemantics {
	private boolean returning;
	private Type type;

	public ExpnSemantics(boolean returning, Type type) {
		this.returning = returning;
		this.type = type;
	}
	
	public boolean isReturning() {
		return returning;
	}
	
	public Type getType() {
		return type;
	}
}
