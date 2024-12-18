// Generated from /home/unmars/ENSIMAG/2A/GL/Projet_GL/src/main/antlr4/fr/ensimag/deca/syntax/DecaLexer.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DecaLexer extends AbstractDecaLexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		EOL=1, COMMENT=2, ESPACE=3, OBRACE=4, CBRACE=5, OPARENT=6, CPARENT=7, 
		SEMI=8, COMMA=9, EQUALS=10, OR=11, AND=12, PRINTLN=13, STRING=14;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LETTER", "DIGIT", "STRING_CAR", "EOL", "COMMENT", "ESPACE", "OBRACE", 
			"CBRACE", "OPARENT", "CPARENT", "SEMI", "COMMA", "EQUALS", "OR", "AND", 
			"PRINTLN", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "' '", "'{'", "'}'", "'('", "')'", "';'", "','", "'='", 
			"'||'", "'&&'", "'println'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "EOL", "COMMENT", "ESPACE", "OBRACE", "CBRACE", "OPARENT", "CPARENT", 
			"SEMI", "COMMA", "EQUALS", "OR", "AND", "PRINTLN", "STRING"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}




	public DecaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DecaLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 3:
			EOL_action((RuleContext)_localctx, actionIndex);
			break;
		case 4:
			COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 5:
			ESPACE_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void EOL_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			skip();
			break;
		}
	}
	private void COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			 skip(); 
			break;
		}
	}
	private void ESPACE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:
			skip();
			break;
		}
	}

	public static final String _serializedATN =
		"\u0004\u0000\u000ec\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u00041\b"+
		"\u0004\n\u0004\f\u00044\t\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0005\u0010]\b\u0010\n\u0010\f\u0010`\t\u0010"+
		"\u0001\u0010\u0001\u0010\u0000\u0000\u0011\u0001\u0000\u0003\u0000\u0005"+
		"\u0000\u0007\u0001\t\u0002\u000b\u0003\r\u0004\u000f\u0005\u0011\u0006"+
		"\u0013\u0007\u0015\b\u0017\t\u0019\n\u001b\u000b\u001d\f\u001f\r!\u000e"+
		"\u0001\u0000\u0004\u0002\u0000AZaz\u0004\u0000\t\n\r\r\"\"\\\\\u0002\u0000"+
		"\t\n\r\r\u0001\u0000\n\nc\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t"+
		"\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001"+
		"\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001"+
		"\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001"+
		"\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001"+
		"\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001"+
		"\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000"+
		"\u0000\u0000\u0001#\u0001\u0000\u0000\u0000\u0003%\u0001\u0000\u0000\u0000"+
		"\u0005\'\u0001\u0000\u0000\u0000\u0007)\u0001\u0000\u0000\u0000\t,\u0001"+
		"\u0000\u0000\u0000\u000b7\u0001\u0000\u0000\u0000\r:\u0001\u0000\u0000"+
		"\u0000\u000f<\u0001\u0000\u0000\u0000\u0011>\u0001\u0000\u0000\u0000\u0013"+
		"@\u0001\u0000\u0000\u0000\u0015B\u0001\u0000\u0000\u0000\u0017D\u0001"+
		"\u0000\u0000\u0000\u0019F\u0001\u0000\u0000\u0000\u001bH\u0001\u0000\u0000"+
		"\u0000\u001dK\u0001\u0000\u0000\u0000\u001fN\u0001\u0000\u0000\u0000!"+
		"V\u0001\u0000\u0000\u0000#$\u0007\u0000\u0000\u0000$\u0002\u0001\u0000"+
		"\u0000\u0000%&\u000219\u0000&\u0004\u0001\u0000\u0000\u0000\'(\b\u0001"+
		"\u0000\u0000(\u0006\u0001\u0000\u0000\u0000)*\u0007\u0002\u0000\u0000"+
		"*+\u0006\u0003\u0000\u0000+\b\u0001\u0000\u0000\u0000,-\u0005/\u0000\u0000"+
		"-.\u0005/\u0000\u0000.2\u0001\u0000\u0000\u0000/1\b\u0003\u0000\u0000"+
		"0/\u0001\u0000\u0000\u000014\u0001\u0000\u0000\u000020\u0001\u0000\u0000"+
		"\u000023\u0001\u0000\u0000\u000035\u0001\u0000\u0000\u000042\u0001\u0000"+
		"\u0000\u000056\u0006\u0004\u0001\u00006\n\u0001\u0000\u0000\u000078\u0005"+
		" \u0000\u000089\u0006\u0005\u0002\u00009\f\u0001\u0000\u0000\u0000:;\u0005"+
		"{\u0000\u0000;\u000e\u0001\u0000\u0000\u0000<=\u0005}\u0000\u0000=\u0010"+
		"\u0001\u0000\u0000\u0000>?\u0005(\u0000\u0000?\u0012\u0001\u0000\u0000"+
		"\u0000@A\u0005)\u0000\u0000A\u0014\u0001\u0000\u0000\u0000BC\u0005;\u0000"+
		"\u0000C\u0016\u0001\u0000\u0000\u0000DE\u0005,\u0000\u0000E\u0018\u0001"+
		"\u0000\u0000\u0000FG\u0005=\u0000\u0000G\u001a\u0001\u0000\u0000\u0000"+
		"HI\u0005|\u0000\u0000IJ\u0005|\u0000\u0000J\u001c\u0001\u0000\u0000\u0000"+
		"KL\u0005&\u0000\u0000LM\u0005&\u0000\u0000M\u001e\u0001\u0000\u0000\u0000"+
		"NO\u0005p\u0000\u0000OP\u0005r\u0000\u0000PQ\u0005i\u0000\u0000QR\u0005"+
		"n\u0000\u0000RS\u0005t\u0000\u0000ST\u0005l\u0000\u0000TU\u0005n\u0000"+
		"\u0000U \u0001\u0000\u0000\u0000V^\u0005\"\u0000\u0000W]\u0003\u0005\u0002"+
		"\u0000XY\u0005\\\u0000\u0000Y]\u0005\"\u0000\u0000Z[\u0005\\\u0000\u0000"+
		"[]\u0005\\\u0000\u0000\\W\u0001\u0000\u0000\u0000\\X\u0001\u0000\u0000"+
		"\u0000\\Z\u0001\u0000\u0000\u0000]`\u0001\u0000\u0000\u0000^\\\u0001\u0000"+
		"\u0000\u0000^_\u0001\u0000\u0000\u0000_a\u0001\u0000\u0000\u0000`^\u0001"+
		"\u0000\u0000\u0000ab\u0005\"\u0000\u0000b\"\u0001\u0000\u0000\u0000\u0004"+
		"\u00002\\^\u0003\u0001\u0003\u0000\u0001\u0004\u0001\u0001\u0005\u0002";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}