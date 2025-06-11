package visitor;

import java.util.Map;
import syntaxtree.*;

public class TransferHelper {
    public static void transferInstruction(Instruction inst, Map<String, LatticeValue> env) {
        // System.out.println("inside transfer " + inst + env);
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
            env.put(varName, LatticeValue.T);
        }

        if (node instanceof AssignmentStatement) {
            AssignmentStatement as = (AssignmentStatement) node;
            String varName = as.f0.f0.tokenImage;
            LatticeValue val = evaluateExpression(as.f2, env);
            // System.out.println(varName + " value " + val);
            env.put(varName, val);
        }

    }

    private static LatticeValue evaluateExpression(Expression exprNode, Map<String, LatticeValue> env) {
        if (exprNode.f0.choice instanceof PrimaryExpression) {
            return extractPrimaryExpression((PrimaryExpression) exprNode.f0.choice, env);
        }
        if (exprNode.f0.choice instanceof PlusExpression) {
            PlusExpression pe = (PlusExpression) exprNode.f0.choice;
            String id1 = pe.f0.f0.toString();
            String id2 = pe.f2.f0.toString();
            LatticeValue left = env.getOrDefault(id1, LatticeValue.T);
            LatticeValue right = env.getOrDefault(id2, LatticeValue.T);

            // System.out.println(id1 + " = " + left + " plus " + id2 + " = " + right);
            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT) {
                return LatticeValue.constant(left.getConstant() + right.getConstant());
            } else if (left.getState() == LatticeValue.State.B ||
                    right.getState() == LatticeValue.State.B) {
                return LatticeValue.B;
            }
            return LatticeValue.T;
        } else if (exprNode.f0.choice instanceof MinusExpression) {
            MinusExpression mi = (MinusExpression) exprNode.f0.choice;
            String id1 = mi.f0.f0.toString();
            String id2 = mi.f2.f0.toString();

            LatticeValue left = env.getOrDefault(id1, LatticeValue.T);
            LatticeValue right = env.getOrDefault(id2, LatticeValue.T);

            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT) {
                return LatticeValue.constant(left.getConstant() - right.getConstant());
            } else if (left.getState() == LatticeValue.State.B &&
                    right.getState() == LatticeValue.State.B) {
                return LatticeValue.B;
            }
            return LatticeValue.T;
        } else if (exprNode.f0.choice instanceof TimesExpression) {
            TimesExpression ti = (TimesExpression) exprNode.f0.choice;
            String id1 = ti.f0.f0.toString();
            String id2 = ti.f2.f0.toString();

            LatticeValue left = env.getOrDefault(id1, LatticeValue.T);
            LatticeValue right = env.getOrDefault(id2, LatticeValue.T);

            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT) {
                return LatticeValue.constant(left.getConstant() * right.getConstant());
            } else if (left.getState() == LatticeValue.State.B ||
                    right.getState() == LatticeValue.State.B) {
                return LatticeValue.B;
            }
            return LatticeValue.T;
        } else if (exprNode.f0.choice instanceof DivExpression) {
            DivExpression di = (DivExpression) exprNode.f0.choice;
            String id1 = di.f0.f0.toString();
            String id2 = di.f2.f0.toString();

            LatticeValue left = env.getOrDefault(id1, LatticeValue.T);
            LatticeValue right = env.getOrDefault(id2, LatticeValue.T);

            if (left.getState() == LatticeValue.State.CONSTANT &&
                    right.getState() == LatticeValue.State.CONSTANT &&
                    right.getConstant() != 0) {
                return LatticeValue.constant(left.getConstant() / right.getConstant());
            } else if (left.getState() == LatticeValue.State.B &&
                    right.getState() == LatticeValue.State.B) {
                return LatticeValue.B;
            }
        }
        return LatticeValue.T;
    }

    private static LatticeValue extractPrimaryExpression(PrimaryExpression pExpr, Map<String, LatticeValue> env) {

        if (pExpr.f0.choice instanceof IntegerLiteral) {
            IntegerLiteral lit = (IntegerLiteral) pExpr.f0.choice;
            int value = Integer.parseInt(lit.f0.tokenImage);
            // System.out.println(value + " inside primary");
            return LatticeValue.constant(value);
        }
        if (pExpr.f0.choice instanceof Identifier) {
            Identifier lit = (Identifier) pExpr.f0.choice;
            String varName = lit.f0.tokenImage;
            return env.getOrDefault(varName, LatticeValue.T);
        }

        return LatticeValue.B;
    }
}
