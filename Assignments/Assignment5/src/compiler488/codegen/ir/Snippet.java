package compiler488.codegen.ir;

import java.util.ArrayList;
import java.util.HashMap;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.runtime.ExecutionException;
/* A snippet is some fragment of the code */

class Snippet {
	public Snippet() {
		cells = new ArrayList<Cell>();
	}

	// unused, construct a plain text string
	public Snippet(String s) {
		cells = new ArrayList<Cell>();
		for(char c:s.toCharArray()) {
			emit(Cell.scalar(c));
		}
		emit(Cell.scalar(0));
	}

	private ArrayList<Cell> cells;



	// -- < Code Generation > ---------------------------------------------
	// generate codes (machine code / assembly)

	// compute the size of code snippet
	int getSize() {
		int count = 0;
		for(Cell c: cells) // FIXME
			if(c.isProgramText())
				++count;
		return count;
	}

	// apply all the anchors
	public Snippet fillAnchor(int startpos) {
		for(Cell c: cells) {
			if(c.getType() == Cell.CellType.CELL_ANCHOR)
				c.applyAnchor(startpos);
			else if(c.isProgramText())
				++startpos;
		}
		return this;
	}

	// convert the IR into machine instruction
	public Snippet writeMachine(int startpos)
		throws MemoryAddressException, ExecutionException {
		for(Cell c: cells) {
			Short content = c.getScalar();
			if(content != null) {
				Machine.writeMemory((short)startpos, content);
				++startpos;
			}
		}
		return this;
	}

	// assembly dump
	public Snippet outputAssembly() {
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

	// -- < Utilities > ---------------------------------------------------
	// utility functions

	// append another snippet to this one
	public Snippet append(Snippet s) { cells.addAll(s.cells); return this; }

	// anchor some label at this point
	public Snippet anchor(Cell scalar) { return emit(Cell.anchor(scalar)); }

	// add a assembly dump hint
	public Snippet dumphint(Object obj) { return emit(Cell.comment(obj)); }

	// emit somthing
	private Snippet emit(Cell cell) {
		if(cell == null)
			throw new NullPointerException();
		cells.add(cell);
		return this;
	}

	// emit a opcode
	private Snippet emit_opcode(int opcode) {
		return emit(Cell.opcode(opcode));
	}

	// -- < Instructions > ------------------------------------------------
	// emit instruction, include macro instruction

	// one word instruction
	public Snippet emit_HALT  () { return emit_opcode( 0); }
	public Snippet emit_LOAD  () { return emit_opcode( 2); }
	public Snippet emit_STORE () { return emit_opcode( 3); }
	public Snippet emit_PUSHMT() { return emit_opcode( 5); }
	public Snippet emit_POP   () { return emit_opcode( 7); }
	public Snippet emit_POPN  () { return emit_opcode( 8); }
	public Snippet emit_DUP   () { return emit_opcode( 9); }
	public Snippet emit_DUPN  () { return emit_opcode(10); }
	public Snippet emit_BR    () { return emit_opcode(11); }
	public Snippet emit_BF    () { return emit_opcode(12); }
	public Snippet emit_NEG   () { return emit_opcode(13); }
	public Snippet emit_ADD   () { return emit_opcode(14); }
	public Snippet emit_SUB   () { return emit_opcode(15); }
	public Snippet emit_MUL   () { return emit_opcode(16); }
	public Snippet emit_DIV   () { return emit_opcode(17); }
	public Snippet emit_EQ    () { return emit_opcode(18); }
	public Snippet emit_LT    () { return emit_opcode(19); }
	public Snippet emit_OR    () { return emit_opcode(20); }
	public Snippet emit_SWAP  () { return emit_opcode(21); }
	public Snippet emit_READC () { return emit_opcode(22); }
	public Snippet emit_PRINTC() { return emit_opcode(23); }
	public Snippet emit_READI () { return emit_opcode(24); }
	public Snippet emit_PRINTI() { return emit_opcode(25); }
	// two word instruction
	public Snippet emit_PUSH(Cell literal) {
		return emit_opcode(4).emit(literal);
	}
	public Snippet emit_SETD(Cell lexlevel) {
		return emit_opcode(6).emit(lexlevel);
	}
	// three word instruction
	public Snippet emit_ADDR(Cell lexlevel, Cell offset) {
		return emit_opcode(1).emit(lexlevel).emit(offset);
	}
	// macro instruction set
	public Snippet emit_BR(Cell where) { return emit_PUSH(where).emit_BR(); }
	public Snippet emit_BF(Cell where) { return emit_PUSH(where).emit_BF(); }
	public Snippet emit_BT(Cell where) { return emit_NOT().emit_BF(where); }
	public Snippet emit_POPN(Cell n) { return emit_PUSH(n).emit_POPN(); }
	public Snippet emit_DUPN(Cell n) { return emit_PUSH(n).emit_DUPN(); }
	public Snippet emit_RESERVE(Cell n) {
		return emit_PUSH(Cell.undefined()).emit_DUPN(n);
	}
	public Snippet emit_NOT() {
		return emit_PUSH(Cell.scalar(false)).emit_EQ();
	}
	public Snippet emit_ENTER(Cell lexlevel, Cell storage) {
		// save the display
		emit_ADDR(lexlevel, Cell.scalar(0));
		// set stack base pointer (display for current scope)
		emit_PUSHMT();
		emit_SETD(lexlevel);
		emit_RESERVE(storage);
		return this;
	}

	public Snippet emit_RESULT(Cell lexlevel, Cell resultoffset) {
		emit_ADDR(lexlevel, resultoffset);
		emit_SWAP();
		emit_STORE();
		return this;
	}

	public Snippet emit_LEAVE(Cell lexlevel) {
		// clear stack
		emit_PUSHMT();
		emit_ADDR(lexlevel, Cell.scalar(0));
		emit_SUB();
		emit_POPN();
		// reset view
		emit_SETD(lexlevel);
		return this;
	}

	public Snippet emit_RETURN() {
		// return
		emit_BR();
		return this;
	}

	public Snippet emit_CALL(Cell fxaddr, Cell nparams) {
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
