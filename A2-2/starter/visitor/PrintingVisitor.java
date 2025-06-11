package visitor;

import syntaxtree.*;
import java.util.*;

public class PrintingVisitor extends CFGGen implements GJNoArguVisitor<String> {

    private final Map<BB, Map<String, LatticeValue>> inMap;
    private final Map<BB, Map<String, LatticeValue>> outMap;
    private StringBuilder output = new StringBuilder();
    private int indentLevel = 0;
    // currentBB should be set appropriately before printing a block’s statements.
    private BB currentBB;

    public PrintingVisitor(Map<BB, Map<String, LatticeValue>> inMap,
            Map<BB, Map<String, LatticeValue>> outMap) {
        this.inMap = inMap;
        this.outMap = outMap;
    }

    // Returns the complete printed output.
    public String getOutput() {
        return output.toString();
    }

    // Helper to append indentation.
    private void indent() {
        output.append("    ".repeat(indentLevel));
    }

    // public void printProgram(ProgramCFG cfg) {
    // // For each method in the CFG, print its code.
    // // This example assumes cfg.methodBBSet is a Map<String, BB> mapping method
    // // names to their entry basic block.
    // for (String methodName : cfg.methodBBSet.keySet()) {
    // // Print a method header. You can obtain the class name in a similar manner.
    // output.append("public void ").append(methodName).append("() {\n");
    // indentLevel++;
    // // Set currentBB for this method to the entry basic block.
    // currentBB = cfg.methodBBSet.get(methodName);
    // // Now, assume that you want to print the instructions/statements in the
    // basic
    // // blocks.
    // // You might iterate over the basic blocks in an appropriate order.
    // List<BB> bbs = getBasicBlocksInOrder(currentBB);
    // for (BB bb : bbs) {
    // currentBB = bb;
    // // Print each instruction in the basic block.
    // for (Instruction inst : bb.instructions) {
    // indent();
    // output.append(printInstruction(inst)).append("\n");
    // }
    // }
    // indentLevel--;
    // output.append("}\n\n");
    // }
    // }
    // ----------------- Visitor Methods for Program Structure -----------------

    @Override
    public String visit(Goal n) {
        // Goal: f0 -> ( <REGLIMIT> )?; f1 -> MainClass(); f2 -> ( TypeDeclaration() )*;
        // f3 -> <EOF>
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return null;
    }

    @Override
    public String visit(MainClass n) {
        output.append("class ").append(n.f1.f0.tokenImage).append(" {\n");
        indentLevel++;
        indent();
        output.append("public static void main(String[] ").append(n.f11.f0.tokenImage).append(") {\n");
        indentLevel++;

        // Process variable declarations and statements:
        n.f14.accept(this); // Variable declarations
        n.f15.accept(this); // Statements

        indentLevel--;
        indent();
        output.append("}\n");
        indentLevel--;
        output.append("}\n\n");
        return null;
    }

    @Override
    public String visit(ClassDeclaration n) {
        output.append("class ").append(n.f1.f0.tokenImage).append(" {\n");
        indentLevel++;

        n.f3.accept(this); // Variable declarations
        n.f4.accept(this); // Method declarations

        indentLevel--;
        output.append("}\n\n");
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n) {
        output.append("class ").append(n.f1.f0.tokenImage)
                .append(" extends ").append(n.f3.f0.tokenImage).append(" {\n");
        indentLevel++;

        n.f5.accept(this); // Variable declarations
        n.f6.accept(this); // Method declarations

        indentLevel--;
        output.append("}\n\n");
        return null;
    }

    // ----------------- Visitor Methods for Methods and Statements
    // -----------------

    @Override
    public String visit(MethodDeclaration n) {
        // Method header: public <return type> methodName() {
        indent();
        // For simplicity, we assume n.f1 (return type) prints correctly.
        output.append("public ").append(n.f1.accept(this)).append(" ")
                .append(n.f2.f0.tokenImage).append("() {\n");
        indentLevel++;

        // Set currentBB to the entry block for this method.
        // Assume cfg.methodBBSet maps method names to their entry basic block.
        // currentBB = cfg.methodBBSet.get(n.f2.f0.tokenImage);

        n.f7.accept(this); // Variable declarations
        n.f8.accept(this); // Statements

        indent();
        output.append("return ").append(n.f10.accept(this)).append(";\n");
        indentLevel--;
        indent();
        output.append("}\n\n");
        return null;
    }

    @Override
    public String visit(VarDeclaration n) {
        indent();
        output.append(n.f0.accept(this)).append(" ")
                .append(n.f1.f0.tokenImage).append(";\n");
        return null;
    }

    @Override
    public String visit(AssignmentStatement n) {
        indent();
        // Substitute constants in the right-hand side.
        String lhs = n.f0.accept(this);
        String rhs = substituteExpression(n.f2);
        output.append(lhs).append(" = ").append(rhs).append(";\n");
        return null;
    }

    @Override
    public String visit(IfthenStatement n) {
        // If statement without an else branch.
        indent();
        // Substitute condition expression.
        String cond = substituteExpression(n.f2);
        output.append("if(").append(cond).append(") {\n");
        indentLevel++;

        // Visit the then-branch. (Before doing so, currentBB should be set for the true
        // branch.)
        // Depending on your CFG construction, you may update currentBB here.
        n.f4.accept(this);

        indentLevel--;
        indent();
        output.append("}\n");
        return null;
    }

    @Override
    public String visit(IfthenElseStatement n) {
        // If statement with an else branch.
        indent();
        String cond = substituteExpression(n.f2);
        output.append("if(").append(cond).append(") {\n");
        indentLevel++;

        n.f4.accept(this); // Then branch

        indentLevel--;
        indent();
        output.append("} else {\n");
        indentLevel++;

        n.f6.accept(this); // Else branch

        indentLevel--;
        indent();
        output.append("}\n");
        return null;
    }

    @Override
    public String visit(WhileStatement n) {
        indent();
        String cond = substituteExpression(n.f2);
        output.append("while(").append(cond).append(") {\n");
        indentLevel++;

        n.f4.accept(this);

        indentLevel--;
        indent();
        output.append("}\n");
        return null;
    }

    @Override
    public String visit(PrintStatement n) {
        indent();
        output.append("System.out.println(").append(n.f2.accept(this)).append(");\n");
        return null;
    }

    // ----------------- Visitor Methods for Expressions -----------------

    // For an Identifier, substitute its constant value if available.
    @Override
    public String visit(Identifier n) {
        return n.f0.tokenImage;
    }

    @Override
    public String visit(IntegerLiteral n) {
        return n.f0.tokenImage;
    }

    @Override
    public String visit(TrueLiteral n) {
        return n.f0.tokenImage;
    }

    @Override
    public String visit(FalseLiteral n) {
        return n.f0.tokenImage;
    }

    // For binary expressions, you can expand as needed. Here’s an example for
    // PlusExpression.
    @Override
    public String visit(PlusExpression n) {
        return n.f0.accept(this) + " + " + n.f2.accept(this);
    }

    @Override
    public String visit(MinusExpression n) {
        return n.f0.accept(this) + " - " + n.f2.accept(this);
    }

    @Override
    public String visit(TimesExpression n) {
        return n.f0.accept(this) + " * " + n.f2.accept(this);
    }

    @Override
    public String visit(DivExpression n) {
        return n.f0.accept(this) + " / " + n.f2.accept(this);
    }

    // ----------------- Helper Methods -----------------

    /**
     * substituteExpression checks if the expression is an Identifier and,
     * using the current basic block’s environment from inMap, returns its constant
     * literal if known. Otherwise, it returns the default printing.
     */
    private String substituteExpression(Node expr) {
        if (expr instanceof Identifier) {
            Identifier id = (Identifier) expr;
            String varName = id.f0.tokenImage;
            Map<String, LatticeValue> env = inMap.get(currentBB);
            if (env != null && env.containsKey(varName)) {
                LatticeValue lv = env.get(varName);
                if (lv.getState() == LatticeValue.State.CONSTANT) {
                    return lv.getConstant().toString();
                }
            }
            return varName;
        }
        // For other expression types, use the standard accept.
        return expr.accept(this);
    }
}