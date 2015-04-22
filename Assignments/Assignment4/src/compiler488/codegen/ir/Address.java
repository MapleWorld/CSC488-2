package compiler488.codegen.ir;

interface Address {
	void emit(Snippet s);
	public class StackAddress implements Address {
		Cell lexlevel;
		Cell offset;
		StackAddress(Cell lexlevel_, Cell offset_) {
			lexlevel = lexlevel_;
			offset = offset_;
		}
		public void emit(Snippet s){
			s.emit_ADDR(lexlevel, offset);
		}
	}
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