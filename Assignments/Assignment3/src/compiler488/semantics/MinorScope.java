package compiler488.semantics;

/**
 * Minor scopes. This should probably be implemented as an enumeration,
 * but I'm implementing it as nested classes instead to keep it consistent
 * with the MajorScope class.
 */
public abstract class MinorScope {
	private MinorScope() {
	}

	@Override
	public abstract boolean equals(Object obj);

	public static final class None extends MinorScope {
		@Override
		public boolean equals(Object obj) {
			return obj instanceof None;
		}
	}
	
	public static final class Loop extends MinorScope {
		@Override
		public boolean equals(Object obj) {
			return obj instanceof None;
		}
	}
}
