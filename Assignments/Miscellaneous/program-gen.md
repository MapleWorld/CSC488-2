CSC488 Toy Language's Semantically Correct Program Generator
============================================================

I guess recursive descent should be enough
and we absolute need to have scoping and typing.

For program generation we can either
- generate a AST in Java
- generate S-expression that we can convert to a AST generator
- directly generate program text(I would assume in Python)
- any other idea?

Some problem:
- generate some funny function and variable name
- production select rule to make sure no program get too nested
- production select rule to make sure test as much production are used
- etc... 

Anthony's Solution
------------------
1. Formalize the semantics in Agda. This gives (me) a clearer picture of what's going on, and what needs to be kept track of. In particular (and this will be useful for semantic analysis):
  1. The list of variables and their types that are in scope.
  2. The list of variables and their types that are introduced.
  3. Major/minor scopes.
  4. Whether or not all terminating paths lead to a return statement (critical for valid functions).
2. Convert to Prolog, since this is much easier to do with backtracking.
  1. To ensure that the generator terminates, I call the generator using `call_with_depth_limit`.
3. Profit!

Note that we can easily generate 'bad' test cases simply by changing the rules. For example, the function generator requires that its body has a return statement. If we change this to require that it does _not_ have a return statement, then we will generate faulty programs.
