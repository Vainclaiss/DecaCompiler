package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.ParserRuleContext;

public class IntegerOverflow extends DecaRecognitionException{

    public IntegerOverflow(DecaParser recognizer,ParserRuleContext ctx) {
        super(recognizer, ctx);
    }

    @Override
    public String getMessage() {
        return "Error: the size of your integer is over 32 bits";
    }
}
