package visitor;

import syntaxtree.*;
import java.util.*;

// First visitor for analysis
class ConstantAnalysisVisitor extends GJNoArguDepthFirst<Object> {
    private Map<String, Integer> constantMap = new HashMap<>();
    private Map<String, Boolean> booleanConstantMap = new HashMap<>();
    private String currentMethod = "";

    public Map<String, Integer> getConstantMap() {
        return constantMap;
    }

    public Map<String, Boolean> getBooleanConstantMap() {
        return booleanConstantMap;
    }

    // Track method context
    public Object visit(MethodDeclaration n) {
        currentMethod = n.f2.f0.toString();
        Object ret = super.visit(n);
        currentMethod = "";
        return ret;
    }

    // Analyze assignments
    public Object visit(AssignmentStatement n) {
        String varName = n.f0.f0.toString() + "_" + currentMethod;
        Expression expr = n.f2;
        // Integer value = evaluateExpression(expr);
        // Check if right side is a constant integer
        if (expr.f0.choice instanceof PrimaryExpression) {
            if (expr.f0.choice instanceof IntegerLiteral) {
                constantMap.put(varName, Integer.parseInt(n.f2.f0.toString()));
            }
        }
        // Check if right side is an expression that can be evaluated
        else if (n.f2.f0.choice instanceof PlusExpression) {
            PlusExpression plus = (PlusExpression) n.f2.f0.choice;
            String leftVar = plus.f0.f0.toString() + "_" + currentMethod;
            String rightVar = plus.f2.f0.toString() + "_" + currentMethod;

            if (constantMap.containsKey(leftVar) &&
                    constantMap.containsKey(rightVar)) {
                int result = constantMap.get(leftVar) +
                        constantMap.get(rightVar);
                constantMap.put(varName, result);
            }
        } else if (n.f2.f0.choice instanceof TimesExpression) {
            TimesExpression plus = (TimesExpression) n.f2.f0.choice;
            String leftVar = plus.f0.f0.toString() + "_" + currentMethod;
            String rightVar = plus.f2.f0.toString() + "_" + currentMethod;

            if (constantMap.containsKey(leftVar) &&
                    constantMap.containsKey(rightVar)) {
                int result = constantMap.get(leftVar) +
                        constantMap.get(rightVar);
                constantMap.put(varName, result);
            }
        } else if (n.f2.f0.choice instanceof MinusExpression) {
            MinusExpression plus = (MinusExpression) n.f2.f0.choice;
            String leftVar = plus.f0.f0.toString() + "_" + currentMethod;
            String rightVar = plus.f2.f0.toString() + "_" + currentMethod;

            if (constantMap.containsKey(leftVar) &&
                    constantMap.containsKey(rightVar)) {
                int result = constantMap.get(leftVar) +
                        constantMap.get(rightVar);
                constantMap.put(varName, result);
            }
        } else if (n.f2.f0.choice instanceof DivExpression) {
            DivExpression plus = (DivExpression) n.f2.f0.choice;
            String leftVar = plus.f0.f0.toString() + "_" + currentMethod;
            String rightVar = plus.f2.f0.toString() + "_" + currentMethod;

            if (constantMap.containsKey(leftVar) &&
                    constantMap.containsKey(rightVar)) {
                int result = constantMap.get(leftVar) +
                        constantMap.get(rightVar);
                constantMap.put(varName, result);
            }
        }

        return super.visit(n);
    }
}
