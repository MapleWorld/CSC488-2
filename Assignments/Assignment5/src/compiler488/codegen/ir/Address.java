package compiler488.codegen.ir;
import java.util.ArrayList;

/* the addressing mode of object */
interface Address {
	/* emit instruction to push the address on to the stack */
	void emit(Snippet s);

	/* a address that is on stack */
	public class StackAddress implements Address {
		Cell lexlevel;
		int offset;
		ArrayList<Cell> fixes;
		StackAddress(Cell lexlevel_, int offset_) {
			lexlevel = lexlevel_;
			offset = offset_;
			fixes = new ArrayList<Cell>();
		}
		public void emit(Snippet s){
			emit(s, 0);
		}
		public void emit(Snippet s, int offset2) {
			Cell off = Cell.scalar(offset+offset2);
			fixes.add(off);
			s.emit_ADDR(lexlevel, off);
		}
		public void fixOffset(int base) {
			offset += base;
			for(Cell c : fixes)
				c.assign(base+c.getScalar());
		}
	}

	/* a plain address that we can directly push */
	public class PlainAddress implements Address {
		Cell location;
		PlainAddress(Cell location_) {
			location = location_;
		}
		public void emit(Snippet s){
			s.emit_PUSH(location);
		}
	}
};