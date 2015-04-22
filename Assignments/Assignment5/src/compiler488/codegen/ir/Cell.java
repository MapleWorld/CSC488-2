package compiler488.codegen.ir;
import compiler488.runtime.Machine;

class Cell {

	/* cell type table */
	public enum CellType {
		CELL_OPCODE,  // this cell contains a opcode
		CELL_SCALAR,  // this cell contains a scalar value
		CELL_LABEL,   // a scalar that will be filled later
		CELL_ANCHOR,  // [PSEUDO] fill a scalar with current location
		CELL_COMMENT, // [PSEUDO] a comment line in the generated ir
		CELL_UNDEFINED
	};

	/* opcode name table */
	public final static String opcode_names[] = {
		"HALT"  , "ADDR"  , "LOAD"  , "STORE" , "PUSH"  ,
		"PUSHMT", "SETD"  , "POP"   , "POPN"  , "DUP"   ,
		"DUPN"  , "BR"    , "BF"    , "NEG"   , "ADD"   ,
		"SUB"   , "MUL"   , "DIV"   , "EQ"    , "LT"    ,
		"OR"    , "SWAP"  , "READC" , "PRINTC", "READI" ,
		"PRINTI", "TRON"  , "TROFF" , "ILIMIT"
	};

	// constructor
	private Cell(CellType type_, Object param_) {
		type = type_; param = param_; hint = null;
	}

	private CellType type; // the type of this cell
	private Object param; // parameter to this cell
	private Object hint; // hint for assembly dump

	/* turn this cell in to a scalar value */
	Short getScalar() {
		if(param instanceof Integer) {
			return (short)(int)(Integer)param;
		} else if (param instanceof Boolean) {
			if((Boolean)param)
				return Machine.MACHINE_TRUE;
			else
				return Machine.MACHINE_FALSE;
		} else {
			return null;
		}
	}

	/* getters, TODO parameter checking */
	CellType getType() { return type; }
	Object getComment() { return param; }
	Object getHint() { return hint; }
	Cell getLabel() { return (Cell)param; }


	/* a simple to string function */
	public String toString() {
		switch(type) {
		case CELL_OPCODE:
			return opcode_names[(Integer)param];
		case CELL_SCALAR:
			return ""+getScalar();
		default:
			return "Cell("+type+","+param+")";
		}
	}

	/* anchor the label at current position */
	void applyAnchor(int where) { ((Cell)param).param = where ; }

	/* assign a value to the cell
	 * TODO: check if this is allowed
	 */
	void assign(int value) { param = new Integer(value); }
	void assign(boolean value) { param = new Boolean(value); }

	/* set the hint when dump as assembly */
	Cell dumphint(Object obj) { hint = obj; return this; }

	/* return if the cell is not pseudo instruction */
	boolean isProgramText() {
		switch(type) {
		case CELL_OPCODE:return true;
		case CELL_SCALAR:return true;
		case CELL_LABEL:return true;
		default:return false;
		}
	}

	/* create cells, depend on type */
	static Cell opcode(int i) {
		return new Cell(CellType.CELL_OPCODE, new Integer(i));
	}
	static Cell scalar(int i) {
		return new Cell(CellType.CELL_SCALAR, new Integer(i));
	}
	static Cell scalar(boolean i) {
		return new Cell(CellType.CELL_SCALAR, new Boolean(i));
	}
	static Cell undefined() {
		return new Cell(CellType.CELL_SCALAR, new Integer(Machine.UNDEFINED));
	}
	static Cell scalar() {
		return new Cell(CellType.CELL_SCALAR, null);
	}
	static Cell label() {
		return new Cell(CellType.CELL_LABEL, null);
	}
	static Cell anchor(Cell which) {
		return new Cell(CellType.CELL_ANCHOR, which);
	}
	static Cell comment(Object obj) {
		return new Cell(CellType.CELL_COMMENT, obj);
	}
};
