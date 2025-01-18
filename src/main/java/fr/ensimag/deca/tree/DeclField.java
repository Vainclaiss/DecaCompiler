package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

public class DeclField extends AbstractDeclField {
    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private AbstractInitialization init;

    public DeclField(Visibility visibility, AbstractIdentifier type, AbstractIdentifier name,
            AbstractInitialization init) {
        Validate.notNull(visibility);
        Validate.notNull(type);
        Validate.notNull(name);
        Validate.notNull(init);
        this.visibility = visibility;
        this.type = type;
        this.name = name;
        this.init = init;
    }

    @Override
    public Symbol getName() {
        return name.getName();
    }

    @Override
    protected FieldDefinition verifyDeclField(DecacCompiler compiler, AbstractIdentifier superClass,
            AbstractIdentifier currentClass)
            throws ContextualError {

        Type nameType = type.verifyType(compiler);

        if (nameType.isVoid()) {
            throw new ContextualError("Error: 'void' cannot be used as a type for field declaration ", getLocation());
        }

        ClassDefinition superDef = superClass.getClassDefinition();
        // superDef != null et c'est une class d'après la passe 1
        //superClass.setDefinition(superDef);

        ExpDefinition envExpSupeDef = superDef.getMembers().get(name.getName());
        if (envExpSupeDef != null && !envExpSupeDef.isField()) {
            throw new ContextualError(
                    "Error: This name is already used for a non field objet at " + envExpSupeDef.getLocation(),
                    getLocation());
        }

        ClassDefinition currentClassDef = currentClass.getClassDefinition();

        // on conserve les fields des classes super
        currentClassDef.incNumberOfFields();
        int index = currentClassDef.getNumberOfFields();

        FieldDefinition newFieldDefinition = new FieldDefinition(nameType, getLocation(), visibility, currentClassDef, index);
        name.setDefinition(newFieldDefinition);

        return newFieldDefinition;
    }

    @Override
    protected void verifyDeclFieldBody(DecacCompiler compiler, EnvironmentExp envExp, AbstractIdentifier currentClass)
            throws ContextualError {

        Type nameType = type.verifyType(compiler);
        init.verifyInitialization(compiler, nameType, envExp, currentClass.getClassDefinition());
    }

    @Override
    protected void codeGenFieldInit(DecacCompiler compiler) {
        // TODO : ajouter la sauvegarde de registres + TSTO
        Type trueType = type.getType();
        if (init.isNoInitialization()) {
            if (trueType.isClass()) {
                compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
            }
            else if (trueType.isInt() || trueType.isBoolean()) {
                compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));
            }
            else if (trueType.isFloat()) {
                compiler.addInstruction(new LOAD(new ImmediateFloat(0), Register.R0));
            }

            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(name.getFieldDefinition().getIndex(), Register.R1)));
        }
        else {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            init.codeGenInitialization(compiler, new RegisterOffset(name.getFieldDefinition().getIndex(), Register.R1));
        }
        // TODO : ajouter le cas avec initialization -> done maybe
        FieldDefinition fieldDef = name.getFieldDefinition();
        fieldDef.setOperand(new RegisterOffset(fieldDef.getIndex(), Register.R0));
    }

    public void codeGenByteField(ClassWriter cw, DecacCompiler compiler, String classInternalName) {
        String fieldName = name.getName().toString(); 
        String fieldDesc = typeToJVMDescriptor(type.getType()); 
    
        int accessFlags = visibilityToAccessFlags(visibility);
        FieldVisitor fv = cw.visitField(accessFlags, fieldName, fieldDesc, null, null);
        fv.visitEnd();
    }
    @Override
    public void codeGenByteFieldInit(MethodVisitor mv, DecacCompiler compiler, String classInternalName) throws ContextualError  {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
    
        Type trueType = type.getType();
        if (init.isNoInitialization()) {
            if (trueType.isClass()) {

                mv.visitInsn(Opcodes.ACONST_NULL);
            } else if (trueType.isInt() || trueType.isBoolean()) {

                mv.visitInsn(Opcodes.ICONST_0);
            } else if (trueType.isFloat()) {

                mv.visitInsn(Opcodes.FCONST_0);
            } else {
                throw new UnsupportedOperationException("Unsupported field type for initialization: " + trueType);
            }
        } else {
            int localIndex= compiler.allocateLocalIndex();
            init.codeGenByteInitialization(mv, localIndex, compiler);
        }
    
        String fieldName = name.getName().toString();
        String fieldDesc = trueType.toJVMDescriptor();
        mv.visitFieldInsn(Opcodes.PUTFIELD, classInternalName, fieldName, fieldDesc);
    }
    
    

    public static String typeToJVMDescriptor(Type t) {
        if (t.isInt()) {
            return "I";      
        } else if (t.isFloat()) {
            return "F";      
        } else if (t.isBoolean()) {
            return "Z";      
        } else if (t.isClass()) {
           
            String internalName = t.getName().toString().replace('.', '/');
            return "L" + internalName + ";";
        } else if (t.isVoid()) {
            return "V";    
        }
        throw new UnsupportedOperationException("Unsupported Deca Type for JVM descriptor: " + t);
    }

    public static int visibilityToAccessFlags(Visibility visibility) {
        switch (visibility) {
            case PUBLIC:
                return Opcodes.ACC_PUBLIC;
            case PROTECTED:
                return Opcodes.ACC_PROTECTED;
            default:
                return Opcodes.ACC_PUBLIC;
        }
    }
    
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(visibility.toString().toLowerCase());
        s.print(" ");
        type.decompile(s);
        s.print(" ");
        name.decompile(s); // TODO: J'ai field dans le poly pas name...
        init.decompile(s);
        s.print(";");
    }

    @Override
    protected String prettyPrintNode() {
        return "DeclField (" + visibility + ")";
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        init.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
        init.iter(f);
    }

}