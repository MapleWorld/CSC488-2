import random
rules = """
program scope
statement variable '<=' expression
statement 'if' expression 'then' statement 'end'
statement 'if' expression 'then' statement 'else' statement 'end'
statement 'while' expression 'do' statement 'end'
statement 'loop' statement 'end'
statement 'exit'
statement 'exit' 'when' expression
statement 'return' '(' expression ')'
statement 'return'
statement 'put' output
statement 'get' input
statement identifier
statement identifier '(' arguments ')'
statement scope
statement type identifiers
statement type 'function' identifier scope
statement type 'function' identifier '(' parameters ')' scope
statement 'procedure' identifier scope
statement 'procedure' identifier '(' parameters ')' scope
statement statement statement
identifiers identifier
identifiers identifier '[' bound ']'
identifiers identifier '[' bound ',' bound ']'
identifiers identifiers ',' identifiers
bound integer
bound generalBound '..' generalBound
generalBound integer
generalBound '-' integer
scope 'begin' statement 'end'
scope 'begin' 'end'
output expression
output text
output 'skip'
output output ',' output
input variable
input input ',' input
type 'integer'
type 'boolean'
arguments expression
arguments arguments ',' arguments
parameters type identifier
parameters parameters ',' parameters
variable identifier
variable identifier '[' expression ']'
variable identifier '[' expression ',' expression ']'
expression expr6
expr6 expr5
expr6 expr6 '|' expr5
expr5 expr4
expr5 expr5 '&' expr4
expr4 expr3
expr4 '!' expr4
expr3 expr2
expr3 expr2 '=' expr2
expr3 expr2 '!=' expr2
expr3 expr2 '<' expr2
expr3 expr2 '<=' expr2
expr3 expr2 '>' expr2
expr3 expr2 '>=' expr2
expr2 expr1
expr2 expr2 '+' expr1
expr2 expr2 '-' expr1
expr1 expr0
expr1 expr1 '*' expr0
expr1 expr1 '/' expr0
expr0 expr_
expr0 '-' expr0
expr_ integer
expr_ 'true'
expr_ 'false'
expr_ '(' expression ')'
expr_ '{' statement 'yields' expression '}'
expr_ identifier '(' arguments ')'
expr_ identifier
expr_ identifier '[' expression ']'
expr_ identifier '[' expression ',' expression ']'
"""

rules = [i.split() for i in rules.split("\n") if i.strip()!=""]
rules = [(l[0],l[1:]) for l in rules]
m = {}
for h, b in rules:
	m.setdefault(h, [])
	m[h].append(b)
for i in list(m):
	upper = [j for j in m[i] if not len(j)<3]
	lower = [j for j in m[i] if len(j)<3]
	if not upper: upper = upper + lower
	if not lower: lower = upper + lower
	m[i] = lower, upper
#print(rules)

linebreakers = {
	'begin',
	'end',
	'statement',
	'{',
	'}'
}

indent = 0
def gen(h, d = 6):
	global indent
	if h == "'end'": indent = indent - 1
	if h == "'}'": indent = indent - 1
	if h[1:-1] in linebreakers or h in linebreakers:
		print("\n","\t"*indent, sep="", end="")
	if h == "'begin'": indent = indent + 1
	if h == "'then'": indent = indent + 1
	if h == "'do'": indent = indent + 1
	if h == "'loop'": indent = indent + 1
	if h == "'{'": indent = indent + 1
	if h in m:
		if d > 0:
			for i in random.choice(m[h][1]):
				gen(i, d-1)
		else:
			for i in random.choice(m[h][0]):
				gen(i, d-1)
			#print("...", end="")
	else:
		if h[0] == "'":
			print(h[1:-1], end=" ")
		elif h == "integer":
			print(random.randint(-100, 100), end=" ")
		elif h == "identifier":
			print(random.choice("qwertyuiopasdfghjklzxcvbnm"), end=" ")
		else:
			print('"%s"'%h, end=" ")
gen("program")
print()
