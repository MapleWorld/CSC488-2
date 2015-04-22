package compiler488.semantics;

import compiler488.ast.type.Type;

public abstract class MajorScope {
	private MajorScope() {
	}
	
	public abstract boolean equals(Object obj);

	public static final class Top extends MajorScope {
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Top;
		}
	}

	public static final class Procedure extends MajorScope {
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Procedure;
		}
	}
	
	public static final class Function extends MajorScope {
		private Type output;

		public Function(Type output) {
			this.output = output;
		}

		public Type getOutput() {
			return output;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Function && ((Function)obj).output.equals(output);
		}
	}
}
