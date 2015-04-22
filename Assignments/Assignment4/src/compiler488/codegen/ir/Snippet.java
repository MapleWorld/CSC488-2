package compiler488.codegen.ir;

import java.util.ArrayList;
import java.util.HashMap;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.runtime.ExecutionException;
/* A snippet is some fragment of the code */

class Snippet {
	Snippet() {
		cells = new ArrayList<Cell>();
	}

	Snippet(String s) {
		cells = new ArrayList<Cell>();
		for(char c:s.toCharArray()) {
			emit(Cell.scalar(c));
		}
		emit(Cell.scalar(0));
	}

	ArrayList<Cell> cells;

	int getSize() {
		int count = 0;
		for(Cell c: cells) // FIXME
			if(c.isProgramText())
				++count;
		return count;
	}

	Snippet expand_cells() {
		return this;
	}

	Snippet fill_anchor(int startpos) {
		for(Cell c: cells) {
			if(c.getType() == Cell.CellType.CELL_ANCHOR)
				c.applyAnchor(startpos);
			else if(c.isProgramText())
				++startpos;
		}
		return this;
	}
	Snippet dumphint(Object obj) { return emit(Cell.comment(obj)); }

	Snippet outputAssembly() {
		HashMap<Cell, Integer> labelspace = new HashMap<Cell, Integer>();
		for(Cell c: cells) {
			switch(c.getType()) {
			case CELL_COMMENT:
				System.out.print("\n\t{"+c.getComment()+"} ");
				break;
			case CELL_OPCODE:
				System.out.print("\n\t"+c);
				break;
			case CELL_LABEL:
				if(labelspace.get(c) == null)
					labelspace.put(c, labelspace.size());
				System.out.print(" LABEL"+labelspace.get(c));
				if(c.getHint()!=null)
					System.out.print(" {"+c.getHint()+"}");
				break;
			case CELL_ANCHOR:
				if(labelspace.get(c.getLabel()) == null)
					labelspace.put(c.getLabel(), labelspace.size());
				System.out.print("\nLABEL"+labelspace.get(c.getLabel())+":");
				if(c.getLabel().getHint()!=null)
					System.out.print(" {"+c.getLabel().getHint()+"}");
				break;
			case CELL_SCALAR:
				System.out.print(" "+c.getScalar());
				break;
			default:
				System.out.print("?"+c);
			}
		}
		System.out.println("");
		return this;
	}

	Snippet write_to_machine(int startpos)
		throws MemoryAddressException, ExecutionException {
		outputAssembly();
		for(Cell c: cells) {
			Short content = c.getScalar();
			if(content != null) {
				Machine.writeMemory((short)startpos, content);
				++startpos;
			}
		}
		return this;
	}

	Snippet emit(Cell cell) {
		if(cell == null)
			throw new NullPointerException();
		cells.add(cell);
		return this;
	}
	Snippet emit_opcode(int opcode) { return emit(Cell.opcode(opcode)); }

	// anchor
	Snippet anchor(Cell scalar) { return emit(Cell.anchor(scalar)); }

	// -- < Instructions > ------------------------------------------------
	// emit instruction, include macro instruction

	// one word instruction
	Snippet emit_HALT  () { return emit_opcode( 0); }
	Snippet emit_LOAD  () { return emit_opcode( 2); }
	Snippet emit_STORE () { return emit_opcode( 3); }
	Snippet emit_PUSHMT() { return emit_opcode( 5); }
	Snippet emit_POP   () { return emit_opcode( 7); }
	Snippet emit_POPN  () { return emit_opcode( 8); }
	Snippet emit_DUP   () { return emit_opcode( 9); }
	Snippet emit_DUPN  () { return emit_opcode(10); }
	Snippet emit_BR    () { return emit_opcode(11); }
	Snippet emit_BF    () { return emit_opcode(12); }
	Snippet emit_NEG   () { return emit_opcode(13); }
	Snippet emit_ADD   () { return emit_opcode(14); }
	Snippet emit_SUB   () { return emit_opcode(15); }
	Snippet emit_MUL   () { return emit_opcode(16); }
	Snippet emit_DIV   () { return emit_opcode(17); }
	Snippet emit_EQ    () { return emit_opcode(18); }
	Snippet emit_LT    () { return emit_opcode(19); }
	Snippet emit_OR    () { return emit_opcode(20); }
	Snippet emit_SWAP  () { return emit_opcode(21); }
	Snippet emit_READC () { return emit_opcode(22); }
	Snippet emit_PRINTC() { return emit_opcode(23); }
	Snippet emit_READI () { return emit_opcode(24); }
	Snippet emit_PRINTI() { return emit_opcode(25); }
	// two word instruction
	Snippet emit_PUSH(Cell literal) {
		return emit_opcode(4).emit(literal);
	}
	Snippet emit_SETD(Cell lexlevel) {
		return emit_opcode(6).emit(lexlevel);
	}
	// three word instruction
	Snippet emit_ADDR(Cell lexlevel, Cell offset) {
		return emit_opcode(1).emit(lexlevel).emit(offset);
	}
	// macro instruction set
	Snippet emit_BR(Cell where) { return emit_PUSH(where).emit_BR(); }
	Snippet emit_BF(Cell where) { return emit_PUSH(where).emit_BF(); }
	Snippet emit_BT(Cell where) { return emit_NOT().emit_BF(where); }
	Snippet emit_POPN(Cell n) { return emit_PUSH(n).emit_POPN(); }
	Snippet emit_DUPN(Cell n) { return emit_PUSH(n).emit_DUPN(); }
	Snippet emit_RESERVE(Cell n) {
		return emit_PUSH(Cell.undefined()).emit_DUPN(n);
	}
	Snippet emit_NOT() {
		return emit_PUSH(Cell.scalar(false)).emit_EQ();
	}
	Snippet emit_ENTER(Cell lexlevel, Cell storage) {
		// save the display
		emit_ADDR(lexlevel, Cell.scalar(0));
		// set stack base pointer (display for current scope)
		emit_PUSHMT();
		emit_SETD(lexlevel);
		emit_RESERVE(storage);
		return this;
	}

	Snippet emit_RESULT(Cell lexlevel, Cell resultoffset) {
		emit_ADDR(lexlevel, resultoffset);
		emit_SWAP();
		emit_STORE();
		return this;
	}

	Snippet emit_LEAVE(Cell lexlevel) {
		// clear stack
		emit_PUSHMT();
		emit_ADDR(lexlevel, Cell.scalar(0));
		emit_SUB();
		emit_POPN();
		// reset view
		emit_SETD(lexlevel);
		return this;
	}

	Snippet emit_RETURN() {
		// return
		emit_BR();
		return this;
	}

	Snippet emit_CALL(Cell fxaddr, Cell nparams) {
		Cell l_return = Cell.label();
		l_return.dumphint("RETURNADDR");
		// set return address
		emit_PUSH(l_return);
		// call routine
		emit_BR(fxaddr);
		anchor(l_return);
		emit_POPN(nparams);
		// cleanup the parameter
		return this;
	}
};
