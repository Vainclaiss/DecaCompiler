package fr.ensimag.deca.syntax;

import fr.ensimag.deca.tree.Main;
import fr.ensimag.deca.tree.ListDeclVar;
import fr.ensimag.deca.tree.ListInst;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestMainArgs {
    final Main main = new Main(new ListDeclVar(),new ListInst());

    @Test
    public void testgetDeclVar() {
        ListDeclVar ldv = main.getDeclVariables();
        assertTrue(ldv instanceof ListDeclVar);
    }

    @Test
    public void testgetInst() {
        ListInst li = main.getInsts();
        assertTrue(li instanceof ListInst);
    }
}