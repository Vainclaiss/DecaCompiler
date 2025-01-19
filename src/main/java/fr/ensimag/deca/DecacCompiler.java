package fr.ensimag.deca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

import fr.ensimag.deca.codegen.TSTOCounter;
import fr.ensimag.deca.codegen.execerrors.ExecError;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.AbstractLine;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.SETROUND_DOWNWARD;
import fr.ensimag.ima.pseudocode.instructions.SETROUND_TONEAREST;
import fr.ensimag.ima.pseudocode.instructions.SETROUND_TOWARDZERO;
import fr.ensimag.ima.pseudocode.instructions.SETROUND_UPWARD;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.deca.codegen.execerrors.StackOverflowExecError;

/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl01
 * @date 01/01/2025
 */
public class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);

    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");
    int nextLocalIndex = 0;

    private Set<ExecError> execErrors;
    private TSTOCounter stackOverflowCounter = new TSTOCounter();
    private int GBoffset;

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;
        this.environmentType = new EnvironmentType(this);
        this.execErrors = new HashSet<ExecError>();
        this.stackOverflowCounter = new TSTOCounter();
        this.GBoffset = 1;
    }

    public int incrGBOffset() {
        return GBoffset++;
    }

    public int getGBOffset() {
        return GBoffset;
    }

    public void resetTSTO() {
        this.stackOverflowCounter = new TSTOCounter();
    }

    public void addExecError(ExecError error) {
        execErrors.add(error);
    }

    public TSTOCounter getStackOverflowCounter() {
        return stackOverflowCounter;
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        program.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        program.addComment(comment);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        program.addLabel(label);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        program.addInstruction(instruction);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     *      java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        program.addInstruction(instruction, comment);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Line)
     */
    public void addFirst(Line i) {
        program.addFirst(i);
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addFirst(Instruction i) {
        program.addFirst(new Line(i));
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Instruction,
     *      java.lang.String)
     */
    public void addFirst(Instruction i, String comment) {
        program.addFirst(new Line(null, i, comment));
    }

    /**
     * @see
     *      fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }

    /**
     * Generate the code to handle error in the program
     * 
     * @param error
     */
    public void genCodeExecError(ExecError error) {
        addLabel(error.getLabel());
        addInstruction(new WSTR(error.getErrorMsg()));
        addInstruction(new WNL());
        addInstruction(new ERROR());
    }

    /**
     * Generate the code to handle all the execution errors of the program
     */
    public void genCodeAllExecErrors() {
        for (ExecError error : execErrors) {
            genCodeExecError(error);
        }
    }

    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private IMAProgram program = new IMAProgram();

    /** The global environment for types (and the symbolTable) */
    public final SymbolTable symbolTable = new SymbolTable();
    public final EnvironmentType environmentType;

    public Symbol createSymbol(String name) {
        return symbolTable.create(name);
    }

    public IMAProgram getProgram() {
        return program;
    }

    public void setProgram(IMAProgram program) {
        this.program = program;
    }

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String destFile = sourceFile.substring(0, sourceFile.length() - 5) + ".ass";
        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName   name of the destination (assembly) file
     * @param out        stream to use for standard output (output of decac -p)
     * @param err        stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);

        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        if (compilerOptions.getStopAfterParse()) {
            LOG.info("Stopped after parsing");
            prog.decompile(out);
            return false;
        }
        assert (prog.checkAllLocations());

        prog.verifyProgram(this);
        assert (prog.checkAllDecorations());

        if (compilerOptions.getStopAfterVerification()) {
            LOG.info("Stopped after verification");
            return false;
        }
        switch (compilerOptions.getFloatRoundMode()) {
            case UPWARD:
                program.addInstruction(new SETROUND_UPWARD());
                break;
            case DOWNWARD:
                program.addInstruction(new SETROUND_DOWNWARD());
                break;
            case TOWARDZERO:
                program.addInstruction(new SETROUND_TOWARDZERO());
                break;
            default:
                break;
        }

        addComment("start main program");

        prog.codeGenProgram(this);
        if (compilerOptions.getGenerateJavaClass()) {
            prog.codeGenByteProgram(this, destName);
        }
        addExecError(StackOverflowExecError.INSTANCE);
        genCodeAllExecErrors(); // genere le code de toutes les erreurs d'exécution à la fin du programme
        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");

        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");
        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err        Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError    When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     *                            compiler.
     * @throws LocationException  When a compilation error (incorrect program)
     *                            occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

    /**
     * Reset the local variable index before generating bytecode for a new method.
     */
    public void resetLocalIndex() {
        nextLocalIndex = 0;
    }

    /**
     * Allocate and return the next free local variable index (for bytecode).
     */
    public int allocateLocalIndex() {
        return nextLocalIndex++;
    }

}
