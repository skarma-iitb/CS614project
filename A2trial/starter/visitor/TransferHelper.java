package visitor;

import java.util.Map;
import syntaxtree.*;

public class TransferHelper {
    public static void transferInstruction(Instruction inst, Map<String, LatticeValue> env) {
        if (inst.instructionNode == null) {
            // System.out.println("nothing instruction");
            return;
        }

        Node node = inst.instructionNode;
        if (node instanceof NodeChoice) {
            node = ((NodeChoice) node).choice;
        }

        if (node instanceof VarDeclaration) {
            VarDeclaration vd = (VarDeclaration) node;
            // Use tokenImage to get the variable name.
            String varName = vd.f1.f0.tokenImage;
            env.put(varName, LatticeValue.top); // or LatticeValue.UNDEFINED if that fits your design
        }

        if (node instanceof AssignmentStatement) {
            AssignmentStatement as = (AssignmentStatement) node;
            // Extract the variable name from the tokenImage

            String varName = as.f0.f0.tokenImage;
            LatticeValue val = evaluateExpression(as.f2, env);
            // System.out.println(varName + " value " + val);
            env.put(varName, val);
        }

    }

    private static LatticeValue evaluateExpression(Expression exprNode, Map<String, LatticeValue> env) {
        // If the node is an Identifier, evaluate it by looking up in the environment.
        // System.out.println("inside the evalExpression");
        if (exprNode.f0.choice instanceof PrimaryExpression) {
            return extractPrimaryExpression((PrimaryExpression) exprNode.f0.choice, env);
        }

        // Handle PlusExpression: left + right
        if (exprNode.f0.choice instanceof PlusExpression) {
            Expression pe = (Expression) exprNode.f0.choice;
            LatticeValue left = evaluateExpression(pe, env);
            LatticeValue right = evaluateExpression(pe, env);
            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT) {
                return LatticeValue.constant(left.getConstant() + right.getConstant());
            }
            return LatticeValue.bottom;
        }
        // Handle MinusExpression: left - right
        else if (exprNode.f0.choice instanceof MinusExpression) {
            Expression mi = (Expression) exprNode.f0.choice;
            LatticeValue left = evaluateExpression(mi, env);
            LatticeValue right = evaluateExpression(mi, env);
            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT) {
                return LatticeValue.constant(left.getConstant() - right.getConstant());
            }
            return LatticeValue.bottom;
        }
        // Handle TimesExpression: left * right
        else if (exprNode.f0.choice instanceof TimesExpression) {
            Expression ti = (Expression) exprNode.f0.choice;
            LatticeValue left = evaluateExpression(ti, env);
            LatticeValue right = evaluateExpression(ti, env);
            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT) {
                return LatticeValue.constant(left.getConstant() * right.getConstant());
            }
            return LatticeValue.bottom;
        }
        // Handle DivExpression: left / right
        else if (exprNode.f0.choice instanceof DivExpression) {
            Expression di = (Expression) exprNode.f0.choice;
            LatticeValue left = evaluateExpression(di, env);
            LatticeValue right = evaluateExpression(di, env);
            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT &&
                    right.getConstant() != 0) {
                return LatticeValue.constant(left.getConstant() / right.getConstant());
            }
            return LatticeValue.bottom;
        }
        return LatticeValue.top;
    }

    private static LatticeValue extractPrimaryExpression(PrimaryExpression pExpr, Map<String, LatticeValue> env) {

        if (pExpr.f0.choice instanceof IntegerLiteral) {
            IntegerLiteral lit = (IntegerLiteral) pExpr.f0.choice;
            int value = Integer.parseInt(lit.f0.tokenImage);
            return LatticeValue.constant(value);
        }
        if (pExpr.f0.choice instanceof Identifier) {
            Identifier lit = (Identifier) pExpr.f0.choice;
            String varName = lit.f0.tokenImage;
            return env.getOrDefault(varName, LatticeValue.top);
        }

        return LatticeValue.bottom;
    }
}
