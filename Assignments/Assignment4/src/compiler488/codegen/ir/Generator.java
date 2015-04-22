package compiler488.codegen.ir;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import compiler488.ast.decl.*;
import compiler488.ast.expn.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.*;
import compiler488.ast.*;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.runtime.ExecutionException;
public class Generator {
	ArrayList<Snippet> snippets;
	Cell entry;

	public Generator() { snippets = new ArrayList<Snippet>(); }

	Snippet alloc_snippet() {
		Snippet snippet = new Snippet();
		snippets.add(snippet);
		return snippet;
	}

	class View {
		View(LexScope scope_, Snippet snippet) {
			scope = scope_;
			s = snippet;
			result_offset = 0;
			exit_label = null;
		}
		LexScope scope;
		Snippet s;
		int result_offset;
		Cell exit_label;
	};

	public void compile_ir(Program program) {
		emit_program(program);
	}

	public void commit_binary() throws MemoryAddressException, ExecutionException {
		Snippet code = new Snippet();
		for(Snippet snippet: snippets)
			code.cells.addAll(snippet.cells);
		code.expand_cells();
		int codesize  = code.getSize();
		int codestart = Machine.memorySize - codesize;
		// write the code to the top of the memory
		code.fill_anchor(codestart).write_to_machine(codestart);

		// extract the entry label and set as the start of pc
		Machine.setPC(entry.getScalar());
		Machine.setMSP((short)1); // start of the stack
		Machine.setMLP((short)codestart);
	}

	void found_null_ASTElement() {
		// TODO
		throw new NullPointerException();
	}
	void not_implemented() {
		// TODO
		throw new NullPointerException();
	}

	void emit_pretty_dumphint(View v, PrettyPrintable pp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(stream);
		BasePrettyPrinter bpp = new BasePrettyPrinter(ps);
		pp.prettyPrint(bpp);
		try {
			String content = stream.toString("utf-8");
			if(content.length() > 60)
				content = content.substring(0,60) + "...";
			content = content.replace('\n', ' ');
			v.s.dumphint(content);
		} catch (UnsupportedEncodingException e) {
			v.s.dumphint("encoding error");
		}
	}

	// -- < Dispatchers > -------------------------------------------------
	// Call the correct procedure to generate the code

	// void emit_program    (View v, Program program);

	// void emit_statement  (View v, Stmt stmt);
	// void emit_expression (View v, Expn expn);
	// void emit_variable   (View v, Expn expn);

	void emit_program(Program program) { emit_Program(null, program); }

	void emit_statement  (View v, Stmt stmt) {
		if(stmt instanceof Declaration)
			v.s.dumphint("Declaration");
		emit_pretty_dumphint(v, stmt);
		if(stmt == null) {
			found_null_ASTElement();
		} else if(stmt instanceof Declaration) {
			declaration(v, (Declaration)stmt);
		} else if(stmt instanceof AssignStmt) {
			emit_AssignStmt(v, (AssignStmt)stmt);
		} else if(stmt instanceof ExitStmt) {
			emit_ExitStmt(v, (ExitStmt)stmt);
		} else if(stmt instanceof GetStmt) {
			emit_GetStmt(v, (GetStmt)stmt);
		} else if(stmt instanceof IfStmt) {
			emit_IfStmt(v, (IfStmt)stmt);
		} else if(stmt instanceof LoopingStmt) {
			emit_LoopingStmt(v, (LoopingStmt)stmt);
		} else if(stmt instanceof ProcedureCallStmt) {
			emit_ProcedureCallStmt(v, (ProcedureCallStmt)stmt);
		} else if(stmt instanceof PutStmt) {
			emit_PutStmt(v, (PutStmt)stmt);
		} else if(stmt instanceof ReturnStmt) {
			emit_ReturnStmt(v, (ReturnStmt)stmt);
		} else if(stmt instanceof Scope) {
			emit_Scope(v, (Scope)stmt);
		} else {
			not_implemented();
		}
	}

	void emit_expression (View v, Expn expn) {
		emit_pretty_dumphint(v, expn);
		if(expn == null) {
			found_null_ASTElement();
		} else if(expn instanceof SubsExpn) {
			emit_SubsExpn(v, (SubsExpn)expn);
		} else if(expn instanceof IdentExpn) {
			emit_IdentExpn(v, (IdentExpn)expn);
		} else if(expn instanceof ConstExpn) {
			emit_ConstExpn(v, (ConstExpn)expn);
		} else if(expn instanceof UnaryExpn) {
			emit_UnaryExpn(v, (UnaryExpn)expn);
		} else if(expn instanceof BinaryExpn) {
			emit_BinaryExpn(v, (BinaryExpn)expn);
		} else if(expn instanceof FunctionCallExpn) {
			emit_FunctionCallExpn(v, (FunctionCallExpn)expn);
		} else if(expn instanceof AnonFuncExpn) {
			emit_AnonFuncExpn(v, (AnonFuncExpn)expn);
		} else {
			not_implemented();
		}
	}

	void emit_variable   (View v, Expn expn) {
		if(expn == null) {
			found_null_ASTElement();
		} else if(expn instanceof SubsExpn) {
			emit_addr_SubsExpn(v, (SubsExpn)expn);
		} else if(expn instanceof IdentExpn) {
			emit_addr_IdentExpn(v, (IdentExpn)expn);
		} else {
			not_implemented();
		}
	}

	void emit_statements (View v, ASTList<Stmt> stmts) {
		for(Stmt stmt: stmts) {
			emit_statement(v, stmt);
		}
	}
	void declaration(View v, Declaration decl) {
		if(decl == null) {
			found_null_ASTElement();
		} else if(decl instanceof ScalarDecl) {
			decl_Scalar(v, (ScalarDecl)decl);
		} else if(decl instanceof MultiDeclarations) {
			decl_Multi(v, (MultiDeclarations)decl);
		} else if(decl instanceof RoutineDecl) {
			decl_Routine(v, (RoutineDecl)decl);
		} else {
			not_implemented();
		}
	}


	// -- < Declarations > ------------------------------------------------
	// They modify the the scopes rule and symbol table

	// void decl_Array  (View v, ArrayDeclPart decl, Type type);
	// void decl_Scalar (View v, ScalarDeclPart decl, Type type);
	// void decl_Scalar (View v, ScalarDecl decl);
	// void decl_Multi  (View v, MultiDeclarations decl);
	// void decl_Routine(View v, RoutineDecl decl);

	void decl_Array(View v, ArrayDeclPart decl, Type type) {
		String name = decl.getName();
		if(decl.getDimension() == 2) {
			v.scope.addLocal(name, new Entity.Array2DEntity(
				decl.getLowerBoundary1(), decl.getUpperBoundary1(),
				decl.getLowerBoundary2(), decl.getUpperBoundary2()));
		} else {
			v.scope.addLocal(name, new Entity.Array1DEntity(
				decl.getLowerBoundary1(), decl.getUpperBoundary1()));
		}
	}

	void decl_Scalar (View v, ScalarDeclPart decl, Type type) {
		// type is useless since all scalar have size 1
		String name = decl.getName();
		v.scope.addLocal(name, new Entity.ScalarEntity());
	}

	void decl_Scalar(View v, ScalarDecl decl) {
		// getType() actually we don't need type
		// since all type have size 1
		String name = decl.getName();
		v.scope.addLocal(name, new Entity.ScalarEntity());
	}

	void decl_Multi  (View v, MultiDeclarations decl) {
		Type type = decl.getType();
		for(DeclarationPart part: decl.getParts()) {
			if(part == null) {
				found_null_ASTElement();
			} else if(part instanceof ScalarDeclPart) {
				decl_Scalar(v, (ScalarDeclPart)part, type);
			} else if(part instanceof ArrayDeclPart) {
				decl_Array(v, (ArrayDeclPart)part, type);
			} else {
				not_implemented();
			}
		}
	}

	void decl_Parameter(View v, ScalarDecl decl, int offset) {
		// getType() actually we don't need type
		// since all type have size 1

		String name = decl.getName();
		Entity entity = new Entity.ScalarEntity();
		Cell lexlevel = Cell.scalar(v.scope.getLexLevel());
		entity.setAddress(new Address.StackAddress(
			lexlevel, Cell.scalar(offset)));
		v.scope.addRefer(name, entity);
	}

	void decl_Routine(View v, RoutineDecl decl) {
		// TODO double check v and fv

		// get all the parts of routine declaration
		String name = decl.getName();
		Type type = decl.getType();
		ASTList<ScalarDecl> parameters = decl.getParameters();
		Scope body = decl.getBody();

		// create a major scope
		LexScope.MajorScope scope = new LexScope.MajorScope(v.scope);
		// create a new view for the routine
		View fv = new View(scope, alloc_snippet());

		// attach the parameters to the scope
		int parametercount = parameters.size();
		int parameteridx = 0;
		for(ScalarDecl parameter:parameters) {
			int parameteroffset = parametercount - parameteridx;
			decl_Parameter(fv, parameter, -2-parameteroffset);
			++parameteridx;
		}
		// emit code for the body
		Cell fxaddr = Cell.label();

		// we put the function to symbol table before we try to
		// create the function body

		// add the routine to symbol table
		Entity entity = null;
		if(type != null) {
			fxaddr.dumphint("Function:"+name);
			entity = new Entity.FunctionEntity(parametercount, fxaddr);
		} else {
			fxaddr.dumphint("Procedure:"+name);
			entity = new Entity.ProcedureEntity(parametercount, fxaddr);
		}
		v.scope.addRefer(name, entity);

		// put the result location if there is one
		// procedure will never set result anyway
		fv.result_offset = -3 - parametercount;
		fv.s.anchor(fxaddr);

		Cell lexlevel = Cell.scalar(fv.scope.getLexLevel());
		Cell storage = Cell.scalar();
		fv.s.emit_ENTER(lexlevel, storage);

		fv.s.dumphint("StartBody");
		emit_statements(fv, body.getBody());

		fv.s.dumphint("Returning");
		// FIXME, if it is function, jump to error output
		fv.s.emit_LEAVE(lexlevel);
		fv.s.emit_RETURN();
		fv.s.dumphint("RoutineEnd");

		// fix the storage value required by this scope
		storage.assign(scope.getStackSize());
		// also fix the offsets used in loading variables
		scope.fixOffset();
	}

	// -- < Variables > ---------------------------------------------------
	// emit code to push the correct address to the stack

	// void emit_addr_SubsExpn (View v, SubsExpn expn);
	// void emit_addr_IdentExpn(View v, IdentExpn expn);

	void emit_addr_SubsExpn (View v, SubsExpn expn) {
		String name = expn.getVariable();
		Entity entity = v.scope.resolve(name);
		if(entity == null) {
			System.err.println("Name not found:" + name);
			throw new NullPointerException();
		}
		if(expn.numSubscripts() == 1) {
			Entity.Array1DEntity e = (Entity.Array1DEntity)entity;
			emit_expression(v, expn.getSubscript1());
			if(e.lower != 0) {
				v.s.emit_PUSH(Cell.scalar(-e.lower));
				v.s.emit_ADD();
			}
		} else {
			Entity.Array2DEntity e = (Entity.Array2DEntity)entity;
			emit_expression(v, expn.getSubscript1());
			if(e.lower != 0) {
				v.s.emit_PUSH(Cell.scalar(-e.lower));
				v.s.emit_ADD();
			}
			v.s.emit_PUSH(Cell.scalar(e.upper2-e.lower2));
			v.s.emit_MUL();
			emit_expression(v, expn.getSubscript2());
			if(e.lower2 != 0) {
				v.s.emit_PUSH(Cell.scalar(-e.lower2));
				v.s.emit_ADD();
			}
			v.s.emit_ADD();
		}
		entity.getAddress().emit(v.s);
		v.s.emit_ADD();
	}

	void emit_addr_IdentExpn(View v, IdentExpn expn) {
		String ident  = expn.getIdent();
		Entity entity = v.scope.resolve(ident);
		if(entity == null) {
			System.err.println("Name not found:" + ident);
			throw new NullPointerException();
		}
		entity.getAddress().emit(v.s);
	}

	// -- < Expressions > -------------------------------------------------
	// emit code to push the result of a expression to the stack

	// void emit_SubsExpn        (View v, SubsExpn expn);
	// void emit_IdentExpn       (View v, IdentExpn expn);
	// void emit_ConstExpn       (View v, ConstExpn expn);
	// void emit_UnaryExpn       (View v, UnaryExpn expn);
	// void emit_BinaryExpn      (View v, BinaryExpn expn);
	// void emit_FunctionCallExpn(View v, FunctionCallExpn expn);
	// void emit_AnonFuncExpn    (View v, AnonFuncExpn expn);

	void emit_SubsExpn(View v, SubsExpn expn) {
		String name = expn.getVariable();
		v.s.dumphint("Load array element of: "+name);
		emit_addr_SubsExpn(v, expn);
		v.s.emit_LOAD();
	}
	void emit_IdentExpn_fcall(View v, IdentExpn expn) {
		String ident  = expn.getIdent();
		Entity.FunctionEntity fx = (Entity.FunctionEntity)v.scope.resolve(ident);
		// result holder
		v.s.emit_PUSH(Cell.undefined());
		v.s.emit_CALL(fx.address, Cell.scalar(fx.parametercount));
	}
	void emit_IdentExpn(View v, IdentExpn expn) {
		String ident  = expn.getIdent();
		Entity entity = v.scope.resolve(ident);
		if(entity instanceof Entity.ScalarEntity) {
			v.s.dumphint("Load variable: "+ident);
			emit_addr_IdentExpn(v, expn);
			v.s.emit_LOAD();
		} else {
			emit_IdentExpn_fcall(v, expn);
		}
	}
	void emit_ConstExpn(View v, ConstExpn expn) {
		if(expn instanceof IntConstExpn) {
			v.s.emit_PUSH(Cell.scalar(((IntConstExpn)expn).getValue()));
		} else {
			v.s.emit_PUSH(Cell.scalar(((BoolConstExpn)expn).getValue()));
		}
	}
	void emit_UnaryExpn       (View v, UnaryExpn expn) {
		Expn right = expn.getOperand();
		String op = expn.getOpSymbol();
		emit_expression(v, right);
		switch(op) {
		case "!":v.s.emit_NOT();break;
		case "-":v.s.emit_NEG();break;
		default:not_implemented();
		}
	}
	void emit_BinaryExpn      (View v, BinaryExpn expn) {
		Expn left = expn.getLeft();
		Expn right = expn.getRight();
		String op = expn.getOpSymbol();
		Cell l_else = Cell.label();
		Cell l_end = Cell.label();
		emit_expression(v, left);
		// if we are not short circuit some thing
		if(! op.equals("&") && !op.equals("|"))
			emit_expression(v, right);
		switch(op) {
		case "+" :v.s.emit_ADD();break;
		case "-" :v.s.emit_SUB();break;
		case "*" :v.s.emit_MUL();break;
		case "/" :v.s.emit_DIV();break;
		case "<" :v.s.emit_LT();break;
		case "<=":v.s.emit_SWAP().emit_LT().emit_NOT();break;
		case ">" :v.s.emit_SWAP().emit_LT();break;
		case ">=":v.s.emit_LT().emit_NOT();break;
		case "=" :v.s.emit_EQ();break;
		case "!=":v.s.emit_EQ().emit_NOT();break;
		// short circuit ?
		case "&":
			v.s.emit_BF(l_else);
			emit_expression(v, right);
			v.s.emit_BR(l_end);
			v.s.anchor(l_else);
			v.s.emit_PUSH(Cell.scalar(false));
			v.s.anchor(l_end);
			l_else.dumphint("AND_SHORTCIRCUIT");
			l_end.dumphint("AND_FINISHED");
			break;
		case "|":
			v.s.emit_BF(l_else);
			v.s.emit_PUSH(Cell.scalar(true));
			v.s.emit_BR(l_end);
			v.s.anchor(l_else);
			emit_expression(v, right);
			v.s.anchor(l_end);
			l_else.dumphint("OR_NORMAL");
			l_end.dumphint("OR_FINISHED");
			break;
		default:not_implemented();
		}
	}
	void emit_FunctionCallExpn(View v, FunctionCallExpn expn) {
		String ident  = expn.getIdent();
		ASTList<Expn> args = expn.getArguments();
		Entity.FunctionEntity fx = (Entity.FunctionEntity)v.scope.resolve(ident);
		// result holder
		v.s.emit_PUSH(Cell.undefined());
		for(Expn argx:args)
			emit_expression(v, argx);
		v.s.emit_CALL(fx.address, Cell.scalar(fx.parametercount));
	}
	void emit_AnonFuncExpn    (View v, AnonFuncExpn expn) {
		ASTList<Stmt> body = expn.getBody();
		Expn yieldsexpn = expn.getExpn();

		// create a major scope
		LexScope.MinorScope scope = new LexScope.MinorScope(v.scope);
		// embed the minor scope in to the major scope outside
		v.scope.embed(scope);
		// create a new view for the routine
		// although we directly jump to the start of the function
		// so we don't need another snippet
		View fv = new View(scope, v.s);

		v.s.dumphint("anon function start");
		// note that this is not actually a function, so we can not return
		emit_statements(fv, body);
		emit_expression(fv, yieldsexpn);
		v.s.dumphint("anon function end");
	}

	// -- < Statements > --------------------------------------------------
	// emit code to execute some statement

	// void emit_AssignStmt       (View v, AssignStmt scope);
	// void emit_ExitStmt         (View v, ExitStmt stmt);
	// void emit_GetStmt          (View v, GetStmt stmt);
	// void emit_IfStmt           (View v, IfStmt stmt);
	// void emit_LoopingStmt      (View v, LoopingStmt stmt);
	// void emit_ProcedureCallStmt(View v, ProcedureCallStmt stmt);
	// void emit_Program          (View v, Program program);
	// void emit_PutStmt          (View v, PutStmt stmt);
	// void emit_ReturnStmt       (View v, ReturnStmt stmt);
	// void emit_Scope            (View v, Scope scope);


	void emit_AssignStmt       (View v, AssignStmt stmt) {
		emit_variable(v, stmt.getLval());
		emit_expression(v, stmt.getRval());
		v.s.emit_STORE();
	}
	void emit_ExitStmt         (View v, ExitStmt stmt){
		Expn cond = stmt.getExpn();
		if(cond == null) {
			v.s.emit_BR(v.exit_label);
		} else {
			emit_expression(v, cond);
			v.s.emit_BT(v.exit_label);
		}
	}
	void emit_GetStmt(View v, GetStmt stmt){
		ASTList<Expn> vars = stmt.getInputs();
		for(Expn var: vars) {
			emit_variable(v, var);
			v.s.emit_READI();
			v.s.emit_STORE();
		}
	}
	void emit_IfStmt(View v, IfStmt stmt) {
		Expn cond = stmt.getCondition();
		ASTList<Stmt> whenTrue = stmt.getWhenTrue();
		ASTList<Stmt> whenFalse = stmt.getWhenFalse();
		Cell l_else = Cell.label();
		Cell l_end  = Cell.label();
		l_else.dumphint("ELSE");
		l_end.dumphint("ENDIF");
		emit_expression(v, cond);
		if(whenFalse == null) {
			v.s.emit_BF(l_end);
			emit_statements(v, whenTrue);
			// we don't need branch here
		} else {
			v.s.emit_BF(l_else);
			emit_statements(v, whenTrue);
			v.s.emit_BR(l_end);
			v.s.anchor(l_else);
			emit_statements(v, whenFalse);
		}
		v.s.anchor(l_end);
	}

	void emit_LoopingStmt(View v, LoopingStmt stmt) {
		Expn cond = stmt.getExpn();
		ASTList<Stmt> body = stmt.getBody();

		Cell start_label = Cell.label();
		Cell exit_label = Cell.label();

		start_label.dumphint("LOOP_BEGIN");
		exit_label.dumphint("LOOP_END");

		Cell outer_exit_label = v.exit_label;
		v.exit_label = exit_label;

		v.s.anchor(start_label);
		if(cond != null) {
			emit_expression(v, cond);
			v.s.emit_BF(exit_label);
		}
		emit_statements(v, body);
		v.s.emit_BR(start_label);
		v.s.anchor(exit_label);
		v.exit_label = outer_exit_label;
	}

	void emit_ProcedureCallStmt(View v, ProcedureCallStmt stmt){
		String ident = stmt.getName();
		ASTList<Expn> args = stmt.getArguments();
		Entity.ProcedureEntity fx = (Entity.ProcedureEntity)v.scope.resolve(ident);
		for(Expn argx:args)
			emit_expression(v, argx);
		v.s.emit_CALL(fx.address, Cell.scalar(fx.parametercount));
	}

	void emit_Program(View v, Program stmt){
		LexScope.MajorScope scope = new LexScope.MajorScope(null);
		v = new View(scope, alloc_snippet());

		// set a assembly dump hint
		v.s.dumphint("ProgramStart");

		// set program start point
		entry = Cell.label();
		v.s.anchor(entry);

		// maybe enable trace
		// v.s.emit(Cell.scalar(26));

		// setup entering code
		Cell storage = Cell.scalar();
		v.s.emit_PUSHMT().emit_SETD(Cell.scalar(0)).emit_RESERVE(storage);

		// the program body
		emit_statements(v, stmt.getBody());

		// end of the program
		v.s.emit_HALT();
		v.s.dumphint("ProgramEnd");

		// fix the storage value required by this scope
		storage.assign(scope.getStackSize());
		// also fix the offsets used in loading variables
		scope.fixOffset();
	}

	void emit_PutStmt(View v, PutStmt stmt) {
		ASTList<Printable> outputs = stmt.getOutputs();
		for(Printable x: outputs) {
			if(x == null) {
				found_null_ASTElement();
			} else if(x instanceof TextConstExpn) {
				// this need to go first since we do not consider
				// text to be expression -_-
				for(char c:((TextConstExpn)x).getValue().toCharArray()) {
					v.s.emit_PUSH(Cell.scalar(c));
					v.s.emit_PRINTC();
				}
			} else if(x instanceof SkipConstExpn) {
				// same thing for skip
				v.s.emit_PUSH(Cell.scalar('\n'));
				v.s.emit_PRINTC();
			} else if(x instanceof Expn) {
				emit_expression(v, (Expn)x);
				v.s.emit_PRINTI();
			} else {
				not_implemented();
			}
		}
	}

	void emit_ReturnStmt       (View v, ReturnStmt stmt){
		Expn expn = stmt.getValue();
		Cell lexlevel = Cell.scalar(v.scope.getLexLevel());
		if(expn != null) {
			emit_expression(v, expn);
			v.s.emit_RESULT(lexlevel, Cell.scalar(v.result_offset));
		}
		v.s.emit_LEAVE(lexlevel);
		v.s.emit_RETURN();
	}

	void emit_Scope(View v, Scope stmt){
		// create a major scope
		LexScope.MinorScope scope = new LexScope.MinorScope(v.scope);
		// embed the minor scope in to the major scope outside
		v.scope.embed(scope);
		// create a new view for the routine
		// although we directly jump to the start of the function
		// so we don't need another snippet
		View sv = new View(scope, v.s);

		v.s.dumphint("minor scope start");
		// note that this is not actually a function, so we can not return
		emit_statements(sv, stmt.getBody());
		v.s.dumphint("minor scope end");
	}
};