grammar generate;

// toplevel
statement
    : CONSTSTR
	| NUMBER
	| Variable
	| func
    ;

CONSTSTR : '"' (.)* '"';
NUMBER : [0-9]+ ;
Variable : ([a-zA-Z_])([a-zA-Z0-9_])* ;
func : md5 | substr | concat;
md5 : 'md5' '(' (statement) ')';
substr : 'substr' '(' ( statement ',' NUMBER ',' NUMBER) ')' ;
concat : 'concat' '(' (statement ',' statement) ')' ;

WS : [ \r\n\t] -> skip ;