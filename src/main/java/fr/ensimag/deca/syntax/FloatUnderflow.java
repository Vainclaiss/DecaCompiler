package fr.ensimag.deca.syntax;
import org.antlr.v4.runtime.ParserRuleContext;
public class FloatUnderflow extends DecaRecognitionException {
    public FloatUnderflow(DecaParser recognizer,ParserRuleContext ctx) {
        super(recognizer,ctx);
    }

    @Override
    public String getMessage() {
        return "Error: Float UnderFlow";
    }
}
