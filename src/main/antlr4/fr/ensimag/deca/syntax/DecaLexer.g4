lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

//fragment
fragment LETTER : ('a' .. 'z' | 'A' .. 'Z');
fragment DIGIT : '0' .. '9';
fragment NUMPOS : '1' .. '9';
fragment STRING_CAR : ~ ('"' | '\\' | '\n');
//fragment FLOAT
fragment SIGN : ('+' | '-' )?;
fragment EXP : ('E' | 'e') SIGN DIGIT+;
fragment DEC : DIGIT+ '.' DIGIT+;
fragment FLOATDEC : (DEC | DEC EXP) ('F' | 'f')?;
fragment DIGITHEX : DIGIT | 'A'..'F' | 'a' .. 'f';
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN DIGIT+ ('F' | 'f')?;
fragment FILENAME : (LETTER | DIGIT | '.' | '-' | '_')+;

// Deca lexer rules.

//SKIP
EOL : ('\n') {skip();};
ESPACE : ' ' {skip();};
COMMENT : ('//' (~('\n'))* | '/*' (~ ('"' | '\\' | '\n'  | '*') | ('*' ~'/')|EOL  | '\\"' | '\\\\')* '*/'){ skip(); };

// SINGLE SYMBOLS
OBRACE : '{';
CBRACE : '}';
OPARENT : '(';
CPARENT : ')';
SEMI : ';';
COMMA : ',';
EQUALS : '=';
OR : '||';
AND : '&&';
EQEQ : '==';
NEQ : '!=';
LEQ : '<=';
GEQ : '>=';
LT : '<';
GT : '>';
PLUS : '+';
MINUS : '-';
TIMES : '*';
SLASH : '/';
PERCENT : '%';
EXCLAM : '!';
DOT : '.';

// WORDS
PRINT : 'print';
PRINTX : 'printx';
PRINTLN : 'println';
PRINTLNX : 'printlnx';
WHILE : 'while';
RETURN : 'return';
IF : 'if';
ELSE : 'else';
INSTANCEOF : 'instanceof';
READINT :'readInt';
READFLOAT :'readFloat';
NEW : 'new';
TRUE : 'true';
FALSE : 'false';
THIS : 'this';
NULL : 'null';
CLASS : 'class';
EXTENDS : 'extends';
PROTECTED : 'protected';
ASM : 'asm';

// EXPRESSIONS
STRING : '"' (STRING_CAR |'\\t' | '\\r' |'\\n' | '\\"' | '\\\\' )* '"' ;
MULTI_LINE_STRING : '"' (STRING_CAR | EOL | '\\"' | '\\\\')* '"';
FLOAT : (FLOATDEC | FLOATHEX);
INT : ('0' | NUMPOS DIGIT*);
IDENT : (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;
INCLUDE : '#include' (' ')* '"' FILENAME '"'{
   doInclude(getText());
   skip();
};