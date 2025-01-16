package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.TSTOCounter;
import fr.ensimag.deca.codegen.execerrors.MissingReturnError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.IMAInternalError;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

public class DeclMethod extends AbstractDeclMethod {

    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private ListDeclParam params;
    final private AbstractMethodBody body;

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier name, ListDeclParam params, AbstractMethodBody body) {
        Validate.notNull(type);
        Validate.notNull(name);
        Validate.notNull(params);
        Validate.notNull(body);
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public MethodDefinition verifyDeclMethod(DecacCompiler compiler, AbstractIdentifier superClass,
            AbstractIdentifier currentClass)
            throws ContextualError {

        Type methodType = type.verifyType(compiler);
        Signature sig = params.verifyListDeclParam(compiler);

        ClassDefinition superDef = superClass.getClassDefinition();
        // superDef != null et c'est une class d'après la passe 1
        //superClass.setDefinition(superDef);

        EnvironmentExp envExpSuper = superDef.getMembers();
        ExpDefinition superMethodDef = envExpSuper.get(name.getName());

        if (superMethodDef != null) {
            if (!sig.equals(superMethodDef
                    .asMethodDefinition("Error: Cast fail from ExpDefinition to MethodDefinition", getLocation())
                    .getSignature())) {
                throw new ContextualError("Error: redefinition of a method with a different signature", getLocation());
            }

            Type superMethodType = superMethodDef.getType();
            if (!methodType.subType(superMethodType)) {
                throw new ContextualError("Error: redefinition of a method with incompatible type", getLocation());
            }
        }

        ClassDefinition currentClassDef = currentClass.getClassDefinition();
        int index;
        if (superMethodDef == null) {
            currentClassDef.incNumberOfMethods();
            index = currentClassDef.getNumberOfMethods();
        } else {
            index = superMethodDef
                    .asMethodDefinition("Error: Cast failed from ExpDefinition to MethodDefinition", getLocation())
                    .getIndex();
        }
        MethodDefinition newMethodDefinition = new MethodDefinition(methodType, getLocation(), sig, index);
        name.setDefinition(newMethodDefinition);

        return newMethodDefinition;

    }

    @Override
    protected void verifyDeclMethodBody(DecacCompiler compiler, EnvironmentExp envExp, AbstractIdentifier currentClass)
            throws ContextualError {

        Type returnType = type.verifyType(compiler);
        EnvironmentExp envExpParams = params.verifyListDeclParamBody(compiler, envExp);
        body.verifyMethodBody(compiler, envExpParams, currentClass.getClassDefinition(), returnType);
    }

    @Override
    protected void codeGenDeclMethod(DecacCompiler compiler, ClassDefinition currentClass) {

        compiler.addComment("Code de la methode " + name.getName().toString() + " dans la classe " + currentClass.getType().toString());
        
        compiler.addLabel(name.getMethodDefinition().getLabel());
        IMAProgram mainProgram = compiler.getProgram();
        compiler.setProgram(new IMAProgram());
        compiler.resetTSTO();
        
        params.codeGenListDeclParams(compiler);
        String labelSuffixe = currentClass.getType().toString() + "." + name.getName();
        Label finLabel = new Label("fin." + labelSuffixe);
        body.codeGenMethodBody(compiler, currentClass, finLabel);

        if (type.getType().isVoid()) {
            compiler.addInstruction(new BRA(finLabel));
        }

        compiler.genCodeExecError(new MissingReturnError(labelSuffixe));
        compiler.addLabel(finLabel);

        TSTOCounter stackOverflowCounter = compiler.getStackOverflowCounter();
        int maxSavedRegisters = stackOverflowCounter.getMaxRegisterUsed();
        stackOverflowCounter.addSavedRegisters(maxSavedRegisters-1);

        compiler.addFirst(new Line("method body"));
        compiler.add(new Line("restauration des registres"));
        for (int i = maxSavedRegisters; i >=2 ; i--) {
            compiler.addFirst(new PUSH(Register.getR(compiler,i)));
            compiler.addInstruction(new POP(Register.getR(compiler,i)));
        }

        compiler.addFirst(new Line("sauvegarde des registres"));
        compiler.addFirst(new TSTO(stackOverflowCounter.getMaxTSTO()), stackOverflowCounter.getDetailsMaxTSTO());

        compiler.addInstruction(new RTS());

        mainProgram.append(compiler.getProgram());
        compiler.setProgram(mainProgram);
    }

    @Override
    public AbstractIdentifier getName() {
        return name;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(type.decompile() + " " + name.decompile() + "(");
        s.print(params.decompile());
        s.print(")");
        s.println(body.decompile());
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // TODO C'est moi qui ai ecrit la signature donc à modifier maybe
    }

}