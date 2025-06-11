package visitor;

import syntaxtree.*;
import java.util.*;

public class ConstantPropagationAnalysis extends CFGGen {
    public Map<String, Integer> constantMap;
    private Queue<Node> worklist;
    private Set<Node> processed;

    private boolean changed;

    public ConstantPropagationAnalysis() {
        this.constantMap = new HashMap<>();
        this.worklist = new LinkedList<>();
        this.processed = new HashSet<>();
        this.changed = false;
    }

    private void addToWorklist(Node n) {
        if (!processed.contains(n)) {
            worklist.add(n);
        }
    }

    public void analyze(Node root) {
        root.accept(this);
        while (!worklist.isEmpty()) {
            Node current = worklist.poll();
            processed.add(current);
            changed = false;
            current.accept(this);
            if (changed) {
                processed.remove(current);
                addToWorklist(current);
            }
        }
    }

    @Override
    public String visit(AssignmentStatement n) {
        String varName = n.f0.f0.tokenImage;
        Expression expr = n.f2;
        Integer value = evaluateExpression(expr);
        if (value != null) {
            if (constantMap.containsKey(varName)) {
                if (!constantMap.get(varName).equals(value)) {
                    constantMap.remove(varName);
                    changed = true;
                }
            } else {
                constantMap.put(varName, value);
                changed = true;
            }
        } else {
            if (constantMap.containsKey(varName)) {
                constantMap.remove(varName);
                changed = true;
            }
        }
        return null;
    }

    private Integer evaluateExpression(Expression expr) {
        NodeChoice choice = expr.f0;

        if (choice.choice instanceof OrExpression) {
            return evaluateOrExpression((OrExpression) choice.choice);
        } else if (choice.choice instanceof AndExpression) {
            return evaluateAndExpression((AndExpression) choice.choice);
        } else if (choice.choice instanceof CompareExpression) {
            return evaluateCompareExpression((CompareExpression) choice.choice);
        } else if (choice.choice instanceof neqExpression) {
            return evaluateNeqExpression((neqExpression) choice.choice);
        } else if (choice.choice instanceof PlusExpression) {
            return evaluatePlusExpression((PlusExpression) choice.choice);
        } else if (choice.choice instanceof MinusExpression) {
            return evaluateMinusExpression((MinusExpression) choice.choice);
        } else if (choice.choice instanceof TimesExpression) {
            return evaluateTimesExpression((TimesExpression) choice.choice);
        } else if (choice.choice instanceof DivExpression) {
            return evaluateDivExpression((DivExpression) choice.choice);
        } else if (choice.choice instanceof ArrayLookup) {
            return evaluateArrayLookup((ArrayLookup) choice.choice);
        } else if (choice.choice instanceof ArrayLength) {
            return evaluateArrayLength((ArrayLength) choice.choice);
        } else if (choice.choice instanceof MessageSend) {
            return evaluateMessageSend((MessageSend) choice.choice);
        } else if (choice.choice instanceof PrimaryExpression) {
            return evaluatePrimaryExpression((PrimaryExpression) choice.choice);
        }

        return null;
    }

    private Integer evaluatePrimaryExpression(PrimaryExpression n) {
        NodeChoice choice = n.f0;

        if (choice.choice instanceof IntegerLiteral) {
            return Integer.parseInt(((IntegerLiteral) choice.choice).f0.tokenImage);
        } else if (choice.choice instanceof Identifier) {
            String varName = ((Identifier) choice.choice).f0.tokenImage;
            return constantMap.get(varName);
        }
        return null;
    }

    private Integer evaluateBinaryExpression(Identifier left, Identifier right, String operator) {
        String leftName = left.f0.tokenImage;
        String rightName = right.f0.tokenImage;
        Integer leftVal = null;
        Integer rightVal = null;
        try {
            leftVal = Integer.parseInt(leftName);
        } catch (NumberFormatException e) {
            leftVal = constantMap.get(leftName);
        }
        try {
            rightVal = Integer.parseInt(rightName);
        } catch (NumberFormatException e) {
            rightVal = constantMap.get(rightName);
        }
        if (leftVal != null && rightVal != null) {
            switch (operator) {
                case "+":
                    return leftVal + rightVal;
                case "-":
                    return leftVal - rightVal;
                case "*":
                    return leftVal * rightVal;
                case "/":
                    return rightVal != 0 ? leftVal / rightVal : null;
                case "<":
                    return leftVal < rightVal ? 1 : 0;
                case "==":
                    return leftVal.equals(rightVal) ? 1 : 0;
                case "!=":
                    return !leftVal.equals(rightVal) ? 1 : 0;
                case "&&":
                    return (leftVal != 0 && rightVal != 0) ? 1 : 0;
                case "||":
                    return (leftVal != 0 || rightVal != 0) ? 1 : 0;
                default:
                    return null;
            }
        }
        return null;
    }

    private Integer evaluatePlusExpression(PlusExpression n) {
        return evaluateBinaryExpression(n.f0, n.f2, "+");
    }

    private Integer evaluateMinusExpression(MinusExpression n) {
        return evaluateBinaryExpression(n.f0, n.f2, "-");
    }

    private Integer evaluateTimesExpression(TimesExpression n) {
        return evaluateBinaryExpression(n.f0, n.f2, "*");
    }

    private Integer evaluateDivExpression(DivExpression n) {
        return evaluateBinaryExpression(n.f0, n.f2, "/");
    }

    private Integer evaluateCompareExpression(CompareExpression n) {
        return evaluateBinaryExpression(n.f0, n.f2, "<");
    }

    private Integer evaluateNeqExpression(neqExpression n) {
        return evaluateBinaryExpression(n.f0, n.f2, "!=");
    }

    private Integer evaluateAndExpression(AndExpression n) {
        return evaluateBinaryExpression(n.f0, n.f2, "&&");
    }

    private Integer evaluateOrExpression(OrExpression n) {
        return evaluateBinaryExpression(n.f0, n.f2, "||");
    }

    private Integer evaluateArrayLookup(ArrayLookup n) {
        return null;
    }

    private Integer evaluateArrayLength(ArrayLength n) {
        return null;
    }

    private Integer evaluateMessageSend(MessageSend n) {
        return null;
    }

    public void constant() {
        System.out.println("Constant Variables:");
        if (constantMap.isEmpty()) {
            System.out.println("No constant variables found.");
        } else {
            for (Map.Entry<String, Integer> entry : constantMap.entrySet()) {
                System.out.printf("%s = %d%n", entry.getKey(), entry.getValue());
            }
        }
    }

    public Map<String, Integer> getConstantMap() {
        return new HashMap<>(constantMap);
    }
}
