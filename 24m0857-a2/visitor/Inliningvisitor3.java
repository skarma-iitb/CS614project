package visitor;

import java.util.*;

import syntaxtree.*;

public class Inliningvisitor3 extends GJDepthFirst<String, String> {
    public HashMap<String, String> possibleMethods;
    private String currentClass;
    private String parentClass;
    private List<String> currentMethodParameters = new ArrayList<>();
    public HashMap<String, List<String>> methodTable = new HashMap<>();
    public HashMap<String, List<String>> methodTable1 = new HashMap<>();
    public HashMap<String, String> vartype = new HashMap<>();
    public HashMap<String, String> varvalue = new HashMap<>();
    public HashMap<String, String> varvalue1 = new HashMap<>();
    public HashMap<String, String> value = new HashMap<>();
    public HashMap<String, String> methodvalue = new HashMap<>();
    public StringBuilder inlining = new StringBuilder();
    public StringBuilder inlining2 = new StringBuilder();
    public ArrayList<String> aar = new ArrayList<>();

    public Inliningvisitor3(HashMap<String, String> possibleMethods) {
        this.possibleMethods = possibleMethods;
    }

    public void clearCurrentMethodParameters() {
        currentMethodParameters.clear();
    }

    public HashMap<String, List<String>> getmethodtable() {
        return methodTable;
    }

    public HashMap<String, List<String>> getmethodtable1() {
        return methodTable1;
    }

    public StringBuilder getInlining() {
        return inlining;
    }

    public StringBuilder getInlining2() {
        return inlining2;
    }

    public HashMap<String, String> getmethodvalue() {
        return methodvalue;
    }

    public HashMap<String, String> getvarvalue() {
        return varvalue1;
    }

    // private static class MethodInfo {
    // String name;
    // String className;
    // Map<String, String> parameters;
    // String returnvalue;
    // // Add other necessary fields
    // @Override
    // public String toString() {
    // return "MethodInfo{" +
    // "name='" + name + '\'' +
    // ", className='" + className + '\'' +
    // ", parameters=" + parameters +
    // ", returnvalue='" + returnvalue + '\'' +
    // '}';
    // }
    // // method declare
    // // MethodInfo info = new MethodInfo();
    // // methodTable.put(currentClass + "_" + currentMethod, info);
    // // info.name = currentMethod;
    // // info.className = currentClass;
    // //
    // // if (n.f4.present()) {
    // // n.f4.accept(this);
    // // // Now params contains all formal parameter name-type pairs.
    // // // For example, you can store it in your MethodInfo:
    // // info.parameters = new HashMap<>(parameters);
    // // }
    // // info.returnvalue = n.f10.f0.toString();
    // // currentMethod = null;

    // }
    @Override
    public String visit(ClassDeclaration n, String s) {
        currentClass = n.f1.f0.toString();
        super.visit(n, currentClass);
        // currentClass = null;
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n, String s) {
        currentClass = n.f1.f0.toString();
        parentClass = n.f3.f0.toString();
        super.visit(n, currentClass);
        return null;
    }

    @Override
    public String visit(VarDeclaration n, String s) {
        // n.f0.accept(this);
        // n.f2.accept(this);
        String type = extractFieldType(n.f0);
        String vardec = n.f1.f0.toString();
        vartype.put(vardec, type);
        currentMethodParameters.add(vardec);
        return null;
    }

    @Override
    public String visit(AssignmentStatement n, String s) {
        String id = n.f0.f0.toString();
        String result = (expressionExtract(n.f2, id));
        // System.out.println(" value of assign " + id + " =" + result);
        // currentMethodParameters.add(id + "=" + result);
        // varvalue.put(id, result);
        varvalue.put(id, result);
        return null;
    }

    @Override
    public String visit(MethodDeclaration n, String id) {
        String currentMethod = n.f2.f0.toString();
        if (possibleMethods.containsKey(currentMethod)) {
            clearCurrentMethodParameters();
            List<String> tempo = new ArrayList<>();
            List<String> tempo1 = new ArrayList<>();
            if (n.f4.present()) {
                n.f4.accept(this, id);
                for (String element : currentMethodParameters) {
                    inlining.append(vartype.get(element) + " ").append(currentClass).append("_").append(currentMethod)
                            .append("_fp_").append(element).append(";").append("\n");
                    // tempo.add(vartype.get(element) + " " + currentClass + "_" + currentMethod +
                    // "_fp_" + element + ";");
                }
                for (String element : currentMethodParameters) {
                    inlining2.append(currentClass).append("_" + currentMethod).append("_fp_").append(element)
                            .append(" " + "=" + " ").append(varvalue.get(element)).append(";").append("\n");
                    // tempo1.add(
                    // currentClass + "_" + currentMethod + "_fp_" + element + "=" +
                    // varvalue.get(element) + ";");
                }
                clearCurrentMethodParameters();
            }
            if (n.f7.present()) {
                n.f7.accept(this, id);
                for (String element : currentMethodParameters) {
                    // tempo1.add(vartype.get(element) + " " + currentMethod + "_" + element + ";");
                }
                clearCurrentMethodParameters();
            }
            if (n.f8.present()) {
                n.f8.accept(this, id);
                for (String element : currentMethodParameters) {
                    tempo.add(element + ";");
                }
                clearCurrentMethodParameters();
            }
            methodTable.put(currentMethod, new ArrayList<>(tempo));
            String ret = n.f10.f0.toString();
            super.visit(n, id);
            String returnvalue = varvalue.get(ret);
            tempo1.add(currentClass + "_" + currentMethod + "_" + ret + "=" + returnvalue);
            methodTable1.put(currentMethod, new ArrayList<>(tempo1));
            // System.out.print("return value " + currentMethod + returnvalue + "\n");
            methodvalue.put(currentMethod, returnvalue);
        }
        super.visit(n, id);
        return null;
    }

    @Override
    public String visit(FormalParameterList n, String s) {
        n.f0.accept(this, s);

        n.f1.accept(this, s);

        return null;
    }

    @Override
    public String visit(FormalParameter n, String s) {

        String identifier = n.f1.f0.toString();
        currentMethodParameters.add(identifier);
        return null;
    }

    @Override
    public String visit(FormalParameterRest n, String s) {
        n.f1.accept(this, s);
        return null;

    }

    private String extractFieldType(Type typeNode) {
        if (typeNode.f0.choice instanceof Identifier) {
            return ((Identifier) typeNode.f0.choice).f0.toString();
        }
        if (typeNode.f0.choice instanceof IntegerType) {
            return "int";
        }
        if (typeNode.f0.choice instanceof BooleanType) {
            return "Boolean";
        }
        if (typeNode.f0.choice instanceof ArrayType) {
            return "int[]";
        }
        return null;
    }

    private String expressionExtract(Expression exprType, String id) {
        if (exprType.f0.choice instanceof PlusExpression) {
            PlusExpression plusExpr = (PlusExpression) exprType.f0.choice;
            String leftid = varvalue.get(plusExpr.f0.f0.toString());
            String rightid = varvalue.get(plusExpr.f2.f0.toString());
            // System.out.println(
            // "plus " + varvalue.get(plusExpr.f0.f0.toString()) +
            // varvalue.get(plusExpr.f2.f0.toString()));
            int num1 = Integer.parseInt(leftid);
            int num2 = Integer.parseInt(rightid);
            int sum = num1 + num2;
            String exp = Integer.toString(sum);
            return exp;
        }
        if (exprType.f0.choice instanceof MinusExpression) {
            MinusExpression minExpr = (MinusExpression) exprType.f0.choice;
            String leftid = varvalue.get(minExpr.f0.f0.toString());
            String rightid = varvalue.get(minExpr.f2.f0.toString());
            int num1 = Integer.parseInt(leftid);
            int num2 = Integer.parseInt(rightid);
            int sum = num1 - num2;
            String exp = Integer.toString(sum);
            return exp;
        }
        if (exprType.f0.choice instanceof TimesExpression) {
            TimesExpression timesExpr = (TimesExpression) exprType.f0.choice;
            // String leftid = getConstantOrIdentifier(timesExpr.f0);
            // String exp = " * ";
            // String rightid = getConstantOrIdentifier(timesExpr.f2);
            // if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
            // int foldedValue = Integer.parseInt(leftid) * Integer.parseInt(rightid);
            // return String.valueOf(foldedValue);
            // }
            String leftid = varvalue.get(timesExpr.f0.f0.tokenImage);
            String rightid = varvalue.get(timesExpr.f2.f0.tokenImage);
            int num1 = Integer.parseInt(leftid);
            int num2 = Integer.parseInt(rightid);
            int sum = num1 * num2;
            String exp = Integer.toString(sum);
            return exp;
        }
        if (exprType.f0.choice instanceof DivExpression) {
            DivExpression divExpr = (DivExpression) exprType.f0.choice;
            // String leftid = getConstantOrIdentifier(divExpr.f0);
            // String exp = " / ";
            // String rightid = getConstantOrIdentifier(divExpr.f2);
            // if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
            // int foldedValue = Integer.parseInt(leftid) / Integer.parseInt(rightid);
            // return String.valueOf(foldedValue);
            // }
            String leftid = varvalue.get(divExpr.f0.f0.toString());
            String rightid = varvalue.get(divExpr.f2.f0.toString());
            int num1 = Integer.parseInt(leftid);
            int num2 = Integer.parseInt(rightid);
            int sum = num1 / num2;
            String exp = Integer.toString(sum);
            return exp;
        }
        if (exprType.f0.choice instanceof PrimaryExpression) {
            return extractPrimaryExpression((PrimaryExpression) exprType.f0.choice, id);
        }
        return "null";
    }

    private String extractPrimaryExpression(PrimaryExpression pExpr, String id) {

        if (pExpr.f0.choice instanceof IntegerLiteral) {
            IntegerLiteral integer = (IntegerLiteral) pExpr.f0.choice;
            varvalue1.put(id, integer.f0.tokenImage);
            // System.out.println("variable " + id + " " + integer.f0.tokenImage);
            return integer.f0.tokenImage;
        }
        if (pExpr.f0.choice instanceof TrueLiteral) {
            TrueLiteral T = (TrueLiteral) pExpr.f0.choice;
            return T.f0.tokenImage;
        }
        if (pExpr.f0.choice instanceof FalseLiteral) {
            FalseLiteral F = (FalseLiteral) pExpr.f0.choice;
            return F.f0.tokenImage;
        }
        if (pExpr.f0.choice instanceof Identifier) {
            Identifier ident = (Identifier) pExpr.f0.choice;
            String varName = ident.f0.tokenImage;
            // Map<String, LatticeValue> env = outMap.get(currentBB);
            // if (env != null && env.containsKey(varName)) {
            // LatticeValue lv = env.get(varName);
            // if (lv.getState() == LatticeValue.State.CONSTANT) {
            // return lv.getConstant().toString();
            // }
            // }
            return varName;
        }
        if (pExpr.f0.choice instanceof ThisExpression) {
            IntegerLiteral ths = (IntegerLiteral) pExpr.f0.choice;
            return ths.f0.tokenImage;
        }
        return "";
    }

    public void printMethodTable() {
        System.out.println("Method Table:");
        for (Map.Entry<String, List<String>> entry : methodTable.entrySet()) {
            System.out.println("methods: " + entry.getKey() + " ->value: " + entry.getValue());
        }
    }

    public void printMethodvalue() {
        System.out.println("Method Table:");
        for (Map.Entry<String, String> entry : methodvalue.entrySet()) {
            System.out.println("methods: " + entry.getKey() + " ->value: " + entry.getValue());
        }
    }
}
