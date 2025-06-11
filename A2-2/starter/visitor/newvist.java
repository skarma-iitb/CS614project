package visitor;

import java.util.Map;

import syntaxtree.AssignmentStatement;
import syntaxtree.MethodDeclaration;

public class newvist extends GJNoArguDepthFirst<Object> {
    private Map<String, Map<String, ConstantInfo>> methodVarMap;
    private String currentMethod;
    private int indentLevel = 0;

    public newvist(Map<String, Map<String, ConstantInfo>> methodVarMap) {
        this.methodVarMap = methodVarMap;
    }

    private void printIndent() {
        for (int i = 0; i < indentLevel; i++) {
            System.out.print("    ");
        }
    }

    public Object visit(MethodDeclaration n) {
        currentMethod = n.f2.f0.toString();
        printIndent();
        n.f1.accept(this);
        System.out.print(" " + n.f2.f0.toString() + "(");
        n.f4.accept(this);
        System.out.println(") {");
        indentLevel++;
        n.f8.accept(this);
        printIndent();
        System.out.print("return ");

        // Handle constant propagation in return statement
        String returnVar = n.f10.f0.toString();
        Map<String, ConstantInfo> varMap = methodVarMap.get(currentMethod);
        if (varMap != null && varMap.containsKey(returnVar) && varMap.get(returnVar).isConstant()) {
            System.out.print(varMap.get(returnVar).getValue());
        } else {
            n.f10.accept(this);
        }
        System.out.println(";");
        indentLevel--;
        printIndent();
        System.out.println("}");
        currentMethod = "";
        return null;
    }

    public Object visit(AssignmentStatement n) {
        printIndent();
        String varName = n.f0.f0.toString();
        System.out.print(varName + " = ");

        // Check if right-hand side can be constant-propagated
        Map<String, ConstantInfo> varMap = methodVarMap.get(currentMethod);
        if (varMap != null && varMap.containsKey(varName) && varMap.get(varName).isConstant()) {
            System.out.println(varMap.get(varName).getValue() + ";");
        } else {
            n.f2.accept(this);
            System.out.println(";");
        }
        return null;
    }

    // Add other necessary visit methods...
}
