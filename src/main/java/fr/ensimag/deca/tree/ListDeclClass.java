package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

/**
 *
 * @author gl01
 * @date 01/01/2025
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClass(compiler);
        }
        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass c : getList()) {
            c.verifyClassMembers(compiler);
        }
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        // throw new UnsupportedOperationException("not yet implemented");

        // Rien a verifier (règle 3.2)
        for (AbstractDeclClass c : getList()) {
            c.verifyClassBody(compiler);
        }
    }

        public void codeGenByteClass(DecacCompiler compiler, String filename)  {
            for (AbstractDeclClass decl : getList()) {
                decl.codeGenByteClass(compiler, filename);  
            }
        }
    
    

    public void codeGenVtable(DecacCompiler compiler) {
        ClassDefinition objectDef = compiler.environmentType.OBJECT.getDefinition();
        objectDef.completeVtable();

        objectDef.codeGenVtable(compiler);

        for (AbstractDeclClass c : getList()) {
            c.codeGenVtable(compiler);
        }
    }

    public void codeGenClass(DecacCompiler compiler) {
        for (AbstractDeclClass c : getList()) {
            c.codeGenClass(compiler);
        }
    }

}
