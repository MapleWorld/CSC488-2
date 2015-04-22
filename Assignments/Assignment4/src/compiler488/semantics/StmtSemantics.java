package compiler488.semantics;

public final class StmtSemantics {
	private boolean returning;

	public StmtSemantics(boolean returning) {
		this.returning = returning;
	}
	
	public boolean isReturning() {
		return returning;
	}
}
