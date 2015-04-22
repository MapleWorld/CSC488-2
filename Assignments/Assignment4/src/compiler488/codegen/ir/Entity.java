package compiler488.codegen.ir;

interface Entity {
	Address getAddress();
	void setAddress(Address s);
	int getSize();
	static class PlainEntity implements Entity {
		int size;
		Address addr;
		public PlainEntity(int size_) { size=size_;addr=null; }
		public Address getAddress() { return addr; }
		public void setAddress(Address s) { addr = s; }
		public int getSize() { return size; }
	}
	static class ScalarEntity extends PlainEntity {
		public ScalarEntity(){super(1);}
	}
	static class Array1DEntity extends PlainEntity {
		int lower, upper;
		public Array1DEntity(int lower_, int upper_) {
			super(upper_-lower_);
			lower=lower_; upper=upper_;
		}
	}
	static class Array2DEntity extends PlainEntity {
		int lower, upper;
		int lower2, upper2;
		public Array2DEntity(int lower_, int upper_, int lower2_, int upper2_) {
			super((upper_-lower_)*(upper2_-lower2_));
			lower=lower_; upper=upper_; lower2=lower2_; upper2=upper2_;
		}
	}

	static class RoutineEntity implements Entity {
		int parametercount;
		Cell address;
		public RoutineEntity(int paramcount, Cell addr) {
			parametercount = paramcount;
			address = addr;
		}
		public void setAddress(Address s) { /*TODO throw exception*/ }
		public Address getAddress() {
			return new Address.PlainAddress(address);
		}
		/* function does not need space to stay */
		public int getSize() {return 0;}
	}
	static class FunctionEntity extends RoutineEntity {
		public FunctionEntity(int paramcount, Cell addr) { super(paramcount, addr); }
	}
	static class ProcedureEntity extends RoutineEntity {
		public ProcedureEntity(int paramcount, Cell addr) { super(paramcount, addr); }
	}
}