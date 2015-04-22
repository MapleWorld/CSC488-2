package compiler488.ast.decl;

import java.util.*;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.type.*;
import compiler488.symbol.*;

/**
 * Holds the declaration part of an array.
 */
public class ArrayDeclPart extends DeclarationPart {
    /** The lower bound of dimension 1. */
    private Integer lb1;

    /** The upper bound of dimension 1. */
    private Integer ub1;

    /** The lower bound of dimension 2 (if any.) */
    private Integer lb2 = null;

    /** The upper bound of dimension 2 (if any.) */
    private Integer ub2 = null;

    /** True iff this is an 2D array */
    private Boolean isTwoDimensional = false;

    public ArrayDeclPart(int line, int column, String name, Integer lb1, Integer ub1) {
        super(line, column, name);

        this.lb1 = lb1;
        this.ub1 = ub1;

        this.isTwoDimensional = false;
        this.lb2 = null;
        this.ub2 = null;
    }

    public ArrayDeclPart(int line, int column, String name, Integer lb1, Integer ub1, Integer lb2, Integer ub2) {
        this(line, column, name, lb1, ub1);

        this.isTwoDimensional = true;
        this.lb2 = lb2;
        this.ub2 = ub2;
    }

    /**
     * Returns a string that describes the array.
     */
    @Override
    public String toString() {
        return name + "[" + lb1 + ".." + ub1 +
               ( isTwoDimensional ?  ", " + lb2 + ".." + ub2 : "" )
               + "]";
    }

    /**
     * Calculates the number of values held in an array declared in this way.
     *
     * TODO: Add a correct computation of the size of this array.
     *
     * @return size of the array
     */
    public int getSize() {
        // FIXME: This is broken.
        throw new UnsupportedOperationException();
    }

    public Integer getLowerBoundary1() {
        return lb1;
    }

    public Integer getUpperBoundary1() {
        return ub1;
    }

    public Integer getLowerBoundary2() {
        // Confirm correct use
        assert isTwoDimensional;

        return lb2;
    }

    public Integer getUpperBoundary2() {
        // Confirm correct use
        assert isTwoDimensional;

        return ub2;
    }

    public int getDimension() {
        if(isTwoDimensional)
            return 2;
        return 1;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        p.print(name + "[" + lb1 + ".." + ub1);

        if (isTwoDimensional) {
            p.print(", " + lb2 + ".." + ub2);
        }

        p.print("]");
    }

	@Override
	public Symbol toSymbol(Type type) {
		if (isTwoDimensional)
			return new Symbol.Array2D(type);
		else
			return new Symbol.Array1D(type);
	}

	@Override
	public void checkSemantics(ArrayList<String> errors) {
		if (lb1 > ub1 || (isTwoDimensional && lb2 > ub2))
			errors.add(createError(46, "The lower bound must not be greater than the upper bound."));
	}
}
