package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.TSTOCounter;
import fr.ensimag.deca.codegen.execerrors.MissingReturnError;
import fr.ensimag.deca.codegen.execerrors.StackOverflowExecError;
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
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
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
        stackOverflowCounter.addSavedRegisters((maxSavedRegisters == 0) ? 0 : maxSavedRegisters-1);

        compiler.addFirst(new Line("method body"));
        compiler.add(new Line("restauration des registres"));
        for (int i = maxSavedRegisters; i >=2 ; i--) {
            compiler.addFirst(new PUSH(Register.getR(compiler,i)));
            compiler.addInstruction(new POP(Register.getR(compiler,i)));
        }

        compiler.addFirst(new Line("sauvegarde des registres"));
        compiler.addFirst(new BOV(StackOverflowExecError.INSTANCE.getLabel())); // ordre des 2 instructions inversé à cause de addFirst()
        compiler.addFirst(new TSTO(stackOverflowCounter.getMaxTSTO()), stackOverflowCounter.getDetailsMaxTSTO());

        compiler.addInstruction(new RTS());

        mainProgram.append(compiler.getProgram());
        compiler.setProgram(mainProgram);
    }
    
    @Override
    protected void codeGenByteDeclMethod(
        ClassWriter cw,
        DecacCompiler compiler,
        ClassDefinition currentClass
    ) {
        String methodName = name.getName().toString();
        String desc = buildMethodDescriptor(this.params, this.type.getType());
        int access = Opcodes.ACC_PUBLIC;

        MethodVisitor mv = cw.visitMethod(
            access,
            methodName,
            desc,
            null, // signature if generic
            null  // exceptions
        );
        mv.visitCode();

        // Setup parameters in bytecode
        params.codeGenByteParamsInit(mv, compiler);

        // Generate method body
        body.codeGenByteMethodBody(mv, compiler, type.getType());

        // TODO: Possibly we want IRETURN, FRETURN, or RETURN, etc. if we know method is non-void
        // For now, rely on body to produce the correct RETURN

        mv.visitMaxs(0, 0); // or rely on COMPUTE_FRAMES
        mv.visitEnd();
    }
    
    public static String buildMethodDescriptor(ListDeclParam params, Type returnType) {
        StringBuilder sb = new StringBuilder("(");
        for (AbstractDeclParam param : params.getList()) {
            sb.append(param.getType().toJVMDescriptor());
        }
        sb.append(")");
        sb.append(returnType.toJVMDescriptor());
        return sb.toString();
    }

    public static String buildMethodDescriptor(Signature signature, Type returnType) {
        StringBuilder sb = new StringBuilder("(");
    
        // Use the getter to access parameter types
        for (Type paramType : signature.getParameters()) {
            sb.append(paramType.toJVMDescriptor());
        }
    
        sb.append(")");
        sb.append(returnType.toJVMDescriptor());
        return sb.toString();
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
        type.iter(f);
        name.iter(f);
        params.iter(f);
        body.iter(f);
    }



}