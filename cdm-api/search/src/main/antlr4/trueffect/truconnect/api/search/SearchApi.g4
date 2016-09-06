grammar SearchApi;

query
	: expression? order?
	;

order
	: ('ordering' | 'sorting') orderTerm (',' orderTerm)*
	;
	
orderTerm
	: IDENTIFIER ('asc' | 'desc')?
	;

expression 
    : '(' expression ')' logicalExpression?
    | relation logicalExpression?
    ;

logicalExpression
    : 'or' expression
    | 'and' expression
    ;

relation
    : IDENTIFIER 
        ( 'not equals to'
        | 'equals to'
        | 'greater than or equals to'
        | 'greater than'
        | 'less than or equals to'
        | 'less than'
        | 'does not contain'
        | 'contains'
        | 'does not start with'
        | 'starts with'
        | 'does not end with'
        | 'ends with'
        | 'is not'
        | 'is'
        | '='
        | '!=' 
        | '>' 
        | '>=' 
        | '<' 
        | '<='
        ) LITERAL
    | IDENTIFIER ('between' | 'not between') LITERAL 'and' LITERAL
    | IDENTIFIER ('inside' | 'in' | 'not inside' | 'not in') '[' LITERAL (',' LITERAL)* ']'
    ;

LITERAL
    : DATE_VALUE
    | INTEGER_VALUE
    | STRING_VALUE
    | CONSTANT_VALUE
    ;

IDENTIFIER : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
INTEGER_VALUE : '-'? ('0'..'9'|'.')+ ;
DATE_VALUE 
    : FOUR_DIGITS ('-') TWO_DIGITS ('-') TWO_DIGITS ('T') TWO_DIGITS (':') TWO_DIGITS (':') TWO_DIGITS ('-'|'+') TWO_DIGITS (':') TWO_DIGITS // 2006-08-18T08:52:33-06:00
    | FOUR_DIGITS ('-') TWO_DIGITS ('-') TWO_DIGITS ('T') TWO_DIGITS (':') TWO_DIGITS (':') TWO_DIGITS // 2006-08-18T08:52:33
    | FOUR_DIGITS ('-') TWO_DIGITS ('-') TWO_DIGITS // 2006-08-18
    ;
FOUR_DIGITS : TWO_DIGITS TWO_DIGITS ;
TWO_DIGITS : DIGIT DIGIT ;
DIGIT : '0'..'9' ;

CONSTANT_VALUE
    : 'true'
    | 'false'
    | 'null'
    ; 

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) -> skip
    ;

STRING_VALUE
    :  '"' ( ESCAPED_SEQUENCE | ~('\\'|'"') )* '"'
    ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESCAPED_SEQUENCE
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESCAPED
    |   OCTAL_ESCAPED
    ;

fragment
OCTAL_ESCAPED
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESCAPED
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
