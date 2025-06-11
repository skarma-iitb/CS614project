package visitor;

import syntaxtree.*;
import java.util.*;

// Class to store constant information for each variable

// Visitor for constant analysis using CFG
public class ConstantAnalyzer {
    private Map<String, Map<String, ConstantInfo>> methodVarMap;
    private String currentMethod;
    private ProgramCFG cfg;

    public ConstantAnalyzer(ProgramCFG cfg) {
        this.cfg = cfg;
        this.methodVarMap = new HashMap<>();
    }

    public Map<String, Map<String, ConstantInfo>> analyze() {
        // Debug print the CFG structure first
        System.out.println("Analyzing CFG structure:");
        for (String className : cfg.classMethodList.keySet()) {
            System.out.println("Class: " + className);
            Set<String> methods = cfg.classMethodList.get(className);
            for (String methodName : methods) {
                System.out.println("  Method: " + methodName);
                BB bb = cfg.methodBBSet.get(methodName);
                if (bb != null) {
                    System.out.println("    Basic Blocks found");
                } else {
                    System.out.println("    No Basic Blocks!");
                }
            }
        }

        // Initialize method maps
        for (String className : cfg.classMethodList.keySet()) {
            Set<String> methods = cfg.classMethodList.get(className);
            for (String methodName : methods) {
                currentMethod = methodName;
                methodVarMap.put(methodName, new HashMap<>());

                // Debug print method entry
                System.out.println("\nAnalyzing method: " + methodName);

                BB entryBB = cfg.methodBBSet.get(methodName);
                if (entryBB != null) {
                    analyzeBasicBlock(entryBB, new HashSet<>());
                }
            }
        }

        return methodVarMap;
    }

    private void analyzeBasicBlock(BB block, Set<BB> visited) {
        if (visited.contains(block))
            return;
        visited.add(block);

        // Debug print block analysis
        System.out.println("  Analyzing Basic Block");
        System.out.println("  Number of instructions: " + block.instructions.size());

        for (Instruction inst : block.instructions) {
            // Debug print instruction
            System.out.println("    Analyzing instruction: " + inst.toString());
            analyzeInstruction(inst);
        }

        for (BB successor : block.outgoingEdges) {
            analyzeBasicBlock(successor, visited);
        }
    }

    private void analyzeInstruction(Instruction inst) {
        String instStr = inst.toString();
        System.out.println("      Instruction string: " + instStr);

        if (instStr.contains("=")) {
            String[] parts = instStr.trim().split("=");
            if (parts.length != 2) {
                System.out.println("      Invalid assignment format");
                return;
            }

            String var = parts[0].trim();
            String expr = parts[1].trim().replaceAll(";", ""); // Remove any semicolons

            System.out.println("      Variable: " + var);
            System.out.println("      Expression: " + expr);

            Map<String, ConstantInfo> varMap = methodVarMap.get(currentMethod);
            if (varMap == null) {
                System.out.println("      No variable map for method: " + currentMethod);
                return;
            }

            // Handle integer literals
            if (expr.matches("-?\\d+")) {
                int value = Integer.parseInt(expr);
                varMap.put(var, new ConstantInfo(true, value));
                System.out.println("      Stored constant: " + var + " = " + value);
            }
            // Handle arithmetic expressions
            else if (expr.matches(".*[+\\-*/].*")) {
                analyzeArithmeticExpression(var, expr, varMap);
            }
            // Handle variable assignments and other cases
            else {
                ConstantInfo sourceInfo = varMap.get(expr);
                if (sourceInfo != null && sourceInfo.isConstant()) {
                    varMap.put(var, new ConstantInfo(true, sourceInfo.getValue()));
                    System.out.println("      Propagated constant: " + var + " = " + sourceInfo.getValue());
                } else {
                    varMap.put(var, new ConstantInfo(false, 0));
                    System.out.println("      Marked as non-constant: " + var);
                }
            }
        }
    }

    private void analyzeArithmeticExpression(String var, String expr, Map<String, ConstantInfo> varMap) {
        System.out.println("      Analyzing arithmetic: " + expr);

        // Handle different operators
        String[] parts;
        char operator;
        if (expr.contains("+")) {
            parts = expr.split("\\+");
            operator = '+';
        } else if (expr.contains("-")) {
            parts = expr.split("-");
            operator = '-';
        } else if (expr.contains("*")) {
            parts = expr.split("\\*");
            operator = '*';
        } else if (expr.contains("/")) {
            parts = expr.split("/");
            operator = '/';
        } else {
            System.out.println("      No valid operator found");
            varMap.put(var, new ConstantInfo(false, 0));
            return;
        }

        if (parts.length != 2) {
            System.out.println("      Invalid expression format");
            varMap.put(var, new ConstantInfo(false, 0));
            return;
        }

        String op1 = parts[0].trim();
        String op2 = parts[1].trim();

        // Try to get constant values
        ConstantInfo ci1 = varMap.get(op1);
        ConstantInfo ci2 = varMap.get(op2);

        // Check if we can evaluate the expression
        if (ci1 != null && ci2 != null && ci1.isConstant() && ci2.isConstant()) {
            int result = computeOperation(ci1.getValue(), ci2.getValue(), operator);
            varMap.put(var, new ConstantInfo(true, result));
            System.out.println("      Computed constant: " + var + " = " + result);
        } else {
            varMap.put(var, new ConstantInfo(false, 0));
            System.out.println("      Cannot compute constant value");
        }
    }

    private int computeOperation(int v1, int v2, char op) {
        switch (op) {
            case '+':
                return v1 + v2;
            case '-':
                return v1 - v2;
            case '*':
                return v1 * v2;
            case '/':
                return v1 / v2;
            default:
                return 0;
        }
    }

    public void printAnalysisResults() {
        System.out.println("\n=== Constant Analysis Results ===");
        for (String methodName : methodVarMap.keySet()) {
            System.out.println("\nMethod: " + methodName);
            Map<String, ConstantInfo> varMap = methodVarMap.get(methodName);
            for (String var : varMap.keySet()) {
                ConstantInfo info = varMap.get(var);
                if (info.isConstant()) {
                    System.out.println("Variable: " + var + " = " + info.getValue() + " (constant)");
                } else {
                    System.out.println("Variable: " + var + " (not constant)");
                }
            }
        }
        System.out.println("\n==============================\n");
    }
}