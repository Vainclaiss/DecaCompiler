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

fragment LETTER : ('a' .. 'z' | 'A' .. 'Z');
fragment DIGIT : '0' .. '9';
fragment NUMPOS : '1' .. '9';
fragment STRING_CAR : ~ ('"' | '\\' | '\n' | '\r' | '\t');

// Deca lexer rules.

//SKIP
EOL : ('\n' | '\r' | '\t') {skip();};
COMMENT : '//' (~('\n'))* { skip(); };
ESPACE : ' ' {skip();};

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


// EXPRESSIONS
STRING : '"' (STRING_CAR | '\\"' | '\\\\' )* '"' ;
MULTI_LINE_STRING : '"' (STRING_CAR | EOL | '\\"' | '\\\\')* '"';
INT : ('0' | NUMPOS DIGIT*);
IDENT :(LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;

