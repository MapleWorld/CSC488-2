package compiler488.symbol;

import compiler488.ast.type.Type;

public abstract class Symbol {
	private Symbol() {
	}

	public static abstract class Declared extends Symbol {
		private Type type;
		
		public Declared(Type type) {
			this.type = type;
		}

		public Type getType() {
			return type;
		}
	}

	public static final class Scalar extends Declared {
		private boolean parameter;
		public Scalar(Type type, boolean parameter) {
			super(type);
			this.parameter = parameter;
		}
		
		public boolean isParameter() {
			return parameter;
		}
	}

	public static final class Array1D extends Declared {
		public Array1D(Type type) {
			super(type);
		}
	}

	public static final class Array2D extends Declared {
		public Array2D(Type type) {
			super(type);
		}
	}

	public static final class Function extends Symbol {
		private Type output;
		private Iterable<Type> parameters;
		
		public Function(Type output, Iterable<Type> parameters) {
			this.output = output;
			this.parameters = parameters;
		}
		
		public Type getOutput() {
			return output;
		}
		
		public Iterable<Type> getParameters() {
			return parameters;
		}
	}

	public static final class Procedure extends Symbol {
		private Iterable<Type> parameters;

		public Procedure(Iterable<Type> parameters) {
			this.parameters = parameters;
		}
		
		public Iterable<Type> getParameters() {
			return parameters;
		}
	}
}
