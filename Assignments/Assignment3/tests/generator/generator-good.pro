:- dynamic degree/2.

set_degree(P, N) :-
    retractall(degree(P, _)),
    asserta(degree(P, N)).

decons(Pred, Name, Args, Arity, Degree) :-
    Pred =.. [Name | Args],
    length(Args, Arity),
    degree(Name/Arity, Degree).

rewrite(Pred, Term) :-
    decons(Pred, Name, Args, Arity, I),
    J is I + 1,
    set_degree(Name/Arity, J),
    atom_concat(Name, '!', NewName),
    Term =.. [NewName, I | Args].

choose(Pred) :-
    decons(Pred, Name, Args, _, N),
    atom_concat(Name, '!', NewName),
    randseq(N, N, L),
    member(I, L),
    J is I - 1,
    apply(NewName, [J | Args]).

expand(Pred :- Goal, Term :- Goal) :-
    rewrite(Pred, Term).
expand(Pred, Term) :-
    rewrite(Pred, Term).
expand(nondet(Name/Arity), Term :- choose(Term)) :-
    set_degree(Name/Arity, 0),
    functor(Term, Name, Arity).
expand(nondet(Name/Arity) :- Goal, Term :- (choose(Term), Goal)) :-
    set_degree(Name/Arity, 0),
    functor(Term, Name, Arity).

term_expansion(P, T) :- expand(P, T).

nondet(bool/1).
bool(true).
bool(false).

nondet(or/3).
or(true, true, true).
or(true, false, true).
or(false, true, true).
or(false, false, false).

nondet(and/3).
and(false, false, false).
and(false, true, false).
and(true, false, false).
and(true, true, true).

nondet(expr_type/1) :- !.
expr_type(integer).
expr_type(boolean).

nondet(decl_type/3) :- !.
decl_type(Base, expr, Base) :- expr_type(Base).
decl_type(Base, array1d, array1d(Base)) :- expr_type(Base).
decl_type(Base, array2d, array2d(Base)) :- expr_type(Base).

nondet(output/6) :- !.
output(NLvl, ELvl, Maj, Env, Ret, Out) :-
    expr(NLvl, ELvl, Maj, Ret, Env, integer, Out).
output(_, _, _, _, false, Out) :-
    random_member(Out, ['"hello"', '"world"', '":D"', '"!?"']).
output(_, _, _, _, false, skip).

input(NLvl, ELvl, Maj, Env, Ret, Ref) :-
    Ref = ref(_, _),
    expr(NLvl, ELvl, Maj, Ret, Env, integer, Ref).

ref(Name-Type, Name, Type).
ref(Env, Name, Type) :-
    is_list(Env),
    random_permutation(Env, L),
    member(X, L),
    (is_list(X) ->
        ref(X, Name, Type),
        \+ member(Name-_, L)
    ;
        ref(X, Name, Type)
    ).

newsyms(_, _, []).
newsyms(Env, Prefix, [H | T]) :-
    reset_gensym,
    repeat,
    gensym(Prefix, H),
    maybe,
    \+ member(H-_, Env),
    !,
    newsyms([H-_ | Env], Prefix, T).

list(MinSize, MaxSize, List) :-
    random_between(MinSize, MaxSize, N),
    length(List, N).

rets(Rets, Ret) :-
    maplist(bool, Rets),
    foldl(or, Rets, false, Ret), !.

nlvl(X, Y) :- X < 3, Y is X + 1.
elvl(X, Y) :- X < 4, Y is X + 1.

nondet(stmt/8) :- !.
stmt(_, _, _, _, false, Pre, Post, declare(Base, Vars)) :-
    list(1, 3, Vars),
    pairs_keys_values(Vars, Names, Ctors),
    pairs_keys_values(Intro, Names, Types),
    newsyms(Pre, var, Names),
    maplist(decl_type(Base), Ctors, Types),
    append(Intro, Pre, Post).
stmt(NLvl, ELvl, Maj, _, Ret, Env, Env, assign(Ref, Expr)) :-
    Ref = ref(Name, _),
    or(Ret1, Ret2, Ret),
    expr(NLvl, ELvl, Maj, Ret1, Env, Type, Ref),
    \+ atom_prefix(Name, arg),
    expr(NLvl, ELvl, Maj, Ret2, Env, Type, Expr).
stmt(NLvl, ELvl, Maj, Min, Ret, Pre, Post, if_then(Cond, Body)) :-
    nlvl(NLvl, NLvl1),
    or(Ret1, Ret, Ret),
    expr(NLvl, ELvl, Maj, Ret, Pre, boolean, Cond),
    stmt_list(NLvl1, ELvl, Maj, Min, Ret1, Pre, Post, Body).
stmt(NLvl, ELvl, Maj, Min, Ret, Pre, Post, if_then_else(Cond, T, F)) :-
    nlvl(NLvl, NLvl1),
    or(Ret1, Ret4, Ret),
    and(Ret2, Ret3, Ret4), !,
    expr(NLvl, ELvl, Maj, Ret1, Pre, boolean, Cond),
    stmt_list(NLvl1, ELvl, Maj, Min, Ret2, Pre, Env, T),
    stmt_list(NLvl1, ELvl, Maj, Min, Ret3, Env, Post, F).
stmt(NLvl, ELvl, Maj, _, Ret, Pre, Post, while(Cond, Body)) :-
    nlvl(NLvl, NLvl1),
    or(Ret1, Ret, Ret),
    expr(NLvl, ELvl, Maj, Ret, Pre, boolean, Cond),
    stmt_list(NLvl1, ELvl, Maj, loop, Ret1, Pre, Post, Body).
stmt(NLvl, ELvl, Maj, _, Ret, Pre, Post, loop(Body)) :-
    nlvl(NLvl, NLvl1),
    stmt_list(NLvl1, ELvl, Maj, loop, Ret, Pre, Post, Body).
stmt(_, _, _, loop, false, Env, Env, exit).
stmt(NLvl, ELvl, Maj, loop, Ret, Env, Env, exit_when(Cond)) :-
    expr(NLvl, ELvl, Maj, Ret, Env, boolean, Cond).
stmt(NLvl, ELvl, Maj, _, Ret, Env, Env, put(Outs)) :-
    list(1, 3, Rets),
    rets(Rets, Ret),
    maplist(output(NLvl, ELvl, Maj, Env), Rets, Outs).
stmt(NLvl, ELvl, Maj, _, Ret, Env, Env, get(Refs)) :-
    list(1, 3, Rets),
    rets(Rets, Ret),
    maplist(input(NLvl, ELvl, Maj, Env), Rets, Refs).
stmt(NLvl, ELvl, Maj, _, Ret, Env, Env, call(Name, Args)) :-
    random_permutation(Env, E),
    member(Name-procedure(Types), E), !,
    same_length(Types, Envs),
    same_length(Types, Rets),
    rets(Rets, Ret),
    maplist(=(Env), Envs),
    maplist(expr(NLvl, ELvl, Maj), Rets, Envs, Types, Args).
stmt(NLvl, ELvl, _, _, false, Env, [Func | Env], procedure(Name, Args, Body)) :-
    Func = Name-procedure(Types),
    Body = scope(_),
    newsyms(Env, proc, [Name]),
    list(0, 3, Args),
    pairs_keys_values(Args, Names, Types),
    newsyms(Env, arg, Names),
    maplist(expr_type, Types),
    append([Func | Args], Env, Env1),
    bool(Ret), !,
    stmt(NLvl, ELvl, procedure, none, Ret, Env1, _, Body).
stmt(NLvl, ELvl, _, _, false, Env, [Func | Env], function(Type, Name, Args, Body)) :-
    Func = Name-function(Type, Types),
    Body = scope(_),
    newsyms(Env, func, [Name]),
    expr_type(Type),
    list(0, 3, Args),
    pairs_keys_values(Args, Names, Types),
    newsyms(Env, arg, Names),
    maplist(expr_type, Types),
    append([Func | Args], Env, Env1),
    stmt(NLvl, ELvl, function(Type), none, true, Env1, _, Body).
stmt(_, _, procedure, _, true, Env, Env, return).
stmt(NLvl, ELvl, function(Type), _, true, Env, Env, return_with(Expr)) :-
    expr(NLvl, ELvl, function(Type), _, Env, Type, Expr).
stmt(NLvl, ELvl, Maj, _, Ret, Env, Env, scope(Body)) :-
    nlvl(NLvl, NLvl1),
    stmt_list(NLvl1, ELvl, Maj, none, Ret, [Env], _, Body).

stmt_list(NLvl, ELvl, Maj, Min, Ret, Pre, Post, Stmts) :-
    list(1, 5, Rets),
    rets(Rets, Ret),
    Pres = [Pre | Envs],
    same_length(Pres, Rets),
    append(Envs, [Post], Posts),
    maplist(stmt(NLvl, ELvl, Maj, Min), Rets, Pres, Posts, Stmts).

nondet(expr/7) :- !.
expr(_, _, _, false, _, boolean, true).
expr(_, _, _, false, _, boolean, false).
expr(_, _, _, false, _, integer, N) :-
    random_between(0, 100, N).
expr(NLvl, ELvl, Maj, Ret, Env, Type, op(Op, Expr)) :-
    elvl(ELvl, ELvl1),
    random_member((Op, Type), [
        (-, integer),
        (!, boolean)
    ]),
    expr(NLvl, ELvl1, Maj, Ret, Env, Type, Expr).
expr(NLvl, ELvl, Maj, Ret, Env, Type, op(Op, Expr1, Expr2)) :-
    elvl(ELvl, ELvl1),
    rets([Ret1, Ret2], Ret),
    random_member((OpType, Op, Type), [
        (Any, =, boolean),
        (Any, '!=', boolean),
        (boolean, &, boolean),
        (boolean, '|', boolean),
        (integer, >, boolean),
        (integer, <, boolean),
        (integer, >=, boolean),
        (integer, <=, boolean),
        (integer, +, integer),
        (integer, -, integer),
        (integer, *, integer),
        (integer, /, integer)
    ]),
    expr(NLvl, ELvl1, Maj, Ret1, Env, OpType, Expr1),
    expr(NLvl, ELvl1, Maj, Ret2, Env, OpType, Expr2).
expr(NLvl, ELvl, Maj, Ret, Env, Type, ref(Name, Args)) :-
    ref(Env, Name, FullType),
    decl_type(Type, Ctor, FullType), !,
    nth0(N, [expr, array1d, array2d], Ctor),
    length(Rets, N),
    length(Args, N),
    rets(Rets, Ret),
    findall(X, (
        member(R, Rets),
        elvl(ELvl, ELvl1),
        expr(NLvl, ELvl1, Maj, R, Env, integer, X)
    ), Args).
expr(NLvl, ELvl, Maj, Ret, Env, Type, yields(Stmt, Expr)) :-
    nlvl(NLvl, NLvl1),
    nlvl(ELvl, ELvl1),
    rets([Ret1, Ret2], Ret),
    stmt(NLvl1, ELvl1, Maj, none, Ret1, [Env], Post, Stmt),
    expr(NLvl1, ELvl1, Maj, Ret2, Post, Type, Expr).
expr(NLvl, ELvl, Maj, Ret, Env, Type, call(Name, Args)) :-
    elvl(ELvl, ELvl1),
    ref(Env, Name, function(Type, Types)), !,
    same_length(Types, Envs),
    same_length(Types, Rets),
    rets(Rets, Ret),
    maplist(=(Env), Envs),
    maplist(expr(NLvl, ELvl1, Maj), Rets, Envs, Types, Args).

nondet(bound/1) :- !.
bound(Bound) :-
    random_between(0, 100, Bound).
bound(Bound) :-
    random_between(-100, 100, I),
    random_between(I, 100, J),
    format(atom(Bound), '~w..~w', [I, J]).

decl_bound(expr, '').
decl_bound(array1d, S) :-
    bound(I),
    format(atom(S), '[~w]', I).
decl_bound(array2d, S) :-
    bound(I),
    bound(J),
    format(atom(S), '[~w, ~w]', [I, J]).

indent(I, J) :-
    atom_concat(I, '\t', J).

pp(I, declare(T, Vs), S) :-
    findall(X, (
        member(N-C, Vs),
        decl_bound(C, B),
        format(atom(X), '~w~w', [N, B])
    ), Vs1),
    atomic_list_concat(Vs1, ', ', V),
    format(atom(S), '~w~w ~w\n', [I, T, V]).
pp(I, assign(X, Y), S) :-
    pp_expr(I, X, X1),
    pp_expr(I, Y, Y1),
    format(atom(S), '~w~w <= ~w\n', [I, X1, Y1]).
pp(I, if_then(C, B), S) :-
    indent(I, J),
    pp_expr(I, C, C1),
    pp(J, B, B1),
    format(atom(S), '~wif ~w then\n~w~wend\n', [I, C1, B1, I]).
pp(I, if_then_else(C, T, F), S) :-
    indent(I, J),
    pp_expr(I, C, C1),
    pp(J, T, T1),
    pp(J, F, F1),
    format(atom(S), '~wif ~w then\n~w~welse\n~w~wend\n', [I, C1, T1, I, F1, I]).
pp(I, while(C, B), S) :-
    indent(I, J),
    pp_expr(I, C, C1),
    pp(J, B, B1),
    format(atom(S), '~wwhile ~w do\n~w~wend\n', [I, C1, B1, I]).
pp(I, exit_when(X), S) :-
    pp_expr(I, X, X1),
    format(atom(S), '~wexit when (~w)\n', [I, X1]).
pp(I, loop(B), S) :-
    indent(I, J),
    pp(J, B, B1),
    format(atom(S), '~wloop\n~w~wend\n', [I, B1, I]).
pp(I, put(As), S) :-
    maplist(pp_expr(I), As, As1),
    atomic_list_concat(As1, ', ', A),
    format(atom(S), '~wput ~w\n', [I, A]).
pp(I, get(As), S) :-
    maplist(pp_expr(I), As, As1),
    atomic_list_concat(As1, ', ', A),
    format(atom(S), '~wget ~w\n', [I, A]).
pp(I, call(N, As), S) :-
    (length(As, 0) ->
        format(atom(S), '~w~w\n', [I, N])
    ;
        maplist(pp_expr(I), As, As1),
        atomic_list_concat(As1, ', ', A),
        format(atom(S), '~w~w(~w)\n', [I, N, A])
    ).
pp(I, procedure(N, As, B), S) :-
    pp(I, B, B1),
    (length(As, 0) ->
        format(atom(S), '~wprocedure ~w\n~w\n', [I, N, B1])
    ;
        findall(X, (
            member(An-At, As),
            format(atom(X), '~w ~w', [At, An])
        ), As1),
        atomic_list_concat(As1, ', ', A1),
        format(atom(S), '~wprocedure ~w(~w)\n~w\n', [I, N, A1, B1])
    ).
pp(I, function(T, N, As, B), S) :-
    pp(I, B, B1),
    (length(As, 0) ->
        format(atom(S), '~w~w function ~w\n~w\n', [I, T, N, B1])
    ;
        findall(X, (
            member(An-At, As),
            format(atom(X), '~w ~w', [At, An])
        ), As1),
        atomic_list_concat(As1, ', ', A1),
        format(atom(S), '~w~w function ~w(~w)\n~w\n', [I, T, N, A1, B1])
    ).
pp(I, return_with(X), S) :-
    pp_expr(I, X, X1),
    format(atom(S), '~wreturn (~w)\n', [I, X1]).
pp(I, L, S) :-
    is_list(L),
    maplist(pp(I), L, Ss),
    atomic_list_concat(Ss, S).
pp(I, scope(B), S) :-
    indent(I, J),
    pp(J, B, B1),
    format(atom(S), '~wbegin\n~w~wend\n', [I, B1, I]).
pp(I, return, S) :-
    format(atom(S), '~wreturn\n', I).
pp(I, exit, S) :-
    format(atom(S), '~wexit\n', I).

pp_expr(I, op(O, X), S) :-
    pp_expr(I, X, X1),
    format(atom(S), '~w(~w)', [O, X1]).
pp_expr(I, op(O, X, Y), S) :-
    pp_expr(I, X, X1),
    pp_expr(I, Y, Y1),
    format(atom(S), '(~w) ~w (~w)', [X1, O, Y1]).
pp_expr(_, ref(X, []), X).
pp_expr(I, ref(X, As), S) :-
    maplist(pp_expr(I), As, As1),
    atomic_list_concat(As1, ', ', A),
    format(atom(S), '~w[~w]', [X, A]).
pp_expr(_, call(N, []), N).
pp_expr(I, call(N, As), S) :-
    maplist(pp_expr(I), As, As1),
    atomic_list_concat(As1, ', ', A),
    format(atom(S), '~w(~w)', [N, A]).
pp_expr(I, yields(B, R), S) :-
    indent(I, J),
    pp(J, B, B1),
    pp_expr(J, R, R1),
    format(atom(S), '{\n~w~wyields\n~w~w\n~w}', [B1, I, J, R1, I]).
pp_expr(_, true, true).
pp_expr(_, false, false).
pp_expr(_, N, N) :- integer(N).
pp_expr(_, skip, skip).
pp_expr(_, X, X) :-
    sub_atom(X, 0, 1, _, '"'),
    sub_atom(X, _, 1, 0, '"').

main :-
    X = scope(_),
    stmt(0, 0, top, none, false, [], [], X), !,
    pp('', X, S), !,
    writeln(S),
    halt.
