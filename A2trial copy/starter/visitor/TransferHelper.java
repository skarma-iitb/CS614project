package visitor;

import java.util.Map;
import syntaxtree.*;

public class TransferHelper {
    public static void transferInstruction(Instruction inst, Map<String, LatticeValue> env) {
        System.out.println(" inside ");
        if (inst.instructionNode == null) {
            System.out.println("nothing instruction");
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
            System.out.println(varName + " value " + val);
            env.put(varName, val);
        }

    }

    private static LatticeValue evaluateExpression(Node exprNode, Map<String, LatticeValue> env) {
        // If the node is an Identifier, evaluate it by looking up in the environment.
        if (exprNode instanceof PrimaryExpression) {
            PrimaryExpression pe = (PrimaryExpression) exprNode;
            System.out.println(" Primary " + pe.toString());
            switch (pe.f0.which) {

                case 0: // IntegerLiteral alternative
                    IntegerLiteral lit = (IntegerLiteral) pe.f0.choice;
                    try {
                        int value = Integer.parseInt(lit.f0.tokenImage);
                        return LatticeValue.constant(value);
                    } catch (NumberFormatException e) {
                        return LatticeValue.bottom;

                    }
                case 3: // Identifier alternative
                    Identifier id = (Identifier) pe.f0.choice;
                    String varName = id.f0.tokenImage;
                    return env.getOrDefault(varName, LatticeValue.top);
                // You can add additional cases for TrueLiteral, FalseLiteral, etc.
                default:
                    return LatticeValue.bottom;
            }
        }

        // Handle PlusExpression: left + right
        else if (exprNode instanceof PlusExpression) {
            PlusExpression pe = (PlusExpression) exprNode;
            LatticeValue left = evaluateExpression(pe.f0, env);
            LatticeValue right = evaluateExpression(pe.f2, env);
            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT) {
                return LatticeValue.constant(left.getConstant() + right.getConstant());
            }
            return LatticeValue.bottom;
        }
        // Handle MinusExpression: left - right
        else if (exprNode instanceof MinusExpression) {
            MinusExpression me = (MinusExpression) exprNode;
            LatticeValue left = evaluateExpression(me.f0, env);
            LatticeValue right = evaluateExpression(me.f2, env);
            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT) {
                return LatticeValue.constant(left.getConstant() - right.getConstant());
            }
            return LatticeValue.bottom;
        }
        // Handle TimesExpression: left * right
        else if (exprNode instanceof TimesExpression) {
            TimesExpression te = (TimesExpression) exprNode;
            LatticeValue left = evaluateExpression(te.f0, env);
            LatticeValue right = evaluateExpression(te.f2, env);
            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT) {
                return LatticeValue.constant(left.getConstant() * right.getConstant());
            }
            return LatticeValue.bottom;
        }
        // Handle DivExpression: left / right
        else if (exprNode instanceof DivExpression) {
            DivExpression de = (DivExpression) exprNode;
            LatticeValue left = evaluateExpression(de.f0, env);
            LatticeValue right = evaluateExpression(de.f2, env);
            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT &&
                    right.getConstant() != 0) {
                return LatticeValue.constant(left.getConstant() / right.getConstant());
            }
            return LatticeValue.bottom;
        }
        return LatticeValue.top;
    }
}