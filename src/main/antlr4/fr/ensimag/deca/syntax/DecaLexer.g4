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
fragment DIGIT : '1' .. '9';
fragment STRING_CAR : ~ ('"' | '\\' | '\n' | '\r' | '\t');

// Deca lexer rules.
ESPACE : ' ' {skip();};
STRING : '"' (STRING_CAR | '\\"' | '\\\\' )* '"' ;
PRINTLN : 'println';
OPARENT : '(';
CPARENT : ')';
SEMI : ';';